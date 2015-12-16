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

package csa.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import csa.domain.CoinUser;
import csa.model.JiraTask;

public class JiraClientMock implements JiraClient {

  private static final Logger LOG = LoggerFactory.getLogger(JiraClientMock.class);
  private Map<String, JiraTask> repository;

  private int counter = 0;

  public JiraClientMock() {
    repository = new HashMap<>();
  }

  @Override
  public String create(final JiraTask task, CoinUser user) {
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
  public List<JiraTask> getTasks(final List<String> keys) {
    List<JiraTask> tasks = new ArrayList<>();
    for (String key : keys) {
      final JiraTask task = repository.get(key);
      if (task != null) {
        tasks.add(task);
      }
    }
    return tasks;
  }

}
