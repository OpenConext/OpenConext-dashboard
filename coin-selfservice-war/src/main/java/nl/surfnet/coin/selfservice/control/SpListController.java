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

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

@Controller
public class SpListController extends BaseController {


  @Resource(name="providerService")
  private ServiceProviderService providerService;

  @RequestMapping(value="/linked-sps")
  public ModelAndView listLinkedSps(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();

    // Add SP's for all idps; put in a Set to filter out duplicates
    List<ServiceProvider> sps = providerService.getLinkedServiceProviders(selectedidp.getId());
    m.put("sps", new ArrayList<ServiceProvider>(sps));
    m.put("activeSection", "linked-sps");
    return new ModelAndView("sp-overview", m);
  }

  @RequestMapping(value="/all-sps")
  public ModelAndView listAllSps(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();

    // Add SP's for all idps; put in a Set to filter out duplicates
    List<ServiceProvider> sps = providerService.getAllServiceProviders(selectedidp.getId());
    m.put("sps", sps);
    m.put("activeSection", "all-sps");
    return new ModelAndView("sp-overview", m);
  }

}
