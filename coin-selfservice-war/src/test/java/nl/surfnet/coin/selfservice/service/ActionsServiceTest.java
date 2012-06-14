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

package nl.surfnet.coin.selfservice.service;

import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import nl.surfnet.coin.selfservice.domain.Action;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.JiraTask;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {
        "/coin-selfservice-context.xml",
        "/coin-selfservice-properties-context.xml",
        "classpath:coin-shared-context.xml"})
public class ActionsServiceTest {

  @Resource(name="actionsService")
  ActionsService actionsService;

  @Autowired
  private JiraService jiraService;

  @Test
  public void synchronization() throws IOException {

    final String idp = "https://mock-idp";

    JiraTask task = new JiraTask.Builder()
            .serviceProvider("https://mock-sp")
            .identityProvider(idp)
            .institution("institution-123")
            .issueType(JiraTask.Type.LINKREQUEST)
            .build();

    CoinUser user = new CoinUser();

    final String key = jiraService.create(task, user);

    actionsService.registerJiraIssueCreation(key, task, "foo", "bar");

    final List<Action> before = actionsService.getActions(idp);

    assertThat(before.size(), is(1));
    assertThat(before.get(0).getStatus(), is(Action.Status.OPEN));

    jiraService.doAction(key, JiraTask.Action.CLOSE);
    actionsService.synchronizeWithJira(idp);

    final List<Action> after = actionsService.getActions(idp);

    assertThat(after.size(), is(1));
    assertThat(after.get(0).getStatus(), is(Action.Status.CLOSED));
    assertThat(after.get(0).getUserName(), IsEqual.equalTo("bar"));
  }

}
