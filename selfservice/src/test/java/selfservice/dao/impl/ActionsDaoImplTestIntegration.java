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
package selfservice.dao.impl;

import static org.hamcrest.CoreMatchers.hasItems;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import selfservice.Application;
import selfservice.dao.ActionsDao;
import selfservice.domain.Action;
import selfservice.domain.JiraTask;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@Rollback
@Transactional
@ActiveProfiles("dev")
public class ActionsDaoImplTestIntegration {

  @Autowired
  private ActionsDao actionsDao;

  @Test
  public void findNone() {
    assertNull(actionsDao.findAction(123123L));
  }

  @Test
  public void saveAndFind() {
    Action a = new Action("key", "userid", "username", "john.doe@nl", JiraTask.Type.QUESTION, JiraTask.Status.OPEN, "body", "idp", "sp", "institute", new Date());
    a.setIdpName("idpName");
    a.setSpName("spName");

    long id = actionsDao.saveAction(a);
    Action savedA = actionsDao.findAction(id);

    assertNotNull(savedA);

    assertThat(savedA.getBody(), is("body"));
    assertThat(savedA.getJiraKey(), is("key"));
    assertThat(savedA.getUserName(), is("username"));
    assertThat(savedA.getSpId(), is("sp"));
    assertThat(savedA.getStatus(), is(JiraTask.Status.OPEN));
    assertThat(savedA.getType(), is(JiraTask.Type.QUESTION));
    assertEquals("idpName", savedA.getIdpName());
    assertEquals("spName", savedA.getSpName());
  }

  @Test
  public void findByIdP() {
    for (int i = 0; i < 3; i++) {
      Action a = new Action("key" + i, "userid", "username", "john.doe@nl", JiraTask.Type.QUESTION, JiraTask.Status.OPEN, "body", "idp", "sp", "foobar", new Date());
      actionsDao.saveAction(a);
    }

    List<Action> actions = actionsDao.findActionsByIdP("idp");
    assertThat(actions.size(), is(3));

    List<Action> actions2 = actionsDao.findActionsByIdP("another-idp");
    assertThat(actions2.size(), is(0));
  }

  @Test
  public void getJiraKeys() {
    String idp = "idp123";
    String[] keys = {"TEST-1", "TEST-2", "TEST-3", "TEST-4"};
    for (String key : keys) {
      actionsDao.saveAction(new Action(key, "userid", "username", "john.doe@nl", JiraTask.Type.QUESTION, JiraTask.Status.OPEN, "body", idp, "sp", "institute-123", new Date()));
    }

    List<String> fetchedKeys = actionsDao.getKeys(idp);

    assertThat(fetchedKeys, hasItems(keys));
  }

  @Test
  public void close() {
    String jiraKey = "TEST-1346";
    Action a = new Action(jiraKey, "userid", "username", "john.doe@nl", JiraTask.Type.QUESTION, JiraTask.Status.OPEN, "body", "idp", "sp", "institute", new Date());
    long id = actionsDao.saveAction(a);

    Action before = actionsDao.findAction(id);
    assertThat(before.getStatus(), is(JiraTask.Status.OPEN));

    actionsDao.close(jiraKey);

    Action after = actionsDao.findAction(id);
    assertThat(after.getStatus(), is(JiraTask.Status.CLOSED));
  }

}
