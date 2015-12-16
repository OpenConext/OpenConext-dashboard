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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.stereotype.Repository;

import selfservice.dao.ActionsDao;
import selfservice.domain.Action;
import selfservice.domain.JiraTask;

@Repository("actionsDao")
public class ActionsDaoImpl implements ActionsDao {

  private final JdbcTemplate jdbcTemplate;
  private final SimpleJdbcInsert insertAction;

  @Autowired
  public ActionsDaoImpl(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
    this.insertAction =
      new SimpleJdbcInsert(jdbcTemplate.getDataSource())
        .withTableName("ss_actions")
        .usingGeneratedKeyColumns("id");
  }

  private static class ActionRowMapper implements RowMapper<Action> {
    @Override
    public Action mapRow(final ResultSet resultSet, final int i) throws SQLException {
      final Action action = new Action(
        resultSet.getString("jiraKey"),
        resultSet.getString("userId"),
        resultSet.getString("userName"),
        null,//we don't store the email of the originator
        JiraTask.Type.valueOf(resultSet.getString("actionType")),
        JiraTask.Status.valueOf(resultSet.getString("actionStatus")),
        resultSet.getString("body"),
        resultSet.getString("idp"),
        resultSet.getString("sp"),
        resultSet.getString("institutionId"),
        resultSet.getTimestamp("requestDate"));
      action.setId(resultSet.getLong("id"));
      action.setIdpName(resultSet.getString("idp_name"));
      action.setSpName(resultSet.getString("sp_name"));
      return action;
    }
  }

  @Override
  public List<Action> findActionsByIdP(String identityProvider) {
    return jdbcTemplate.query("SELECT id, jiraKey, userId, userName, actionType, actionStatus, body, idp, sp, idp_name, sp_name, " +
        " institutionId, requestDate FROM ss_actions WHERE idp = ? ORDER BY id", new ActionRowMapper(),
      identityProvider);
  }

  @Override
  public Long saveAction(final Action action) {
    Map<String, Object> params = new HashMap<>();
    String[] columns = new String[]{"jiraKey", "userId", "userName", "idp", "sp", "idp_name", "sp_name", "institutionId", "actionType", "actionStatus", "body", "requestDate"};
    Object[] values = new Object[]{action.getJiraKey(), action.getUserId(), action.getUserName(), action.getIdpId(),
      action.getSpId(), action.getIdpName(), action.getSpName(), action.getInstitutionId(), action.getType().name(), action.getStatus().name(), action.getBody(),
      action.getRequestDate()};
    for (int i = 0; i < columns.length; i++) {
      params.put(columns[i], values[i]);
    }
    Number newId = insertAction.executeAndReturnKey(params);
    action.setId(newId.longValue());
    return action.getId();
  }

  @Override
  public Action findAction(long id) {
    try {
      return jdbcTemplate.queryForObject("select * from ss_actions where id = ?", new ActionRowMapper(), id);
    } catch (EmptyResultDataAccessException e) {
      return null;
    }
  }

  @Override
  public void close(final String jiraKey) {
    jdbcTemplate.update("UPDATE ss_actions SET actionStatus = 'CLOSED' WHERE jiraKey = ?", jiraKey);
  }

  @Override
  public List<String> getKeys(String identityProvider) {
    return jdbcTemplate.query("SELECT jiraKey FROM ss_actions WHERE actionStatus = 'OPEN' AND idp = ?", (resultSet, i) -> {
      return resultSet.getString("jiraKey");
    }, identityProvider);
  }

  @Override
  public List<Action> findActionsByDateRange(Date from, Date to) {
    return jdbcTemplate.query("select * from ss_actions where requestDate >= ? AND requestDate < ? order by requestDate", new ActionRowMapper(), from, to);
  }
}
