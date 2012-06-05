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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.JiraService;

public class JiraServiceMock implements JiraService {

  private static final Logger LOG = LoggerFactory.getLogger(JiraServiceMock.class);
  private Map<String, JiraTask> repository;

  private int counter = 0;

  public JiraServiceMock() {
    repository = new HashMap<String, JiraTask>();
  }

  @Override
  public String create(final JiraTask task) throws IOException {
    String key = generateKey();
    repository.put(key, new JiraTask.Builder()
            .key(key)
            .identityProvider(task.getIdentityProvider())
            .serviceProvider(task.getServiceProvider())
            .institution(task.getInstitution())
            .issueType(task.getIssueType())
            .body(task.getBody())
            .status(JiraTask.Status.OPEN)
            .build());
    LOG.debug("Added task (key '{}') to repository: {}", key, task);
    return key;
  }

  private String generateKey() {
    return "TASK-" + counter++;
  }

  @Override
  public void delete(final String key) throws IOException {
    repository.remove(key);
  }

  @Override
  public void doAction(final String key, final JiraTask.Action action) throws IOException {
    JiraTask jiraTask = repository.get(key);
    JiraTask newTask;
    switch (action) {
      case CLOSE:
        newTask = new JiraTask.Builder()
            .key(jiraTask.getKey())
            .identityProvider(jiraTask.getIdentityProvider())
            .serviceProvider(jiraTask.getServiceProvider())
            .institution(jiraTask.getInstitution())
            .issueType(jiraTask.getIssueType())
            .body(jiraTask.getBody())
            .status(JiraTask.Status.CLOSED)
            .build();
        break;
      case REOPEN:
      default:
        newTask = new JiraTask.Builder()
            .key(jiraTask.getKey())
            .identityProvider(jiraTask.getIdentityProvider())
            .serviceProvider(jiraTask.getServiceProvider())
            .institution(jiraTask.getInstitution())
            .issueType(jiraTask.getIssueType())
            .body(jiraTask.getBody())
            .status(JiraTask.Status.OPEN)
            .build();
        break;
    }
    repository.remove(key);
    repository.put(key, newTask);
  }

  @Override
  public List<JiraTask> getTasks(final List<String> keys) throws IOException {
    List<JiraTask> tasks = new ArrayList<JiraTask>();
    for (String key : keys) {
      tasks.add(repository.get(key));
    }
    return tasks;
  }

  @Override
  public void setBaseUrl(String baseUrl) {
  }

  @Override
  public void setUsername(String username) {
  }

  @Override
  public void setPassword(String password) {
  }

  @Override
  public void setProjectKey(String projectKey) {
  }
}
