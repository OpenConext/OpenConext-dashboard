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

package nl.surfnet.coin.selfservice.control.idpadmin;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for SP detail pages
 *
 * @deprecated All requests for detail pages should be handled by a single controller, {@link nl.surfnet.coin.selfservice.control.ServiceDetailController}
 */
@Controller
@RequestMapping("/idpadmin/sp")
@SessionAttributes(value = { "linkrequest", "unlinkrequest" })
@Deprecated
public class SpDetailController extends BaseController {

  @Resource(name = "providerService")
  private ServiceProviderService providerService;

  @Resource(name = "jiraService")
  private JiraService jiraService;

  @Resource(name = "actionsService")
  private ActionsService actionsService;

  @Resource(name = "notificationService")
  private NotificationService notificationService;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  /**
   * Controller for detail page.
   * 
   * @param spEntityId
   *          the entity id
   * @return ModelAndView
   */
  @RequestMapping(value = "/detail.shtml")
  public ModelAndView spDetail(@RequestParam String spEntityId, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    m.put("sp", sp);
    return new ModelAndView("idpadmin/sp-detail", m);
  }

}
