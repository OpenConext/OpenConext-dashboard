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
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import dashboard.domain.Action;
import dashboard.domain.Action.Type;
import dashboard.domain.Change;
import dashboard.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

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
    private ArrayList standardFields;

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

        this.standardFields = new ArrayList(Arrays.asList("summary", "resolution", "status", "assignee", "issuetype", "created", "description", "updated"));
        standardFields.addAll(this.mappings.get(this.environment).get("customFields").values().stream().map(s -> "customfield_" + s).collect(toList()));

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
    public JiraResponse searchTasks(String idp, JiraFilter jiraFilter) {
        String query = buildQueryForIdp(idp, jiraFilter);
        try {
            ImmutableMap<String, Object> body = ImmutableMap.of(
                    "jql", query,
                    "maxResults", jiraFilter.getMaxResults(),
                    "startAt", jiraFilter.getStartAt(),
                    "fields", this.standardFields);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders);

            String url = baseUrl + "/search";
            Map<String, Object> result = restTemplate.postForObject(url, entity, Map.class);

            List<Action> issues = ((List<Map<String, Object>>) result.get("issues")).stream().map(issue -> {
                Map<String, Object> fields = (Map<String, Object>) issue.get("fields");
                String issueType = (String) ((Map<String, Object>) fields.get("issuetype")).get("id");
                Map<String, String> resolution = (Map<String, String>) fields.get("resolution");
                return Action.builder()
                        .jiraKey((String) issue.get("key"))
                        .idpId(Optional.ofNullable((String) fields.get("customfield_" + idpCustomField())).orElse(""))
                        .spId(Optional.ofNullable((String) fields.get("customfield_" + spCustomField())).orElse(""))
                        .status((String) ((Map<String, Object>) fields.get("status")).get("name"))
                        .resolution(resolution != null ? resolution.get("name") : null)
                        .type(findType(issueType))
                        .requestDate(ZonedDateTime.parse((String) fields.get("created"), DATE_FORMATTER))
                        .updateDate(ZonedDateTime.parse((String) fields.get("updated"), DATE_FORMATTER))
                        .body((String) fields.get("description")).build();
            }).collect(toList());
            return new JiraResponse(issues, (Integer) result.get("total"), (Integer) result.get("startAt"), (Integer) result.get("maxResults"));

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
        return new JiraResponse(new ArrayList<>(), 0, jiraFilter.getStartAt(), jiraFilter.getMaxResults());
    }

    Action.Type findType(String issueType) {
        return this.mappings.get(this.environment).get("issueTypes").entrySet().stream()
                .filter(entry -> entry.getValue().equals(issueType))
                .map(entry -> Action.Type.valueOf(entry.getKey().toUpperCase()))
                .findFirst().orElseThrow(() -> new RuntimeException("No issue type for " + issueType));
    }

    String actionToIssueIdentifier(Action.Type actionType) {
        return this.mappings.get(this.environment).get("issueTypes").entrySet().stream()
                .filter(entry -> entry.getKey().equals(actionType.name().toLowerCase()))
                .map(entry -> entry.getValue())
                .findFirst().orElseThrow(() -> new RuntimeException("No action type for " + actionType));
    }

    @Override
    public Map<String, String> validTransitions(String key) {
        String url = baseUrl + "/issue/" + key + "/transitions";
        Map body = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(defaultHeaders), Map.class).getBody();
        List<Map<String, Object>> transitions = (List) body.getOrDefault("transitions", new ArrayList<>());
        return transitions.stream().collect(Collectors.toMap(map -> String.class.cast(map.get("name")), map -> String.class.cast(map.get("id"))));
    }

    @Override
    public void transition(String key, String transitionId, Optional<String> resolutionOptional, Optional<String> commentOptional) {
        String url = baseUrl + "/issue/" + key + "/transitions";
        final Map<String, Map<String, Object>> body = new HashMap<>();
        body.put("transition", Collections.singletonMap("id", transitionId));
        resolutionOptional.ifPresent(resolution -> {
            body.put("fields", Collections.singletonMap("resolution", Collections.singletonMap("name", resolution)));
        });
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
        commentOptional.ifPresent(comment -> {
            String commentUrl = baseUrl + "/issue/" + key + "/comment";
            HttpEntity<Object> commentRequestEntity = new HttpEntity<>(ImmutableMap.of("body", comment), defaultHeaders);
            restTemplate.exchange(commentUrl, HttpMethod.POST, commentRequestEntity, Map.class);
        });
    }

    String buildQueryForIdp(String idp, JiraFilter jiraFilter) {
        StringBuilder sb = new StringBuilder(String.format("project = %s AND cf[%s]~\"%s\"", projectKey, idpCustomField(), idp));
        List<String> statuses = jiraFilter.getStatuses();
        if (!CollectionUtils.isEmpty(statuses)) {
            sb.append(String.format(" AND status in (%s)", statuses.stream().map(status -> "\"" + status + "\"").collect(joining(", "))));
        }
        List<Type> types = jiraFilter.getTypes();
        if (!CollectionUtils.isEmpty(types)) {
            String issueTypes = types.stream().map(type -> this.mappings.get(this.environment).get("issueTypes").get(type.name().toLowerCase())).collect(joining(", "));
            sb.append(String.format(" AND issueType in (%s)", issueTypes));
        }
        if (StringUtils.hasText(jiraFilter.getSpEntityId())) {
            sb.append(String.format(" AND cf[%s]~\"%s\"", spCustomField(), jiraFilter.getSpEntityId()));
        }
        if (jiraFilter.getFrom() != null) {
            String from = DateTimeFormatter.ofPattern("YYYY-MM-dd").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(jiraFilter.getFrom()));
            sb.append(" AND created >= \"" + from + "\"");
        }
        if (jiraFilter.getTo() != null) {
            String to = DateTimeFormatter.ofPattern("YYYY-MM-dd").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(jiraFilter.getTo()));
            sb.append(" AND created <= \"" + to + "\"");
        }
        if (StringUtils.hasText(jiraFilter.getSortBy())) {
            sb.append(" ORDER BY ");
            switch (jiraFilter.getSortBy()) {
                case "requestDate":
                    sb.append("created ");
                    break;
                case "updateDate":
                    sb.append("updated ");
                    break;
                case "spName":
                    sb.append("cf[" + spCustomField() + "]");
                    break;
                case "type":
                    sb.append("issueType");
                    break;
                case "jiraKey":
                    sb.append("key");
                    break;
                case "status":
                    sb.append("status");
                    break;
            }
            sb.append(jiraFilter.isSortAsc() ? " ASC" : " DESC");
        }
        return sb.toString();
    }

    private String idpCustomField() {
        return this.mappings.get(this.environment).get("customFields").get("idpEntityId");
    }

    private String spCustomField() {
        return this.mappings.get(this.environment).get("customFields").get("spEntityId");
    }

}
