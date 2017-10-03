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
package selfservice.service.impl;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import selfservice.domain.Action;
import selfservice.domain.Action.Type;
import selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

public class JiraClientImpl implements JiraClient {
  private static final Logger LOG = LoggerFactory.getLogger(JiraClientImpl.class);

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

  private static final String SP_CUSTOM_FIELD = "13018 ";
  private static final String IDP_CUSTOM_FIELD = "13012 ";
  private static final String DEFAULT_SECURITY_LEVEL_ID = "10100";
  private static final String PRIORITY_MEDIUM_ID = "3";

  private static final Map<Action.Type, String> TASKTYPE_TO_ISSUETYPE_CODE = ImmutableMap.of(
    Type.QUESTION, "11103",
    Type.LINKREQUEST, "11104",
    Type.UNLINKREQUEST, "11105",
    Type.CHANGE, "11106");

  private final String baseUrl;
  private final RestTemplate restTemplate;
  private final String projectKey;
  private final HttpHeaders defaultHeaders;

  public JiraClientImpl(final String baseUrl, final String username, final String password, final String projectKey) {
    this.projectKey = projectKey;
    this.baseUrl = baseUrl;

    this.defaultHeaders = new HttpHeaders();
    this.defaultHeaders.setContentType(MediaType.APPLICATION_JSON);
    byte[] encoded = Base64.encode((username + ":" + password).getBytes());
    this.defaultHeaders.add(HttpHeaders.AUTHORIZATION, "Basic " + new String(encoded));
    this.restTemplate = new RestTemplate();
  }

  @Override
  @SuppressWarnings("unchecked")
  public String create(final Action action) {
    Map<String, Object> fields = new HashMap<>();
    fields.put("priority", ImmutableMap.of("id", PRIORITY_MEDIUM_ID));
    fields.put("project", ImmutableMap.of("key", projectKey));
    fields.put("security", ImmutableMap.of("id", DEFAULT_SECURITY_LEVEL_ID));
    fields.put("customfield_" + SP_CUSTOM_FIELD, action.getSpId());
    fields.put("customfield_" + IDP_CUSTOM_FIELD, action.getIdpId());
    fields.put("issuetype", ImmutableMap.of("id", TASKTYPE_TO_ISSUETYPE_CODE.get(action.getType())));

    SummaryAndDescription summaryAndDescription = JiraTicketSummaryAndDescriptionBuilder.build(action);
    fields.put("summary", summaryAndDescription.summary);
    fields.put("description", summaryAndDescription.description);

    Map<String, Object> issue = new HashMap<>();
    issue.put("fields", fields);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(issue, defaultHeaders);
    try {
      Map<String, String> result = restTemplate.postForObject(baseUrl + "/issue", entity, Map.class);
      return result.get("key");
    } catch (HttpClientErrorException e) {
      LOG.error("Failed to create Jira issue: {} ({}) with response:\n{}", e.getStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
      throw Throwables.propagate(e);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<Action> getTasks(String idp) {
    String query = buildQueryForIdp(idp, TASKTYPE_TO_ISSUETYPE_CODE.values());

    try {
      HttpEntity<Map<String, String>> entity = new HttpEntity<>(ImmutableMap.of("jql", query, "maxResults", "100"), defaultHeaders);

      Map<String, Object> result = restTemplate.postForObject(baseUrl + "/search?expand=all", entity, Map.class);

      return ((List<Map<String, Object>>) result.get("issues")).stream().map(issue -> {
          Map<String, Object> fields = (Map<String, Object>) issue.get("fields");

          String issueType = (String) ((Map<String, Object>) fields.get("issuetype")).get("id");

          return Action.builder()
              .jiraKey((String) issue.get("key"))
              .idpId(Optional.ofNullable((String) fields.get("customfield_" + IDP_CUSTOM_FIELD)).orElse(""))
              .spId(Optional.ofNullable((String) fields.get("customfield_" + SP_CUSTOM_FIELD)).orElse(""))
              .status((String) ((Map<String, Object>) fields.get("status")).get("name"))
              .type(findType(issueType))
              .requestDate(ZonedDateTime.parse((String) fields.get("created"), DATE_FORMATTER))
              .body((String) fields.get("description")).build();
        }).collect(toList());

    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
        LOG.error("The Jira query \"{}\" was invalid:\n{}", query, e.getResponseBodyAsString());
      } else {
        LOG.error("Jira returned a {} ({}) for query {}:\n{}", e.getStatusCode(), e.getStatusText(), query, e.getResponseBodyAsString());
      }
    } catch (RestClientException e) {
      LOG.error("Error communicating with Jira", e);
    }

    return Collections.emptyList();
  }

  private Action.Type findType(String issueType) {
    Action.Type type = TASKTYPE_TO_ISSUETYPE_CODE.entrySet().stream()
        .filter(entry -> entry.getValue().equals(issueType))
        .map(Map.Entry::getKey)
        .findFirst().orElseThrow(() -> new RuntimeException("no issue type for " + issueType));
    return type;
  }

  private String buildQueryForIdp(String idp, Collection<String> issueTypeIds) {
    return String.format("project = %s AND issueType IN (%s) AND cf[%s]~\"%s\" ORDER BY created DESC", projectKey, issueTypeIds.stream().collect(joining(", ")), IDP_CUSTOM_FIELD, idp);
  }

}
