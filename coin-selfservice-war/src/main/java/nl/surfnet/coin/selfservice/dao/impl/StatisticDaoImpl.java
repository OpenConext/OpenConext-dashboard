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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.StatResult;

import org.joda.time.LocalDate;
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
  public List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId, String spEntityId) {
    List<StatResult> statResults;
    /*
     * Because we also want to show statistics for the IdP with id SURFnet%20BV
     * URLEncoder#encode replaces a space with +, but in the database we have
     * %20
     */
    String encodedIdp = idpEntityId.replaceAll(" ", "%20");
    Object[] args = spEntityId == null ? new Object[] { encodedIdp } : new Object[] { encodedIdp, spEntityId };

    try {
      String sql = getSql(spEntityId);
      statResults = this.ebJdbcTemplate.query(sql, args, mapRowsToStatResult());
    } catch (EmptyResultDataAccessException e) {
      return new ArrayList<ChartSerie>();
    }
    return convertStatResultsToChartSeries(statResults);
  }

  private String getSql(String spEntityId) {
    StringBuilder sql = new StringBuilder("select count(id) as cid, spentityid, CAST(loginstamp AS DATE) as logindate ");
    sql.append("from log_logins where idpentityid = ? ");
    if (spEntityId != null) {
      sql.append("and spentityid = ? ");
    }
    sql.append("group by logindate, spentityid order by spentityid, logindate");
    return sql.toString();
  }

  public RowMapper<StatResult> mapRowsToStatResult() {
    return new RowMapper<StatResult>() {
      @Override
      public StatResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        int logins = rs.getInt("cid");
        String spentitiy = rs.getString("spentityid");
        Date logindate = rs.getDate("logindate");
        return  new StatResult(spentitiy,logindate.getTime(),logins);
//        statResult.setSpEntityId(spentitiy);
//        final Calendar cal = Calendar.getInstance();
//        cal.setTime(logindate);
//        convertToGmt(cal);
//        statResult.setDate(convertToGmt(cal).getTime());
//        statResult.setLogins(logins);
//        return statResult;
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
      ChartSerie chartSerie = chartSerieMap.get(statResult.getSpEntityId());
      if (chartSerie == null) {
        chartSerie = new ChartSerie(statResult.getSpEntityId(), statResult.getMillis());
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

  /**
   * Get a converted Calendar in which the timezone difference has been added.<br />
   * <br />
   * 
   * Input: 0:00:00 CEST<br />
   * Output: 2:00:00 CEST
   * 
   * @param cal
   *          the original
   * @return converted calendar
   */
  private Calendar convertToGmt(Calendar cal) {

    Date date = cal.getTime();
    TimeZone tz = cal.getTimeZone();

    // Returns the number of milliseconds since January 1, 1970, 00:00:00 GMT
    long msFromEpochGmt = date.getTime();

    // gives you the current offset in ms from GMT at the current date
    int offsetFromUTC = tz.getOffset(msFromEpochGmt);

    // create a new calendar in GMT timezone, set to this date and add the
    // offset
    Calendar gmtCal = Calendar.getInstance(TimeZone.getTimeZone("GMT"));
    gmtCal.setTime(date);
    gmtCal.add(Calendar.MILLISECOND, offsetFromUTC);

    return gmtCal;
  }

  public void setEbJdbcTemplate(JdbcTemplate ebJdbcTemplate) {
    this.ebJdbcTemplate = ebJdbcTemplate;
  }
}
