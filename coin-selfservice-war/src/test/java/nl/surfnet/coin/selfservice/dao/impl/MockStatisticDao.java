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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;

/**
 * Mock implementation for {@link nl.surfnet.coin.selfservice.dao.StatisticDao}
 */
public class MockStatisticDao implements StatisticDao {

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId, String spEntityId) {
    List<ChartSerie> result = new ArrayList<ChartSerie>();
    final ChartSerie e = new ChartSerie();
    e.setData(Arrays.asList(1, 2, 1, 2,3123, 13, 123, 123, 123));
    result.add(e);
    return result;
  }
}
