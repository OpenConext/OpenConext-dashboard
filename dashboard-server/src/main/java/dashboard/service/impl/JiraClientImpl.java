/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dashboard.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dashboard.domain.Action;
import dashboard.domain.Action.Type;
import dashboard.domain.Change;
import dashboard.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

@SuppressWarnings("unchecked")
public class JiraClientImpl implements JiraClient {
    private static final Logger LOG = LoggerFactory.getLogger(JiraClientImpl.class);

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    private String baseUrl;
    private RestTemplate restTemplate;
    private String projectKey;
    private HttpHeaders defaultHeaders;
    private Map<String, Map<String, Map<String, String>>> mappings;
    private ObjectMapper objectMapper = new ObjectMapper();
    private String environment;

    public JiraClientImpl(String baseUrl, String username, String password, String projectKey) throws IOException {
        this.projectKey = projectKey;
        this.baseUrl = baseUrl;

        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
        byte[] encoded = Base64.encode((username + ":" + password).getBytes());
        this.defaultHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(encoded));
        this.restTemplate = new RestTemplate();
        this.environment = baseUrl.contains("test") ? "test" : "prod";
        this.mappings = objectMapper.readValue(new ClassPathResource("jira/mappings.json").getInputStream(), Map.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public String create(final Action action, List<Change> changes) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("priority", ImmutableMap.of("id", "3"));
        fields.put("project", ImmutableMap.of("key", projectKey));
        fields.put("customfield_" + spCustomField(), action.getSpId());
        fields.put("customfield_" + idpCustomField(), action.getIdpId());
        fields.put("issuetype", ImmutableMap.of("id", actionToIssueIdentifier(action.getType())));

        SummaryAndDescription summaryAndDescription = JiraTicketSummaryAndDescriptionBuilder.build(action, changes);
        fields.put("summary", summaryAndDescription.summary);
        fields.put("description", summaryAndDescription.description);

        Map<String, Object> issue = new HashMap<>();
        issue.put("fields", fields);

        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sending JSON {} to JIRA", objectMapper.writeValueAsString(issue));
            }
        } catch (JsonProcessingException e) {
            //ignore
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(issue, defaultHeaders);
        try {
            Map<String, String> result = restTemplate.postForObject(baseUrl + "/issue", entity, Map.class);
            return result.get("key");
        } catch (HttpClientErrorException e) {
            LOG.error("Failed to create Jira issue: {} ({}) with response:\n{}", e.getStatusCode(), e.getStatusText(), e
                    .getResponseBodyAsString());
            throw Throwables.propagate(e);
        }
    }

    @Override
    public Map<String, Object> getTasks(String idp, int startAt, int maxResults) {
        String query = buildQueryForIdp(idp);

        try {
            HttpEntity<Map<String, String>> entity = new HttpEntity<>(ImmutableMap.of("jql", query, "maxResults",
                    String.valueOf(maxResults)),
                    defaultHeaders);

            String url = baseUrl + "/search";
            Map<String, Object> result = restTemplate.postForObject(url, entity, Map.class);

            List<Action> issues = ((List<Map<String, Object>>) result.get("issues")).stream().map(issue -> {
                Map<String, Object> fields = (Map<String, Object>) issue.get("fields");

                String issueType = (String) ((Map<String, Object>) fields.get("issuetype")).get("id");

                return Action.builder()
                        .jiraKey((String) issue.get("key"))
                        .idpId(Optional.ofNullable((String) fields.get("customfield_" + idpCustomField())).orElse(""))
                        .spId(Optional.ofNullable((String) fields.get("customfield_" + spCustomField())).orElse(""))
                        .status((String) ((Map<String, Object>) fields.get("status")).get("name"))
                        .type(findType(issueType))
                        .requestDate(ZonedDateTime.parse((String) fields.get("created"), DATE_FORMATTER))
                        .body((String) fields.get("description")).build();
            }).collect(toList());
            Map<String, Object> answer = new HashMap<>();
            answer.put("issues", issues);
            answer.put("total", result.get("total"));
            answer.put("startAt", result.get("startAt"));
            answer.put("maxResults", result.get("maxResults"));
            return answer;

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                LOG.error("The Jira query \"{}\" was invalid:\n{}", query, e.getResponseBodyAsString());
            } else {
                LOG.error("Jira returned a {} ({}) for query {}:\n{}", e.getStatusCode(), e.getStatusText(), query, e
                        .getResponseBodyAsString());
            }
        } catch (RestClientException e) {
            LOG.error("Error communicating with Jira", e);
        }

        return Collections.emptyMap();
    }

    protected Action.Type findType(String issueType) {
        return this.mappings.get(this.environment).get("issueTypes").entrySet().stream()
                .filter(entry -> entry.getValue().equals(issueType))
                .map(entry -> Action.Type.valueOf(entry.getKey().toUpperCase()))
                .findFirst().orElseThrow(() -> new RuntimeException("No issue type for " + issueType));
    }

    protected String actionToIssueIdentifier(Action.Type actionType) {
        return this.mappings.get(this.environment).get("issueTypes").entrySet().stream()
                .filter(entry -> entry.getKey().equals(actionType.name().toLowerCase()))
                .map(entry -> entry.getValue())
                .findFirst().orElseThrow(() -> new RuntimeException("No action type for " + actionType));
    }


    protected String buildQueryForIdp(String idp) {
        String issueTypes = this.mappings.get(this.environment).get("issueTypes").values().stream().collect(joining(", "));
        return String.format("project = %s AND issueType IN (%s) AND cf[%s]~\"%s\" ORDER BY created DESC", projectKey,
                issueTypes, idpCustomField(), idp);
    }

    protected String buildQueryForIdp(String idp, String sp, Optional<List<String>> statuses) {
        String issueTypes = this.mappings.get(this.environment).get("issueTypes").values().stream().collect(joining(", "));
        String statusPart = statuses.map(s -> "AND status in (" + String.join("\",\"") + ")").orElse("");
        return String.format("project = %s AND issueType IN (%s) AND cf[%s]~\"%s\" AND cf[%s]~\"%s\" %s ORDER BY created DESC", projectKey,
                issueTypes, idpCustomField(), idp, spCustomField(), sp, statusPart);
    }

    protected String idpCustomField() {
        return this.mappings.get(this.environment).get("customFields").get("idpEntityId");
    }

    protected String spCustomField() {
        return this.mappings.get(this.environment).get("customFields").get("spEntityId");
    }

}
