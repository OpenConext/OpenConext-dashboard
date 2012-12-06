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

package nl.surfnet.coin.selfservice.control.stats;

import java.util.List;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller for statistics
 */
@Controller
@RequestMapping(value = "/stats/*")
public class StatisticController extends BaseController {

  @Autowired
  private StatisticDao statisticDao;

  @RequestMapping("/stats.shtml")
  public String stats(ModelMap model, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    model.put("selectedidp", selectedidp);
    if (AuthorityScopeInterceptor.isDistributionChannelAdmin()) {
      model.put("allIdps", statisticDao.getIdpLoginIdentifiers());
    }
    return "stats/statistics";
  }

  @RequestMapping("/loginsperspperdaybyidp.json")
  public @ResponseBody
  List<ChartSerie> getLoginsPerSPByIdp(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    return statisticDao.getLoginsPerSpPerDay(selectedidp.getId());
  }

  @RequestMapping("/loginsperspperday.json")
  public @ResponseBody
  List<ChartSerie> getLoginsPerSP() {
    return statisticDao.getLoginsPerSpPerDay();
  }

  public void setStatisticDao(StatisticDao statisticDao) {
    this.statisticDao = statisticDao;
  }
}
