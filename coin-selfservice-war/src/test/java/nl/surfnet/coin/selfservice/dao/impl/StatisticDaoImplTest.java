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

import static junit.framework.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:coin-selfservice-context.xml", "classpath:coin-selfservice-properties-context.xml",
    "classpath:coin-shared-context.xml" })
@TransactionConfiguration(transactionManager = "selfServiceTransactionManager", defaultRollback = true)
@Transactional
public class StatisticDaoImplTest {

  @Autowired
  private StatisticDao dao;

  @Test
  public void testConvertStatResultsToChartSeries() throws Exception {
    List<ChartSerie> series = dao.getLoginsPerSpPerDay("http://mock-idp");
    assertEquals(2, series.size());

    ChartSerie serie = getChartSerie("https://rave.beta.surfnet.nl", series);
    assertEquals(Arrays.asList(1,2,1), serie.getData());

    serie = getChartSerie("https://canvas.test.surfnet.nl", series);
    assertEquals(Arrays.asList(1,0,0,2,1), serie.getData());
  }

  private ChartSerie getChartSerie(String sp, List<ChartSerie> series) {
    for (ChartSerie chartSerie : series) {
      if (chartSerie.getName().equals(sp)) {
        return chartSerie;
      }
    }
    throw new RuntimeException("No chartSerie with name '" + sp + "'");
  }
}
