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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.csa.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.Collections2;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.PersonMainAttributes;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabPerson;

/**
 * Controller of the homepage showing 'my apps' (or my services, meaning the
 * services that belong to you as a user with a specific role)
 */
@Controller
public class HomeController extends BaseController {

  public static final List<String> INTERESTING_ROLES = Arrays.asList("SURFconextbeheerder", "SURFconextverantwoordelijke");
  private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Resource
  private Csa csa;

  @Resource
  private Cruncher cruncher;

  @Resource
  private Sab sabClient;


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

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    model.put("personAttributeLabels", attributeLabelMap);
    model.put("view", view);
    model.put("showFacetSearch", true);

    addLicensedConnectedLoginCounts(model, services);

    List<Category> facets = csa.getTaxonomy().getCategories();
    this.filterFacetValues(services, facets);
    model.put("facets", facets);
    model.put("facetsUsed", this.isCategoryValuesUsed(facets));

    model.put(SERVICES, services);
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
  @ResponseBody
  public String closeNotificationPopup(HttpServletRequest request) {
    notificationPopupClosed(request);
    return ""; // prevents further view-resolution
  }

  @RequestMapping("/idp.shtml")
  public ModelAndView idp(HttpServletRequest httpServletRequest) {
    InstitutionIdentityProvider currentIdp = getSelectedIdp(httpServletRequest);
    ModelMap model = new ModelMap();

    addRoleAssignmentsToModel(currentIdp, model);
    addOfferedServiceToModel(currentIdp, model);

    return new ModelAndView("idp", model);
  }

  private void addOfferedServiceToModel(InstitutionIdentityProvider currentIdp, ModelMap model) {
    List<OfferedService> offeredServices = csa.findOfferedServicesFor(currentIdp.getId());
    LOG.debug("CSA returned {} offered services", offeredServices.size());
    model.addAttribute("offeredServicePresenter", new OfferedServicePresenter(offeredServices));
  }

  private void addRoleAssignmentsToModel(InstitutionIdentityProvider currentIdp, ModelMap model) {
    Map<String, String> roleAssignments = new HashMap<>();
    for (final String role: INTERESTING_ROLES) {
      final Collection<SabPerson> personsInRoleForOrganization = sabClient.getPersonsInRoleForOrganization(currentIdp.getName(), role);
      Collection<String> fullNames = Collections2.transform(personsInRoleForOrganization, new Function<SabPerson, String>() {
        public String apply(SabPerson person) {
          return person.fullname();
        }
      });

      final List<String> sortedFullnames = new ArrayList<>(fullNames);
      Collections.sort(sortedFullnames);
      roleAssignments.put(role, Joiner.on(", ").join(sortedFullnames));
    }
    model.put("roleAssignments", roleAssignments);
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
