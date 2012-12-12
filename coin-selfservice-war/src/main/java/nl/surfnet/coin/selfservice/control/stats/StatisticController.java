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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProviderRepresenter;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProviderRepresenter;
import nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;

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

  @Resource(name = "providerService")
  private IdentityProviderService idpService;

  @RequestMapping("/stats.shtml")
  public String stats(ModelMap model, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    model.put("selectedidp", selectedidp);

    // Get all idp's which are known in selfservice and available in the statistics database
    if (AuthorityScopeInterceptor.isDistributionChannelAdmin()) {
      List<IdentityProviderRepresenter> idpRepresenters = new ArrayList<IdentityProviderRepresenter>();
      List<IdentityProvider> allIdps = idpService.getAllIdentityProviders();
      for (IdentityProviderRepresenter idpRepresenter : statisticDao.getIdpLoginIdentifiers()) {
        if (containsIdpWithEntityId(allIdps, idpRepresenter.getEntityId())) {
          idpRepresenters.add(idpRepresenter);
        }
      }
      model.put("allIdps", idpRepresenters);
    }
    return "stats/statistics";
  }

  private boolean containsIdpWithEntityId(List<IdentityProvider> allIdps, String entityId) {
    for (IdentityProvider idp : allIdps) {
      if (idp.getId().equals(entityId)) {
        return true;
      }
    }
    return false;
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

  @RequestMapping("/spcspcombis.json")
  public @ResponseBody
  List<CompoundServiceProviderRepresenter> getCspSpIds() {
    return statisticDao.getCompoundServiceProviderSpLinks();
  }

  public void setStatisticDao(StatisticDao statisticDao) {
    this.statisticDao = statisticDao;
  }
}
