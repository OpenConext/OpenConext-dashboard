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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

@Controller
public class SpListController {


  @Resource(name="providerService")
  private ServiceProviderService providerService;

  @RequestMapping(value="/linked-sps")
  public ModelAndView listLinkedSps() {
    Map<String, Object> m = new HashMap<String, Object>();

    // Add SP's for all idps; put in a Set to filter out duplicates
    Set<ServiceProvider> sps = new HashSet<ServiceProvider>();
    for (String idpId : getCurrentUser().getInstitutionIdps()) {
      sps.addAll(providerService.getLinkedServiceProviders(idpId));
    }
    m.put("sps", new ArrayList<ServiceProvider>(sps));
    m.put("activeSection", "linked-sps");
    return new ModelAndView("sp-overview", m);
  }

  @RequestMapping(value="/all-sps")
  public ModelAndView listAllSps() {
    Map<String, Object> m = new HashMap<String, Object>();

    // Add SP's for all idps; put in a Set to filter out duplicates
    Set<ServiceProvider> sps = new HashSet<ServiceProvider>();
    for (String idpId : getCurrentUser().getInstitutionIdps()) {
      sps.addAll(providerService.getAllServiceProviders(idpId));
    }
    m.put("sps", new ArrayList<ServiceProvider>(sps));


    m.put("activeSection", "all-sps");

    return new ModelAndView("sp-overview", m);
  }

  /**
   * Controller for detail page.
   * @param spEntityId
   * @return
   */
  @RequestMapping(value="/sp/detail.shtml")
  public ModelAndView spDetail(@RequestParam String spEntityId) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId);
    m.put("sp", sp);
    return new ModelAndView("sp-detail", m);
  }

  /**
   * Get the IDP Entity Id from the security context.
   * @return String
   * @throws SecurityException in case no principal is found.
   */
  private static CoinUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      throw new SecurityException("No suitable security context.");
    }
    Object principal = auth.getPrincipal();
    if (principal != null && principal instanceof CoinUser) {
      return (CoinUser) principal;
    }
    throw new SecurityException("No suitable security context.");
  }
}
