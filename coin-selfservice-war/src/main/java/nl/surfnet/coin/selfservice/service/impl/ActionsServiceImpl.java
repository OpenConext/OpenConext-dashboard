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
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import nl.surfnet.coin.selfservice.dao.impl.ActionsDaoImpl;
import nl.surfnet.coin.selfservice.domain.Action;
import nl.surfnet.coin.selfservice.domain.JiraTask;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;

@Service(value = "actionsService")
public class ActionsServiceImpl implements ActionsService {

  @Resource(name="actionsDao")
  private ActionsDaoImpl actionsDao;

  @Resource(name="jiraService")
  private JiraService jiraService;

  @Override
  public List<Action> getActions(String identityProvider) {
    return actionsDao.findActionsByIdP(identityProvider);
  }

  @Override
  public void registerJiraIssueCreation(String issueKey, JiraTask task, String userId, String userName) {
    Action a = new Action(issueKey, userId, userName, Action.Type.byJiraIssueType(task.getIssueType()),
        Action.Status.byJiraIssueStatus(task.getStatus()), task.getBody(),
        task.getIdentityProvider(), task.getServiceProvider(), task.getInstitution(), new Date());
    actionsDao.saveAction(a);
  }

  @Override
  public void synchronizeWithJira(String identityProvider) throws IOException {
    List<String> openTasks = actionsDao.getKeys(identityProvider);
    final List<JiraTask> tasks = jiraService.getTasks(openTasks);
    for (JiraTask task : tasks) {
      if (task.getStatus() == JiraTask.Status.CLOSED) {
        actionsDao.close(task.getKey());
      }
    }
  }
}
