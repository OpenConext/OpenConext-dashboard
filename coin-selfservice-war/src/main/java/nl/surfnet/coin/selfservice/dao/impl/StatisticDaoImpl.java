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
import nl.surfnet.coin.selfservice.domain.IdentityProviderRepresenter;
import nl.surfnet.coin.selfservice.domain.StatResult;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.util.StringUtils;

/**
 * SQL implementation for the statistic service
 */
public class StatisticDaoImpl implements StatisticDao {

  private static final long DAY_IN_MS = 24L * 60L * 60L * 1000L;

  private static final String BASE_SQL = "select count(id) as cid, spentityid as spentityid, spentityname as spentityname, idpentityid as idpentityid, CAST(loginstamp AS DATE) as logindate from log_logins %s group by logindate, spentityid order by spentityid, logindate, idpentityid";
  private static final String SQL_WITH_IDP = String.format(BASE_SQL, "where idpentityid = ?");
  private static final String SQL_ALL_IDPS = String.format(BASE_SQL, "");

  @Autowired
  private JdbcTemplate ebJdbcTemplate;

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId) {
    String encodedIdp = bugFixForEntityId(idpEntityId);
    try {
      List<StatResult> results = ebJdbcTemplate.query(SQL_WITH_IDP, new Object[] { encodedIdp }, mapRowsToStatResult());
      return convertStatResultsToChartSeries(results);
    } catch (EmptyResultDataAccessException e) {
      return new ArrayList<ChartSerie>();
    }
  }

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay() {
    try {
      List<StatResult> results = ebJdbcTemplate.query(SQL_ALL_IDPS, mapRowsToStatResult());
      return convertStatResultsToChartSeries(results);
    } catch (EmptyResultDataAccessException e) {
      return new ArrayList<ChartSerie>();
    }
  }
  
  @Override
  public List<IdentityProviderRepresenter> getIdpLoginIdentifiers() {
    return ebJdbcTemplate.query("select distinct idpentityid as idpentityid, idpentityname as idpentityname from log_logins order by idpentityname", new RowMapper<IdentityProviderRepresenter>() {
      @Override
      public IdentityProviderRepresenter mapRow(ResultSet rs, int rowNum) throws SQLException {
        String name = rs.getString("idpentityname");
        String entityId = rs.getString("idpentityid");
        name = spNameNullCheck(name, entityId);
        return new IdentityProviderRepresenter(entityId,name);
      }});
  }

  private String bugFixForEntityId(String idpEntityId) {
    /*
     * Because we also want to show statistics for the IdP with id SURFnet%20BV
     * URLEncoder#encode replaces a space with +, but in the database we have
     * %20
     */
    String encodedIdp = idpEntityId.replaceAll(" ", "%20");
    return encodedIdp;
  }

  private RowMapper<StatResult> mapRowsToStatResult() {
    return new RowMapper<StatResult>() {
      @Override
      public StatResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        int logins = rs.getInt("cid");
        String spName = rs.getString("spentityname");
        String spEntityId = rs.getString("spentityid");
        String idpPentityId = rs.getString("idpentityid");
        Date logindate = rs.getDate("logindate");
        spName = spNameNullCheck(spName, spEntityId);
        return new StatResult(spEntityId, spName, logindate.getTime(), logins, idpPentityId);
      }

    };
  }

  private String spNameNullCheck(String spName, String spEntityId) {
    /*
     * Although we have ensured EngineBlock always inserts spentityname,
     * there might be older records (and the cornercase being that the
     * spentityname is not null but sometimes ''
     */
    spName = StringUtils.hasText(spName) ? spName : spEntityId;
    return spName;
  }
  /*
   * The SQL query returns a single row per date/Service provider combination.
   * For the {@link ChartSerie} we need one object per Service Provider with a
   * list of dates. If on a day no logins were done for an SP, the SQL query
   * returns no row. We need to insert a zero hits entry into the list of
   * logins.
   * 
   * @param statResults List of {@link StatResult}'s (SQL row)
   * 
   * @return List of {@link ChartSerie} (HighChart input)
   */
  private List<ChartSerie> convertStatResultsToChartSeries(List<StatResult> statResults) {
    Collections.sort(statResults);

    Map<String, ChartSerie> chartSerieMap = new HashMap<String, ChartSerie>();
    long previousMillis = 0;

    for (StatResult statResult : statResults) {
      ChartSerie chartSerie = chartSerieMap.get(statResult.getSpName());
      if (chartSerie == null) {
        chartSerie = new ChartSerie(statResult.getSpName(), statResult.getIdpEntityIdp(), statResult.getSpEntityId(),
            statResult.getMillis());
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
