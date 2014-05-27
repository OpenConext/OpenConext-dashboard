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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Category;
import nl.surfnet.coin.csa.model.CategoryValue;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor;
import nl.surfnet.coin.selfservice.service.DashboardApp;
import nl.surfnet.coin.selfservice.service.EdugainService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.PersonMainAttributes;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

/**
 * Controller of the homepage showing 'my apps' (or my services, meaning the
 * services that belong to you as a user with a specific role)
 */
@Controller
public class HomeController extends BaseController {

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Resource
  private Csa csa;

  @Resource
  private Cruncher cruncher;

  @Resource
  EdugainService edugainService;

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  @RequestMapping("/app-overview.shtml")
  public ModelAndView home(@RequestParam(value = "switchIdpId", required = false) String switchIdpId,
                           @RequestParam(value = "view", defaultValue = "card") String view, HttpServletRequest request) {
    Map<String, Object> model = new HashMap<>();
    InstitutionIdentityProvider identityProvider;
    if (StringUtils.hasText(switchIdpId)) {
      identityProvider = switchIdp(request, switchIdpId);
    } else {
      identityProvider = getSelectedIdp(request);
    }
    List<Service> services = csa.getServicesForIdp(identityProvider.getId());

    /*
     * Strange but we need to do this to get the facet / connected / licensed numbers. Alternative is worst and let the AuthorityScopeInterceptor call the HomeController
     */
    services = AuthorityScopeInterceptor.scopeListOfServices(services);

    model.put("surfnetCount", services.size());

    addLastLoginDateToServices(services, identityProvider.getId());
    final List<DashboardApp> dashboardApps = new ArrayList<>();

    Set<String> entityIds = new HashSet<>();
    for (Service service: services) {
      DashboardApp dashboardApp = new DashboardApp();
      BeanUtils.copyProperties(service, dashboardApp);
      dashboardApps.add(dashboardApp);
      entityIds.add(service.getSpEntityId());
    }

    final List<DashboardApp> edugainApps = edugainService.getApps(entityIds);
    model.put("edugainCount", edugainApps.size());
    dashboardApps.addAll(edugainApps);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    model.put("personAttributeLabels", attributeLabelMap);
    model.put("view", view);
    model.put("showFacetSearch", true);

    addLicensedConnectedLoginCounts(model, dashboardApps);

    List<Category> facets = csa.getTaxonomy().getCategories();
    this.filterFacetValues(dashboardApps, facets);
    model.put("facets", facets);
    model.put("facetsUsed", this.isCategoryValuesUsed(facets));

    model.put(SERVICES, dashboardApps);
    return new ModelAndView("app-overview", model);
  }

  private void addLastLoginDateToServices(List<Service> services, String selectedIdpId) {
    List<SpStatistic> loginsForUser = cruncher.getRecentLoginsForUser(SpringSecurity.getCurrentUser().getUid(), selectedIdpId);
    for (SpStatistic spStatistic : loginsForUser) {
      Service service = getServiceBySpEntityId(services, spStatistic.getSpEntityId());
      if (service != null) {
        service.setLastLoginDate(new Date(spStatistic.getEntryTime()));
      }
    }
  }

  private Service getServiceBySpEntityId(List<Service> services, String spEntityId) {
    for (Service service : services) {
      if (service.getSpEntityId().equalsIgnoreCase(spEntityId)) {
        return service;
      }
    }
    //corner-case, but can happen in theory
    return null;
  }

  private boolean isCategoryValuesUsed(List<Category> categories) {
    if (CollectionUtils.isNotEmpty(categories)) {
      for (Category cat : categories) {
        if (cat.isUsedFacetValues()) {
          return true;
        }
      }
    }
    return false;
  }

  @RequestMapping("/user.shtml")
  public ModelAndView user() {
    Map<String, Object> model = new HashMap<>();
    CoinUser user = SpringSecurity.getCurrentUser();
    model.put("mainAttributes", new PersonMainAttributes(user.getAttributeMap()));
    return new ModelAndView("user", model);
  }

  @RequestMapping(value = "/closeNotificationPopup.shtml")
  public void closeNotificationPopup(HttpServletRequest request) {
    notificationPopupClosed(request);
  }

  private void filterFacetValues(List<? extends Service> services, List<Category> categories) {

    for (Category category : categories) {
      if (category.getValues() == null) {
        continue;
      }
      for (CategoryValue categoryValue : category.getValues()) {
        int count = 0;
        for (Service service : services) {
          if (service.getCategories() == null || service.getCategories().isEmpty()) {
            continue;
          }
          for (Category c : service.getCategories()) {
            if (c.containsValue(categoryValue.getValue())) {
              ++count;
            }
          }
        }
        categoryValue.setCount(count);
      }
    }
  }

  private void addLicensedConnectedLoginCounts(Map<String, Object> model, Collection<? extends Service> services) {
    int connectedCount = getConnectedCount(services);
    model.put("connectedCount", connectedCount);
    model.put("notConnectedCount", services.size() - connectedCount);

    int licensedCount = getLicensedCount(services);
    model.put("licensedCount", licensedCount);
    model.put("notLicensedCount", services.size() - licensedCount);

    int recentlyLoggedInCount = getRecentlyLoggedInCount(services);

    model.put("recentLoginCount", recentlyLoggedInCount);
    model.put("noRecentLoginCount", services.size() - recentlyLoggedInCount);
  }

  private int getLicensedCount(Collection<? extends Service> services) {
    int result = 0;
    for (Service service : services) {
      if (service.getLicense() != null) {
        ++result;
      }
    }
    return result;
  }

  private int getConnectedCount(Collection<? extends Service> services) {
    int result = 0;
    for (Service service : services) {
      if (service.isConnected()) {
        ++result;
      }
    }
    return result;
  }

  private int getRecentlyLoggedInCount(Collection<? extends Service> services) {
    int result = 0;
    for (Service service : services) {
      if (service.getLastLoginDate() != null) {
        ++result;
      }
    }
    return result;
  }


}
