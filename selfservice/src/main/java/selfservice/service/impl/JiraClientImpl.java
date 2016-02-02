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

import static java.util.stream.Collectors.toList;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import selfservice.domain.JiraTask;
import selfservice.domain.CoinUser;
import selfservice.service.impl.JiraTicketSummaryAndDescriptionBuilder.SummaryAndDescription;

public class JiraClientImpl implements JiraClient {
  private static final Logger LOG = LoggerFactory.getLogger(JiraClientImpl.class);

  private static final String SP_CUSTOM_FIELD = "customfield_10100";
  private static final String IDP_CUSTOM_FIELD = "customfield_10101";
  private static final String DEFAULT_SECURITY_LEVEL_ID = "10100";

  private static final Map<JiraTask.Type, String> TASKTYPE_TO_ISSUETYPE_CODE = ImmutableMap.of(
    JiraTask.Type.QUESTION, "16",
    JiraTask.Type.LINKREQUEST, "13",
    JiraTask.Type.UNLINKREQUEST, "17");

  private static final String PRIORITY_MEDIUM_ID = "3";

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
  public String create(final JiraTask task, final CoinUser user) {
    Map<String, Object> fields = new HashMap<>();
    fields.put("priority", ImmutableMap.of("id", PRIORITY_MEDIUM_ID));
    fields.put("project", ImmutableMap.of("key", projectKey));
    fields.put("security", ImmutableMap.of("id", DEFAULT_SECURITY_LEVEL_ID));
    fields.put(SP_CUSTOM_FIELD, task.getServiceProvider());
    fields.put(IDP_CUSTOM_FIELD, task.getIdentityProvider());
    fields.put("issuetype", ImmutableMap.of("id", TASKTYPE_TO_ISSUETYPE_CODE.get(task.getIssueType())));

    SummaryAndDescription summaryAndDescription = JiraTicketSummaryAndDescriptionBuilder.build(task, user);
    fields.put("summary", summaryAndDescription.summary);
    fields.put("description", summaryAndDescription.description);

    Map<String, Object> issue = new HashMap<>();
    issue.put("fields", fields);

    HttpEntity<Map<String, Object>> entity = new HttpEntity<>(issue, defaultHeaders);
    try {
      Map<String, String> result = restTemplate.postForObject(baseUrl + "/issue", entity, Map.class);
      return result.get("key");
    } catch (HttpClientErrorException e) {
      throw new RuntimeException(e.getResponseBodyAsString());
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public List<JiraTask> getTasks(final List<String> keys) {
    if (CollectionUtils.isEmpty(keys)) {
      return Collections.emptyList();
    }

    StringBuilder query = new StringBuilder("project = ");
    query.append(projectKey);
    query.append(" AND key IN (");
    Joiner.on(",").skipNulls().appendTo(query, keys);
    query.append(")");

    Map<String, String> searchArgs = ImmutableMap.of("jql", query.toString());

    try {
      HttpEntity<Map<String, String>> entity = new HttpEntity<>(searchArgs, defaultHeaders);
      Map<String, Object> result = restTemplate.postForObject(baseUrl + "/search?expand=all", entity, Map.class);

      List<Map<String, Object>> issues = (List<Map<String, Object>>) result.get("issues");
      return issues.stream().map(issue -> {
          Map<String, Object> fields = (Map<String, Object>) issue.get("fields");
          Map<String, Object> statusInfo = (Map<String, Object>) fields.get("status");

          return new JiraTask.Builder()
              .key((String) issue.get("key"))
              .identityProvider(Optional.ofNullable((String) fields.get(IDP_CUSTOM_FIELD)).orElse(""))
              .serviceProvider(Optional.ofNullable((String) fields.get(SP_CUSTOM_FIELD)).orElse(""))
              .institution("???")
              .status(JiraTask.Status.valueOf(((String) statusInfo.get("name")).toUpperCase()))
              .body((String) fields.get("description")).build();
        }).collect(toList());
    } catch (HttpStatusCodeException e) {
      if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
        LOG.error("The Jira query \"{}\" was invalid:\n{}", query.toString(), e.getResponseBodyAsString());
      } else {
        LOG.error("Jira returned a {} ({}) for query {}:\n{}", e.getStatusCode(), e.getStatusText(), query.toString(), e.getResponseBodyAsString());
      }
    } catch (RestClientException e) {
      LOG.error("Error communicating with Jira", e);
    }

    return Collections.emptyList();
  }

}
