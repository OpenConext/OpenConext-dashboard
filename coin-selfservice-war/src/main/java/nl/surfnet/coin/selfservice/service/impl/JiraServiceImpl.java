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

package nl.surfnet.coin.selfservice.service.impl;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import com.google.common.base.Joiner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Required;
import org.swift.common.soap.jira.JiraSoapService;
import org.swift.common.soap.jira.JiraSoapServiceServiceLocator;
import org.swift.common.soap.jira.RemoteCustomFieldValue;
import org.swift.common.soap.jira.RemoteFieldValue;
import org.swift.common.soap.jira.RemoteIssue;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.JiraService;

public class JiraServiceImpl implements JiraService, InitializingBean {

  private static Logger log = LoggerFactory.getLogger(JiraServiceImpl.class);

  private static String ENDPOINT = "/rpc/soap/jirasoapservice-v2";

  public static final RemoteFieldValue[] EMPTY_REMOTE_FIELD_VALUES = new RemoteFieldValue[0];
  public static final String[] EMPTY_STRINGS = new String[0];
  public static final RemoteCustomFieldValue[] EMPTY_REMOTE_CUSTOM_FIELD_VALUES = new RemoteCustomFieldValue[0];
  public static final String SP_CUSTOM_FIELD = "customfield_10100";
  public static final String IDP_CUSTOM_FIELD = "customfield_10101";

  public static final String TYPE_LINKREQUEST = "13";
  public static final String TYPE_UNLINKREQUEST = "TODO"; // TODO: get from jira
  public static final String TYPE_QUESTION = "16";

  public static final String PRIORITY_MEDIUM = "3";
  public static final String CLOSE_ACTION_IDENTIFIER = "2";
  public static final String REOPEN_ACTION_IDENTIFIER = "3";

  private String baseUrl;
  private String username;
  private String password;

  private JiraSoapService jiraSoapService;
  private String projectKey;

  public JiraServiceImpl() {

  }

  public String create(final JiraTask task, CoinUser user) throws IOException {
    RemoteIssue remoteIssue;
    switch (task.getIssueType()) {
      case LINKREQUEST:
      case UNLINKREQUEST:
        remoteIssue = createRequest(task, user);
        break;
      default:
        remoteIssue = createQuestion(task, user);
        break;
    }
    final RemoteIssue createdIssue = jiraSoapService.createIssue(getToken(), remoteIssue);
    if (createdIssue == null) {
      return null;
    }
    return createdIssue.getKey();
  }

  private RemoteIssue createQuestion(final JiraTask task, CoinUser user) {
    RemoteIssue remoteIssue = new RemoteIssue();
    remoteIssue.setType(TYPE_QUESTION);
    remoteIssue.setSummary(new StringBuilder()
            .append("Question about ").append(task.getServiceProvider()).toString());
    remoteIssue.setProject(projectKey);
    remoteIssue.setPriority(PRIORITY_MEDIUM);
    StringBuilder description = new StringBuilder();
    description.append("Applicant name: ").append(user.getDisplayName()).append("\n");
    description.append("Applicant email: ").append(user.getEmail()).append("\n");
    description.append("Identity Provider: ").append(task.getIdentityProvider()).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");
    description.append("Time: ").append(new SimpleDateFormat("HH:mm dd-MM-yy").format(new Date())).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");
    description.append("Request: ").append(task.getBody()).append("\n");
    remoteIssue.setDescription(description.toString());
    appendSpAndIdp(task, remoteIssue);
    return remoteIssue;
  }

  private String getIssueTypeByJiraTaskType(JiraTask.Type t) {
    switch (t) {
      case QUESTION:
        return TYPE_QUESTION;
      case LINKREQUEST:
        return TYPE_LINKREQUEST;
      case UNLINKREQUEST:
        return TYPE_UNLINKREQUEST;
      default:
        throw new IllegalStateException("Unknown type: " + t);
    }
  }

  private RemoteIssue createRequest(final JiraTask task, CoinUser user) {
    RemoteIssue remoteIssue = new RemoteIssue();
    remoteIssue.setType(getIssueTypeByJiraTaskType(task.getIssueType()));
    remoteIssue.setSummary(new StringBuilder()
        .append("New connection for IdP ").append(task.getIdentityProvider())
        .append(" to SP ").append(task.getServiceProvider()).toString());
    remoteIssue.setProject(projectKey);
    remoteIssue.setPriority(PRIORITY_MEDIUM);
    StringBuilder description = new StringBuilder();
    if (task.getIssueType() == JiraTask.Type.LINKREQUEST) {
      description.append("Request: Create a new connection").append("\n");
    } else {
      description.append("Request: terminate a connection").append("\n");
    }

    description.append("Applicant name: ").append(user.getDisplayName()).append("\n");
    description.append("Applicant email: ").append(user.getEmail()).append("\n");
    description.append("Identity Provider: ").append(task.getIdentityProvider()).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");
    description.append("Time: ").append(new SimpleDateFormat("HH:mm dd-MM-yy").format(new Date())).append("\n");
    description.append("Service Provider: ").append(task.getServiceProvider()).append("\n");
    description.append("Remark from user: ").append(task.getBody()).append("\n");
    remoteIssue.setDescription(description.toString());
    appendSpAndIdp(task, remoteIssue);
    return remoteIssue;
  }

  private void appendSpAndIdp(final JiraTask task, final RemoteIssue remoteIssue) {
    final List<RemoteCustomFieldValue> customFieldValues = new ArrayList<RemoteCustomFieldValue>();
    final List<String> spValue = Collections.singletonList(task.getServiceProvider());
    final List<String> idpValue = Collections.singletonList(task.getIdentityProvider());
    customFieldValues.add(new RemoteCustomFieldValue(SP_CUSTOM_FIELD, null, spValue.toArray(EMPTY_STRINGS)));
    customFieldValues.add(new RemoteCustomFieldValue(IDP_CUSTOM_FIELD, null, idpValue.toArray(EMPTY_STRINGS)));
    remoteIssue.setCustomFieldValues(customFieldValues.toArray(EMPTY_REMOTE_CUSTOM_FIELD_VALUES));
  }

  public void delete(final String key) throws IOException {
    jiraSoapService.deleteIssue(getToken(), key);
  }

  public void doAction(String key, JiraTask.Action update) throws IOException {
    String action;
    switch (update) {
      case CLOSE:
        action = CLOSE_ACTION_IDENTIFIER;
        break;
      case REOPEN:
        action = REOPEN_ACTION_IDENTIFIER;
        break;
      default:
        throw new IllegalArgumentException("Action must be either close or reopen");
    }
    jiraSoapService.progressWorkflowAction(getToken(), key, action, Collections.emptyList().toArray(EMPTY_REMOTE_FIELD_VALUES));
  }

  public List<JiraTask> getTasks(final List<String> keys) throws IOException {
    if (keys == null || keys.size() == 0) {
      return Collections.emptyList();
    }
    List<JiraTask> jiraTasks = new ArrayList<JiraTask>();
    StringBuilder query = new StringBuilder("project = ");
    query.append(projectKey);
    query.append(" AND key IN(");
    Joiner.on(",").skipNulls().appendTo(query, keys);
    query.append(")");
    final RemoteIssue[] issuesFromJqlSearch = jiraSoapService.getIssuesFromJqlSearch(getToken(), query.toString(), 1000);
    for (RemoteIssue remoteIssue : issuesFromJqlSearch) {
      String identityProvider = fetchValue(IDP_CUSTOM_FIELD, remoteIssue.getCustomFieldValues());
      String serviceProvider = fetchValue(SP_CUSTOM_FIELD, remoteIssue.getCustomFieldValues());
      final JiraTask jiraTask = new JiraTask.Builder()
          .key(remoteIssue.getKey())
          .identityProvider(identityProvider)
          .serviceProvider(serviceProvider)
          .institution("???")
          .status(fetchStatus(remoteIssue))
          .body(remoteIssue.getDescription())
          .build();
      jiraTasks.add(jiraTask);
    }
    return jiraTasks;
  }

  private JiraTask.Status fetchStatus(final RemoteIssue remoteIssue) {
    if ("6" .equals(remoteIssue.getStatus())) {
      return JiraTask.Status.CLOSED;
    }
    if ("1" .equals(remoteIssue.getStatus())) {
      return JiraTask.Status.OPEN;
    }
    throw new IllegalStateException("Unknown JiraTask Status");
  }

  private String fetchValue(final String name, final RemoteCustomFieldValue[] customFieldValues) {
    for (RemoteCustomFieldValue customFieldValue : customFieldValues) {
      if (name.equals(customFieldValue.getCustomfieldId())) {
        return customFieldValue.getValues()[0];
      }
    }
    return "";
  }

  private String getToken() throws IOException {
    return jiraSoapService.login(username, password);
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    JiraSoapServiceServiceLocator jiraSoapServiceGetter = new JiraSoapServiceServiceLocator() {{
      setJirasoapserviceV2EndpointAddress(baseUrl + ENDPOINT);
      setMaintainSession(true);
    }};
    jiraSoapService = jiraSoapServiceGetter.getJirasoapserviceV2();
  }

  /**
   * meant to override the default impl (for instance to mock for unit tests)
   * @param jiraSoapService the soap service
   */
  protected void setJiraSoapService(JiraSoapService jiraSoapService) {
    this.jiraSoapService = jiraSoapService;
  }

  @Override
  @Required
  public void setBaseUrl(final String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @Override
  @Required
  public void setUsername(final String username) {
    this.username = username;
  }

  @Override
  @Required
  public void setPassword(final String password) {
    this.password = password;
  }

  @Override
  @Required
  public void setProjectKey(String projectKey) {
    this.projectKey = projectKey;
  }
}
