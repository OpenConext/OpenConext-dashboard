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

package nl.surfnet.coin.selfservice.control.shopadmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller that handles the CSP status page (used for the shopmanager to get
 * an overview of all services for a specific IDP)
 */
@Controller
@RequestMapping(value = "/shopadmin/*")
public class CspStatusController extends BaseController {

  @Resource
  private CompoundSPService compoundSPService;

  @Resource(name = "providerService")
  private IdentityProviderService idpService;

  @ModelAttribute(value = "allIdps")
  public List<IdentityProvider> getAllIdps() {
    return idpService.getAllIdentityProviders();
  }

  @RequestMapping("/csp-status-overview.shtml")
  public ModelAndView allCspsForSelectedIdp(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    Map<String, Object> model = new HashMap<String, Object>();

    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
    model.put(COMPOUND_SPS, services);
    model.put("filteredIdp", selectedidp.getId());

    return new ModelAndView("shopadmin/csp-status-overview", model);
  }

  @RequestMapping(value = "/selectIdp", method = RequestMethod.POST)
  public ModelAndView selectIdp(HttpServletRequest req) {
    Map<String, Object> model = new HashMap<String, Object>();

    String filteredIdpId = (String) req.getParameter("filteredIdpId");
    if (filteredIdpId != null) {
      IdentityProvider selectedidp = idpService.getIdentityProvider(filteredIdpId);
      List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
      model.put(COMPOUND_SPS, services);
      model.put("filteredIdp", selectedidp.getId());
    }

    return new ModelAndView("shopadmin/csp-status-overview", model);
  }

}
