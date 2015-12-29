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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import selfservice.dao.impl.ActionsDaoImpl;
import selfservice.domain.Action;
import selfservice.domain.JiraTask;
import selfservice.domain.CoinUser;
import selfservice.service.ActionsService;

@Service(value = "actionsService")
public class ActionsServiceImpl implements ActionsService {

  @Autowired
  private ActionsDaoImpl actionsDao;

  @Autowired
  private JiraClient jiraClient;

  @Override
  public List<Action> getActions(String identityProvider) {
    List<String> openTasks = actionsDao.getKeys(identityProvider);
    final List<JiraTask> tasks = jiraClient.getTasks(openTasks);

    // does an impromptu status update, only record transitions to 'closed', otherwise we
    // will assume the issue to be 'open' or 'in progress'. The end-user cares only about 'done' or 'not done'?

    // TODO the ad-hoc character of this  jira <-> csa synchronization is bothersome.
    tasks.stream().
      filter(task -> JiraTask.Status.CLOSED.equals(task.getStatus())).
      forEach(task -> actionsDao.close(task.getKey()));
    return actionsDao.findActionsByIdP(identityProvider);
  }

  @Override
  public void registerJiraIssueCreation(Action action) {
    JiraTask task = new JiraTask.Builder()
      .body(action.getUserEmail() + ("\n\n" + action.getBody()))
      .identityProvider(action.getIdpId()).serviceProvider(action.getSpId())
      .institution(action.getInstitutionId()).issueType(action.getType())
      .status(JiraTask.Status.OPEN).build();

    CoinUser coinUser = new CoinUser();
    coinUser.setDisplayName(action.getUserName());
    coinUser.setEmail(action.getUserEmail());
    coinUser.setUid(action.getUserId());

    String jiraKey = jiraClient.create(task, coinUser);
    action.setJiraKey(jiraKey);
  }

  @Override
  public Action registerAction(Action action) {
    actionsDao.saveAction(action);
    return action;
  }

}
