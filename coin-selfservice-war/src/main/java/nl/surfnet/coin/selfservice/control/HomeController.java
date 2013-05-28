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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Category;
import nl.surfnet.coin.csa.model.CategoryValue;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.csa.model.Taxonomy;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
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
  private Csa csa;

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  @RequestMapping("/app-overview.shtml")
  public ModelAndView home(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp,
                           @RequestParam(value = "view", defaultValue = "card") String view, HttpServletRequest request) {
    Map<String, Object> model = new HashMap<String, Object>();

    List<Service> services = csa.getServicesForIdp(selectedidp.getId());
    model.put(SERVICES, services);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    model.put("personAttributeLabels", attributeLabelMap);
    model.put("view", view);
    addLicensedConnectedCounts(model, services);
    model.put("showFacetSearch", true);

    List<Category> facets = this.filterFacetValues(services, csa.getTaxonomy());
    model.put("facets", facets);
    model.put("facetsUsed", this.isCategoryValuesUsed(facets));

    return new ModelAndView("app-overview", model);
  }

  private void addLicensedConnectedCounts(Map<String, Object> model, List<Service> services) {
    int connectedCount = getConnectedCount(services);
    model.put("connectedCount", connectedCount);
    model.put("notConnectedCount", services.size() - connectedCount);

    int licensedCount = getLicensedCount(services);
    model.put("licensedCount", licensedCount);
    model.put("notLicensedCount", services.size() - licensedCount);
  }

  private boolean isCategoryValuesUsed(List<Category> categories) {
    for (Category cat : categories) {
      if (cat.isUsedFacetValues()) {
        return true;
      }
    }
    return false;
  }

  private int getLicensedCount(List<Service> services) {
    int result = 0;
    for (Service service : services) {
      if (service.getLicense() != null) {
        ++result;
      }
    }
    return result;
  }

  private int getConnectedCount(List<Service> services) {
    int result = 0;
    for (Service service : services) {
      if (service.isHasCrmLink()) {
        ++result;
      }
    }
    return result;
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

  private List<Category> filterFacetValues(List<Service> services, Taxonomy taxonomy) {
    if (taxonomy == null || taxonomy.getCategories() == null) {
      return Collections.emptyList();
    }

    for (Category category : taxonomy.getCategories()) {
      if (category.getValues() == null) {
        continue;
      }
      for (CategoryValue value : category.getValues()) {
        int count = 0;
        for (Service service : services) {
          List<CategoryValue> categoryValues = service.getCategories().get(category);
          if (categoryValues != null && categoryValues.contains(value)) {
            ++count;
          }
        }
        value.setCount(count);
      }
    }
    return taxonomy.getCategories();
  }
}
