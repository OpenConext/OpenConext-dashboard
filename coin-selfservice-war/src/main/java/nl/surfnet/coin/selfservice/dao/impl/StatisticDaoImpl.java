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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.StatResult;

/**
 * SQL implementation for the statistic service
 */
@Repository
public class StatisticDaoImpl implements StatisticDao {

  private static final long DAY_IN_MS = 24L * 60L * 60L * 1000L;

  @Autowired
  private JdbcTemplate ebJdbcTemplate;

  @Override
  public List<ChartSerie> getLoginsPerSP(String idpEntityId) {
    final List<StatResult> statResults = getLoginsPerDay(idpEntityId);
    return convertStatResultsToChartSeries(statResults);
  }

  @Override
  public List<StatResult> getLoginsPerDay(String idpEntityId) {
    return this.getLoginsPerSpPerDay(idpEntityId, null);
  }

  @Override
  public List<StatResult> getLoginsPerSpPerDay(String idpEntityId, String spEntityId) {
    List<StatResult> statResults;
    Object[] args = spEntityId == null ? new Object[]{idpEntityId} : new Object[]{idpEntityId, spEntityId};

    try {
      final StringBuilder sql = new StringBuilder("select count(*), spentityid, date(loginstamp) ");
      sql.append("from log_logins where idpentityid = ? ");
      if (spEntityId != null) {
        sql.append("and spentityid = ? ");
      }
      sql.append("group by day(loginstamp), spentityid ");
      sql.append("order by spentityid, loginstamp");

      statResults = this.ebJdbcTemplate.query(
          sql.toString(),
          args, mapRowsToStatResult());
    } catch (EmptyResultDataAccessException e) {
      statResults = new ArrayList<StatResult>();
    }
    return statResults;
  }

  private RowMapper<StatResult> mapRowsToStatResult() {
    return new RowMapper<StatResult>() {
      @Override
      public StatResult mapRow(ResultSet rs, int rowNum) throws SQLException {
        int logins = rs.getInt("count(*)");
        String spentitiy = rs.getString("spentityid");
        String mySqlDate = rs.getString("date(loginstamp)");
        StatResult statResult = new StatResult();
        statResult.setSpEntityId(spentitiy);
        statResult.setDate(convertFromMySqlString(mySqlDate));
        statResult.setLogins(logins);
        return statResult;
      }
    };
  }

  /**
   * The SQL query returns a single row per date/Service provider combination. For the {@link ChartSerie}
   * we need one object per Service Provider with a list of dates.
   * If on a day no logins were done for an SP, the SQL query returns no row. We need to insert a zero hits
   * entry into the list of logins.
   *
   * @param statResults List of {@link StatResult}'s (SQL row)
   * @return List of {@link ChartSerie} (HighChart input)
   */
  public List<ChartSerie> convertStatResultsToChartSeries(List<StatResult> statResults) {
    Collections.sort(statResults);

    Map<String, ChartSerie> chartSerieMap = new LinkedHashMap<String, ChartSerie>();
    Date previousDate = new Date();

    for (StatResult statResult : statResults) {
      ChartSerie c = chartSerieMap.get(statResult.getSpEntityId());
      if (c == null) {
        c = new ChartSerie();
        c.setName(statResult.getSpEntityId());
        c.setPointStart(statResult.getDate());
      } else {
        final long dayDiff = statResult.getDate().getTime() - previousDate.getTime();
        long nrOfZeroDates = dayDiff / DAY_IN_MS;
        for (long i = 1; i < nrOfZeroDates; i++) {
          c.addData(0);
        }
      }
      c.addData(statResult.getLogins());
      previousDate = statResult.getDate();
      chartSerieMap.put(c.getName(), c);
    }
    List<ChartSerie> chartSeries = new ArrayList<ChartSerie>();
    for (ChartSerie c : chartSerieMap.values()) {
      chartSeries.add(c);
    }
    return chartSeries;
  }

  /**
   * Creates a {@link Date} that maps with the date string value returned by MySQL
   * <p/>
   * MySQL returns date as yyyy-MM-dd, but the Month offset is 1 (0 is reserved for the case the month is unknown)
   * So Jan 1, 2011 is 2011-01-01.
   * We need to substract 1 from the month to get the proper Java month. Then we need to set the TimeZone to GMT
   * so that the UTC value of the Date is not influenced by the current machine's timezone.
   *
   * @param mySqlDate String representation of a date
   */
  public static Date convertFromMySqlString(String mySqlDate) {
    Calendar calendar = Calendar.getInstance();
    String[] dateFields = mySqlDate.split("-");
    int year = Integer.parseInt(dateFields[0]);
    int month = Integer.parseInt(dateFields[1]) - 1;
    int day = Integer.parseInt(dateFields[2]);
    calendar.set(year, month, day, 0, 0, 0);
    calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
    return calendar.getTime();
  }

}
