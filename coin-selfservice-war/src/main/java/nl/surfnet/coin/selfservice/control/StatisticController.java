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

package nl.surfnet.coin.selfservice.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.StatResult;

/**
 * Controller for statistics
 */
@Controller
public class StatisticController extends BaseController {

  @Autowired
  private StatisticDao statisticDao;

  @RequestMapping("/splogins.json")
  public
  @ResponseBody
  List<ChartSerie> getSPLogins(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    return statisticDao.getLoginsPerSP(selectedidp.getId());
  }

  @RequestMapping("/loginsperspperday.json")
  public
  @ResponseBody
  Map<String, List<StatResult>> getLoginsPerSP(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, List<StatResult>> m = new HashMap<String, List<StatResult>>();

    final List<StatResult> loginsPerDay = statisticDao.getLoginsPerDay(selectedidp.getId());
    for (StatResult s : loginsPerDay) {
      String spEntityId = s.getSpEntityId();
      if (m.containsKey(spEntityId)) {
        m.get(spEntityId).add(s);
      } else {
        List<StatResult> statResults = new ArrayList<StatResult>();
        statResults.add(s);
        m.put(spEntityId, statResults);
      }
    }
    return m;
  }

  void setStatisticDao(StatisticDao statisticDao) {
    this.statisticDao = statisticDao;
  }

}
