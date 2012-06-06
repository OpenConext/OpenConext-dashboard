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

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;

/**
 * Mock implementation for {@link nl.surfnet.coin.selfservice.dao.StatisticDao}
 */
public class MockStatisticDao implements StatisticDao {

  @Override
  public List<ChartSerie> getLoginsPerSP(String idpEntityId) {
    ChartSerie mujina = new ChartSerie();
    mujina.setName("Mujina");
    List<Integer> integers = Arrays.asList(Integer.valueOf(5), Integer.valueOf(7), Integer.valueOf(3), Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(0));
    mujina.setData(integers);
    Calendar cal = Calendar.getInstance();
    cal.set(2012, Calendar.JANUARY, 2);
    mujina.setPointStart(cal.getTime());

    ChartSerie testSp = new ChartSerie();
    testSp.setName("TestSp");
    integers = Arrays.asList(Integer.valueOf(1), Integer.valueOf(0), Integer.valueOf(4));
    testSp.setData(integers);
    cal.set(2012, Calendar.JANUARY, 1);
    testSp.setPointStart(cal.getTime());

    ChartSerie surfTeams = new ChartSerie();
    surfTeams.setName("SURFteams");
    integers = Arrays.asList(Integer.valueOf(3), Integer.valueOf(2), Integer.valueOf(3));
    surfTeams.setData(integers);
    cal.set(2011, Calendar.DECEMBER, 31);
    surfTeams.setPointStart(cal.getTime());

    return Arrays.asList(mujina, testSp, surfTeams);
  }
}
