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
import dashboard.domain.Action;
import dashboard.domain.Action.Type;
import dashboard.domain.Change;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;
import dashboard.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Collections.*;
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
    private int dueDateWeeks;

    public JiraClientImpl(String baseUrl, String username, String password, String projectKey, int dueDateWeeks) throws IOException {
        this.projectKey = projectKey;
        this.baseUrl = baseUrl;
        this.dueDateWeeks = dueDateWeeks;

        this.defaultHeaders = new HttpHeaders();
        this.defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
        byte[] encoded = Base64.encode((username + ":" + password).getBytes());
        this.defaultHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(encoded));
        this.restTemplate = new RestTemplate();
        this.environment = baseUrl.contains("test") ? "test" : "prod";
        this.mappings = objectMapper.readValue(new ClassPathResource("jira/mappings.json").getInputStream(), Map.class);

        this.standardFields = new ArrayList(Arrays.asList("summary", "resolution", "status", "assignee", "issuetype",
                "created", "description", "updated", "comment"));
        standardFields.addAll(this.mappings.get(this.environment).get("customFields").values().stream().map(s -> "customfield_" + s).collect(toList()));
    }

    @Override
    @SuppressWarnings("unchecked")
    public String create(final Action action, List<Change> changes) {
        Map<String, Object> fields = new HashMap<>();
        fields.put("priority", ImmutableMap.of("id", "3"));
        fields.put("project", ImmutableMap.of("key", projectKey));
        fields.put("customfield_" + spCustomField(), action.getSpId());
        if (StringUtils.hasText(action.getPersonalMessage())) {
            fields.put("customfield_" + this.optionalMessageCustomField(), action.getPersonalMessage());
        }
        String typeMetaData = action.getTypeMetaData();
        if (StringUtils.hasText(typeMetaData)) {
            fields.put("customfield_" + typeMetaDataCustomField(), ImmutableMap.of("value", action.getTypeMetaData()));
        }
        fields.put("customfield_" + idpCustomField(), action.getIdpId());
        if (action.getType().equals(Type.LINKINVITE)) {
            fields.put("customfield_" + emailToCustomField(), action.getEmailTo());
        }
        fields.put("issuetype", ImmutableMap.of("id", actionToIssueIdentifier(action.getType())));

        SummaryAndDescription summaryAndDescription = JiraTicketSummaryAndDescriptionBuilder.build(action, changes);
        fields.put("summary", summaryAndDescription.summary);
        fields.put("description", summaryAndDescription.description);
        fields.put("duedate", dueDate());

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
        Map<String, Object> result = new HashMap<>();
        try {
            ImmutableMap<String, Object> body = ImmutableMap.of(
                    "jql", query,
                    "maxResults", jiraFilter.getMaxResults(),
                    "startAt", jiraFilter.getStartAt(),
                    "fields", this.standardFields);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, defaultHeaders);

            String url = baseUrl + "/search";
            result = restTemplate.postForObject(url, entity, Map.class);
            List<Action> issues = ((List<Map<String, Object>>) result.get("issues")).stream().map(issue -> {
                Map<String, Object> fields = (Map<String, Object>) issue.get("fields");

                boolean rejected = ((List) ((Map) fields.getOrDefault("comment", Collections.EMPTY_MAP)).getOrDefault(
                        "comments", Collections.emptyList()))
                        .stream()
                        .filter(o -> ((String) ((Map) o).getOrDefault("body", "")).contains("rejected"))
                        .findAny().isPresent();

                String issueType = (String) ((Map<String, Object>) fields.get("issuetype")).get("id");
                Map<String, String> resolution = (Map<String, String>) fields.get("resolution");
                String typeMetaData = Optional.ofNullable((Map<String, String>) fields.get("customfield_" + typeMetaDataCustomField())).orElse(emptyMap()).getOrDefault("value", "");

                return Action.builder()
                        .jiraKey((String) issue.get("key"))
                        .idpId(Optional.ofNullable((String) fields.get("customfield_" + idpCustomField())).orElse(""))
                        .spId(Optional.ofNullable((String) fields.get("customfield_" + spCustomField())).orElse(""))
                        .typeMetaData(typeMetaData)
                        .emailTo(Optional.ofNullable((String) fields.get("customfield_" + emailToCustomField())).orElse(""))
                        .status((String) ((Map<String, Object>) fields.get("status")).get("name"))
                        .resolution(resolution != null ? resolution.get("name") : null)
                        .type(findType(issueType))
                        .personalMessage(Optional.ofNullable((String) fields.get("customfield_" + optionalMessageCustomField())).orElse(""))
                        .requestDate(ZonedDateTime.parse((String) fields.get("created"), DATE_FORMATTER))
                        .updateDate(ZonedDateTime.parse((String) fields.get("updated"), DATE_FORMATTER))
                        .body((String) fields.get("description"))
                        .rejected(rejected)
                        .build();
            }).collect(toList());
            return new JiraResponse(issues, (Integer) result.get("total"), (Integer) result.get("startAt"), (Integer) result.get("maxResults"));

        } catch (HttpStatusCodeException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                LOG.error("The Jira query \"{}\" was invalid:\n{}", query, e.getResponseBodyAsString());
            } else {
                LOG.error("Jira returned a {} ({}) for query {}:\n{}", e.getStatusCode(), e.getStatusText(), query, e
                        .getResponseBodyAsString());
            }
        } catch (Exception e) {
            LOG.error(String.format("Error communicating with Jira: %s", result), e);
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
    public void comment(String key, String comment) {
        String commentUrl = baseUrl + "/issue/" + key + "/comment";
        HttpEntity<Object> commentRequestEntity = new HttpEntity<>(ImmutableMap.of("body", comment), defaultHeaders);
        restTemplate.exchange(commentUrl, HttpMethod.POST, commentRequestEntity, Map.class);
    }

    //    @Override
//    public void attachments(String key, String... attachments) {
//        String url = baseUrl + "/issue/" + key + "/attachments";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
//        headers.set(HttpHeaders.AUTHORIZATION, defaultHeaders.get(HttpHeaders.AUTHORIZATION).get(0));
//
//        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
//        Stream.of(attachments).forEach(attachment -> map.add("file", new ByteArrayResource(attachment.getBytes())));
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(map, headers);
//        restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//    }
//
    @Override
    public void transition(String key, String transitionId, Optional<String> resolutionOptional, Optional<String> commentOptional) {
        String url = baseUrl + "/issue/" + key + "/transitions";
        final Map<String, Map<String, Object>> body = new HashMap<>();
        body.put("transition", singletonMap("id", transitionId));
        commentOptional.ifPresent(comment -> {
            //{ "update": {"comment": [ { "add": {"body": "Testing."} }]},"fields": {}, "transition": { "id": "21" }}
            body.put("update", singletonMap("comment", singletonList(singletonMap("add", singletonMap("body", comment)))));
        });
        resolutionOptional.ifPresent(resolution -> {
            body.put("fields", singletonMap("resolution", singletonMap("name", resolution)));
        });
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders);
        restTemplate.exchange(url, HttpMethod.POST, requestEntity, Map.class);
    }

    @Override
    public void updateOptionalMessage(String jiraKey, String optionalMessage) {
        String url = baseUrl + "/issue/" + jiraKey;
        final Map<String, Map<String, Object>> body = new HashMap<>();
        body.put("fields", singletonMap("customfield_" + this.optionalMessageCustomField(), optionalMessage));
        HttpEntity<Object> requestEntity = new HttpEntity<>(body, defaultHeaders);
        ResponseEntity<Map> exchange = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, Map.class);
        System.out.println(exchange);
    }

    String buildQueryForIdp(String idp, JiraFilter jiraFilter) {
        String escapedIdpField = idp.indexOf("?") > -1 ? idp.substring(0, idp.indexOf("?")) : idp;
        StringBuilder sb = new StringBuilder(String.format("project = %s AND cf[%s]~\"%s\"", projectKey, idpCustomField(), escapedIdpField));
        List<String> statuses = jiraFilter.getStatuses();
        if (!CollectionUtils.isEmpty(statuses)) {
            sb.append(String.format(" AND status in (%s)", statuses.stream().map(status -> "\"" + status + "\"").collect(joining(", "))));
        }
        List<Type> types = jiraFilter.getTypes();
        if (!CollectionUtils.isEmpty(types)) {
            String issueTypes = types.stream().map(type -> this.mappings.get(this.environment).get("issueTypes").get(type.name().toLowerCase())).collect(joining(", "));
            sb.append(String.format(" AND issueType in (%s)", issueTypes));
        }
        String spEntityId = jiraFilter.getSpEntityId();
        if (StringUtils.hasText(spEntityId)) {
            String escapedSpEntityId = spEntityId.indexOf("?") > -1 ? spEntityId.substring(0, spEntityId.indexOf("?")) : spEntityId;
            sb.append(String.format(" AND cf[%s]~\"%s\"", spCustomField(), escapedSpEntityId));
        }
        if (StringUtils.hasText(jiraFilter.getKey())) {
            sb.append(" AND key = \"" + jiraFilter.getKey() + "\"");
        }
        if (jiraFilter.getFrom() != null) {
            String from = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(jiraFilter.getFrom()));
            sb.append(" AND created >= \"" + from + "\"");
        }
        if (jiraFilter.getTo() != null) {
            String to = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneId.systemDefault()).format(Instant.ofEpochSecond(jiraFilter.getTo()));
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
        return this.customField("idpEntityId");
    }

    private String spCustomField() {
        return this.customField("spEntityId");
    }

    private String typeMetaDataCustomField() {
        return this.customField("typeMetaData");
    }

    private String emailToCustomField() {
        return this.customField("emailTo");
    }

    private String optionalMessageCustomField() {
        return this.customField("optionalMessage");
    }

    private String customField(String name) {
        return this.mappings.get(this.environment).get("customFields").get(name);
    }

    private String dueDate() {
        LocalDate localDate = new Date().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return localDate.plusWeeks(this.dueDateWeeks).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

}
