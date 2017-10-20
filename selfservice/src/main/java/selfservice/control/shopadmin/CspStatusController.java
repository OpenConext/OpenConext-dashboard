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

package selfservice.control.shopadmin;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import selfservice.domain.IdentityProvider;
import selfservice.service.impl.CompoundServiceProviderService;
import selfservice.serviceregistry.ServiceRegistry;

/**
 * Controller that handles the CSP status page (used for the shopmanager to get
 * an overview of all services for a specific IDP)
 */
@Controller
@RequestMapping(value = "/shopadmin/*")
public class CspStatusController extends BaseController {

  @Autowired
  private CompoundServiceProviderService cspService;

  @Autowired
  private ServiceRegistry serviceRegistry;

  @ModelAttribute(value = "allIdps")
  public List<IdentityProvider> getAllIdps() {
    List<IdentityProvider> identityProviders = serviceRegistry.getAllIdentityProviders();
    Collections.sort(identityProviders);
    return identityProviders;
  }

  @RequestMapping("/csp-status-overview.shtml")
  public ModelAndView allCspsForSelectedIdp(HttpServletRequest request) {
    IdentityProvider selectedidp = getSelectedIdp(request);

    return statusOverview(selectedidp);
  }

  @RequestMapping(value = "/selectIdp", method = RequestMethod.GET)
  public ModelAndView selectIdp(@RequestParam String filteredIdpId) {
    return statusOverview(serviceRegistry.getIdentityProvider(filteredIdpId).orElseThrow(RuntimeException::new));
  }

  private ModelAndView statusOverview(IdentityProvider selectedidp) {
    Map<String, Object> model = new HashMap<>();
    model.put(COMPOUND_SPS, cspService.getCompoundServiceProvidersByIdp(selectedidp));
    model.put("filteredIdp", selectedidp.getId());

    return new ModelAndView("shopadmin/csp-status-overview", model);
  }

}
