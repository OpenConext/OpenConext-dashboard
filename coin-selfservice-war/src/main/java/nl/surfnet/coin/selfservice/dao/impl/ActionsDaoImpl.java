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

  private JdbcTemplate jdbcTemplate;

  @Resource(name = "selfserviceDataSource")
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  private static class ActionRowMapper implements RowMapper<Action> {
    @Override
    public Action mapRow(final ResultSet resultSet, final int i) throws SQLException {
      return new Action(
          resultSet.getString("jiraKey"),
          resultSet.getString("userId"),
          resultSet.getString("userName"),
          Action.Type.valueOf(resultSet.getString("actionType")),
          Action.Status.valueOf(resultSet.getString("actionStatus")),
          resultSet.getString("body"),
          resultSet.getString("idp"),
          resultSet.getString("sp"),
          resultSet.getDate("requestDate"));
    }
  }

  @Override
  public List<Action> findActionsByInstitute(String institutionId) {
    return jdbcTemplate.query("SELECT jiraKey, userId, userName, actionType, actionStatus, body, idp, " +
        "sp FROM ss_actions WHERE institutionId = ?", new ActionRowMapper(), institutionId);
  }

  @Override
  public void saveAction(Action action) {
    jdbcTemplate.update(
        "INSERT INTO ss_actions(jiraKey, userId, userName, institutionId, actionType, actionStatus, body) VALUES(?, ?, ?, ?, ?, 'QUESTION', 'OPEN', ?)",
        action.getJiraKey(), action.getUserId(), action.getUserName(), action.getJiraKey());
  }

  @Override
  public Action findAction(int id) {
    return jdbcTemplate.queryForObject("select * from ss_actions where actionId = ?", new ActionRowMapper(), id);
  }
}
