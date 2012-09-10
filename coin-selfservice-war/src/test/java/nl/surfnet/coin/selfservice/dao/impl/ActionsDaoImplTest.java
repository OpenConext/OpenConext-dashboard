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

package nl.surfnet.coin.selfservice.dao.impl;

import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;
import nl.surfnet.coin.selfservice.domain.Action;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.matchers.JUnitMatchers.hasItems;

public class ActionsDaoImplTest extends AbstractInMemoryDatabaseTest {

  ActionsDaoImpl actionsDao;

  @Before
  public void myBefore() throws Exception {
    actionsDao = new ActionsDaoImpl();
    actionsDao.setDataSource(getJdbcTemplate().getDataSource());
  }

  @Test
  public void findNone() {
    assertNull(actionsDao.findAction(123123L));
  }

  @Test
  public void saveAndFind() {
    Action a = new Action("key", "userid", "username", Action.Type.QUESTION, Action.Status.OPEN, "body", "idp", "sp",
        "institute", new Date());
    actionsDao.saveAction(a);
    Action savedA = actionsDao.findAction(1L);
    assertNotNull(savedA);
    assertThat(savedA.getBody(), is("body"));
    assertThat(savedA.getJiraKey(), is("key"));
    assertThat(savedA.getUserName(), is("username"));
    assertThat(savedA.getSpId(), is("sp"));
    assertThat(savedA.getStatus(), is(Action.Status.OPEN));
    assertThat(savedA.getType(), is(Action.Type.QUESTION));
  }

  @Test
  public void findByIdP() {
    for (int i = 0; i < 3; i++) {
      Action a = new Action("key"+i, "userid", "username", Action.Type.QUESTION, Action.Status.OPEN, "body", "idp",
          "sp", "foobar", new Date());
      actionsDao.saveAction(a);
    }

    final List<Action> actions = actionsDao.findActionsByIdP("idp");
    assertThat(actions.size(), is(3));
    final List<Action> actions2 = actionsDao.findActionsByIdP("another-idp");
    assertThat(actions2.size(), is(0));
  }

  @Test
  public void getJiraKeys() {
    final String idp = "idp123";
    String[] keys = {"TEST-1", "TEST-2", "TEST-3", "TEST-4"};
    for (String key : keys) {
      actionsDao.saveAction(new Action(key, "userid", "username", Action.Type.QUESTION, Action.Status.OPEN, "body", idp, "sp", "institute-123", new Date()));
    }
    final List<String> fetchedKeys = actionsDao.getKeys(idp);
    assertThat(fetchedKeys, hasItems(keys));
  }

  @Test
  public void close() {
    final String jiraKey = "TEST-1346";
    Action a = new Action(jiraKey, "userid", "username", Action.Type.QUESTION, Action.Status.OPEN, "body", "idp", "sp",
            "institute", new Date());
    actionsDao.saveAction(a);

    final Action before = actionsDao.findAction(1L);
    assertThat(before.getStatus(), is(Action.Status.OPEN));

    actionsDao.close(jiraKey);

    final Action after = actionsDao.findAction(1L);
    assertThat(after.getStatus(), is(Action.Status.CLOSED));
  }


  @Override
  public String getMockDataContentFilename() {
    return "db/migration/hsqldb/V0.0.0__initial.sql";
  }

  @Override
  public String getMockDataCleanUpFilename() {
    return "coin-selfservice-db-cleanup.sql";
  }

}
