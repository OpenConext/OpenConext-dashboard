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

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;

/**
 * Controller for statistics
 */
@Controller
@RequestMapping(value = "/idpadmin/*")
public class StatisticController extends BaseController {

  @Autowired
  private StatisticDao statisticDao;

  @RequestMapping("/loginsperspperday.json")
  public
  @ResponseBody
  ChartSerie getLoginsPerSP(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                                               @RequestParam(value = "spentityid", required = false) String spentityid) {

    final List<ChartSerie> loginsPerDay = statisticDao.getLoginsPerSpPerDay(selectedidp.getId(), spentityid);
    if (loginsPerDay.size() > 0) {
      return loginsPerDay.get(0);
    } else {
      return null;
    }
  }

  void setStatisticDao(StatisticDao statisticDao) {
    this.statisticDao = statisticDao;
  }

}
