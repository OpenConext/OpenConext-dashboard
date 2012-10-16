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

package nl.surfnet.coin.selfservice.control.idpadmin;

import nl.surfnet.coin.selfservice.control.statistics.StatisticController;
import nl.surfnet.coin.selfservice.dao.impl.MockStatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;

/**
Test class for {@link nl.surfnet.coin.selfservice.control.statistics.StatisticController}
 */
public class StatisticControllerTest {

  private StatisticController controller = new StatisticController();

  @Before
  public void setUp() throws Exception {
    controller.setStatisticDao(new MockStatisticDao());
  }

  @Test
  public void testChartData() throws Exception {
    IdentityProvider idp = new IdentityProvider("mock-123", null, "Mock 123");
    ChartSerie chartData = controller.getLoginsPerSP(idp, "spentityid");
    assertFalse(chartData.getData().isEmpty());
  }

}
