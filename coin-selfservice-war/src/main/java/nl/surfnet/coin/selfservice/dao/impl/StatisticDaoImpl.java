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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.StatResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * SQL implementation for the statistic service
 */
@Repository
public class StatisticDaoImpl implements StatisticDao {

  private static final long DAY_IN_MS = 24L * 60L * 60L * 1000L;

  @Autowired
  private JdbcTemplate ebJdbcTemplate;

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId) {
    List<StatResult> statResults;
    /*
     * Because we also want to show statistics for the IdP with id SURFnet%20BV
     * URLEncoder#encode replaces a space with +, but in the database we have
     * %20
     */
    String encodedIdp = idpEntityId.replaceAll(" ", "%20");
    Object[] args = new Object[] { encodedIdp } ;

    try {
      String sql = getSql();
      statResults = this.ebJdbcTemplate.query(sql, args, mapRowsToStatResult());
    } catch (EmptyResultDataAccessException e) {
      return new ArrayList<ChartSerie>();
    }
    return convertStatResultsToChartSeries(statResults);
  }

  private String getSql() {
    StringBuilder sql = new StringBuilder("select count(id) as cid, spentityname, CAST(loginstamp AS DATE) as logindate ");
    sql.append("from log_logins where idpentityid = ? ");
    sql.append("group by logindate, spentityname order by spentityname, logindate");
    return sql.toString();
  }

  public RowMapper<StatResult> mapRowsToStatResult() {
    return new RowMapper<StatResult>() {
      @Override
      public StatResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        int logins = rs.getInt("cid");
        String sp = rs.getString("spentityname");
        Date logindate = rs.getDate("logindate");
        return  new StatResult(sp,logindate.getTime(),logins);
      }
    };
  }

  /**
   * The SQL query returns a single row per date/Service provider combination.
   * For the {@link ChartSerie} we need one object per Service Provider with a
   * list of dates. If on a day no logins were done for an SP, the SQL query
   * returns no row. We need to insert a zero hits entry into the list of
   * logins.
   * 
   * @param statResults
   *          List of {@link StatResult}'s (SQL row)
   * @return List of {@link ChartSerie} (HighChart input)
   */
  public List<ChartSerie> convertStatResultsToChartSeries(List<StatResult> statResults) {
    Collections.sort(statResults);

    Map<String, ChartSerie> chartSerieMap = new HashMap<String, ChartSerie>();
    long previousMillis = 0;

    for (StatResult statResult : statResults) {
      ChartSerie chartSerie = chartSerieMap.get(statResult.getSpName());
      if (chartSerie == null) {
        chartSerie = new ChartSerie(statResult.getSpName(), statResult.getMillis());
      } else {
        int nbrOfZeroDays = (int) (((statResult.getMillis() - previousMillis) / DAY_IN_MS) - 1);
        chartSerie.addZeroDays(nbrOfZeroDays);
      }
      chartSerie.addData(statResult.getLogins());
      previousMillis = statResult.getMillis();
      chartSerieMap.put(chartSerie.getName(), chartSerie);
    }
    List<ChartSerie> chartSeries = new ArrayList<ChartSerie>();
    for (ChartSerie c : chartSerieMap.values()) {
      chartSeries.add(c);
    }
    return chartSeries;
  }

  public void setEbJdbcTemplate(JdbcTemplate ebJdbcTemplate) {
    this.ebJdbcTemplate = ebJdbcTemplate;
  }
}
