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
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import selfservice.control.BaseController;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.domain.csa.IdentityProvider;
import selfservice.service.IdentityProviderService;
import selfservice.service.impl.CompoundSPService;

/**
 * Controller that handles the CSP status page (used for the shopmanager to get
 * an overview of all services for a specific IDP)
 */
@Controller
@RequestMapping(value = "/shopadmin/*")
public class CspStatusController extends BaseController {

  @Autowired
  private CompoundSPService compoundSPService;

  @Autowired
  private IdentityProviderService idpService;

  @ModelAttribute(value = "allIdps")
  public List<IdentityProvider> getAllIdps() {
    List<IdentityProvider> identityProviders = idpService.getAllIdentityProviders();
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
    IdentityProvider selectedidp = idpService.getIdentityProvider(filteredIdpId).orElseThrow(RuntimeException::new);
    return statusOverview(selectedidp);
  }

  private ModelAndView statusOverview(IdentityProvider selectedidp) {
    Map<String, Object> model = new HashMap<>();
    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
    model.put(COMPOUND_SPS, services);
    model.put("filteredIdp", selectedidp.getId());
    return new ModelAndView("shopadmin/csp-status-overview", model);
  }

}
