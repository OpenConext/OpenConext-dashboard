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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import nl.surfnet.coin.selfservice.dao.ActionsDao;
import nl.surfnet.coin.selfservice.domain.Action;

/**
 * Implementation of the ActionsDao, using a RDBMS for persistence
 */
@Repository("actionsDao")
public class ActionsDaoImpl implements ActionsDao {

  private JdbcOperations jdbcTemplate;

  @Resource(name = "selfserviceDataSource")
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static class ActionRowMapper implements RowMapper<Action> {
    @Override
    public Action mapRow(final ResultSet resultSet, final int i) throws SQLException {
      final Action action = new Action(
          resultSet.getString("jiraKey"),
          resultSet.getString("userId"),
          resultSet.getString("userName"),
          Action.Type.valueOf(resultSet.getString("actionType")),
          Action.Status.valueOf(resultSet.getString("actionStatus")),
          resultSet.getString("body"),
          resultSet.getString("idp"),
          resultSet.getString("sp"),
          resultSet.getString("institutionId"),
          resultSet.getTimestamp("requestDate"));
      action.setId(resultSet.getLong("id"));
      return action;
    }
  }

  @Override
  public List<Action> findActionsByIdP(String identityProvider) {
    return jdbcTemplate.query("SELECT id, jiraKey, userId, userName, actionType, actionStatus, body, idp, " +
        "sp, institutionId, requestDate FROM ss_actions WHERE idp = ?", new ActionRowMapper(),
            identityProvider);
  }

  @Override
  public void saveAction(Action action) {
    jdbcTemplate.update(
        "INSERT INTO ss_actions (jiraKey, userId, userName, idp, sp, institutionId, actionType, actionStatus, body, " +
            "requestDate) VALUES(" +
            "?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
        action.getJiraKey(), action.getUserId(), action.getUserName(), action.getIdpId(),
        action.getSpId(), action.getInstitutionId(), action.getType().name(), action.getStatus().name(), action.getBody(),
        action.getRequestDate());
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
    return jdbcTemplate.query("SELECT jiraKey FROM ss_actions WHERE actionStatus = 'OPEN' AND idp = ?", new RowMapper<String>() {
      @Override
      public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
        return resultSet.getString("jiraKey");
      }
    }, identityProvider);
  }
}
