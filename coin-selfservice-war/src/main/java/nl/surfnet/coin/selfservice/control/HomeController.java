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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.PersonMainAttributes;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller of the homepage showing 'my apps' (or my services, meaning the
 * services that belong to you as a user with a specific role)
 * 
 */
@Controller
public class HomeController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(HomeController.class);

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Resource
  private CompoundSPService compoundSPService;

  @Resource
  private NotificationService notificationService;

  @ModelAttribute(value = "personAttributeLabels")
  public Map<String, PersonAttributeLabel> getPersonAttributeLabels() {
    return personAttributeLabelService.getAttributeLabelMap();
  }

  @RequestMapping("/app-overview.shtml")
  public ModelAndView home(@ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    Map<String, Object> model = new HashMap<String, Object>();

    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
    model.put(COMPOUND_SPS, services);

    if (request.getSession().getAttribute("notificationsShown") == null) {
      List<NotificationMessage> notificationMessages = notificationService.getNotifications(services);
      try {
        String jsonNotificationMessages = objectMapper.writeValueAsString(notificationMessages);
        model.put("jsonNotificationMessages", jsonNotificationMessages);
        request.getSession().setAttribute("notificationsShown", Boolean.TRUE);
      } catch (Exception e) {
        LOG.error("Exception while creating json for notificationMessages");
      }
    }

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    model.put("personAttributeLabels", attributeLabelMap);

    return new ModelAndView("app-overview", model);
  }

  @RequestMapping("/user.shtml")
  public ModelAndView user() {
    Map<String, Object> model = new HashMap<String, Object>();
    CoinUser user = SpringSecurity.getCurrentUser();
    model.put("mainAttributes", new PersonMainAttributes(user.getAttributeMap()));
    return new ModelAndView("user", model);
  }
}
