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

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.dao.FacetDao;
import nl.surfnet.coin.selfservice.domain.*;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.PersonMainAttributes;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller of the homepage showing 'my apps' (or my services, meaning the
 * services that belong to you as a user with a specific role)
 * 
 */
@Controller
public class HomeController extends BaseController {

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Resource
  private CompoundSPService compoundSPService;

  @Resource
  private FacetDao facetDao;

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  @RequestMapping("/app-overview.shtml")
  public ModelAndView home(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                           @RequestParam(value = "view", defaultValue = "card") String view, HttpServletRequest request) {
    Map<String, Object> model = new HashMap<String, Object>();

    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
    model.put(COMPOUND_SPS, services);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    model.put("personAttributeLabels", attributeLabelMap);
    model.put("view", view);
    model.put("showFacetSearch", true);
    List<Facet> facets = facetDao.findAll();
    facets = this.filterFacetValues(services, facets);
    model.put("facets",facets);

    return new ModelAndView("app-overview", model);
  }

  @RequestMapping("/user.shtml")
  public ModelAndView user() {
    Map<String, Object> model = new HashMap<String, Object>();
    CoinUser user = SpringSecurity.getCurrentUser();
    model.put("mainAttributes", new PersonMainAttributes(user.getAttributeMap()));
    return new ModelAndView("user", model);
  }

  @RequestMapping(value = "/closeNotificationPopup.shtml")
  public void closeNotificationPopup(HttpServletRequest request) {
    notificationPopupClosed(request);
  }

  private List<Facet> filterFacetValues(List<CompoundServiceProvider> services, List<Facet> facets) {
    for (Facet facet : facets) {
      for (FacetValue facetValue : facet.getFacetValues()) {
        int count = 0;
        for (CompoundServiceProvider service : services) {
          if (service.getFacetValues().contains(facetValue)) {
            ++count;
          }
        }
        facetValue.setCount(count);
      }
    }
    return facets;
  }
}
