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

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.GroupContext;
import nl.surfnet.coin.selfservice.domain.GroupContext.Group20Wrap;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.impl.EmailServiceImpl;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.AjaxResponseException;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Controller for the detail view(s) of a service (provider)
 */
@Controller
@RequestMapping
public class ServiceDetailController extends BaseController {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceDetailController.class);

  @Resource(name = "emailService")
  private EmailService emailService;

  @Resource
  private OpenConextOAuthClient apiClient;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Value("${lmngDeepLinkBaseUrl}")
  private String lmngDeepLinkBaseUrl;

  @Value("${maxRecommendationEmails}")
  private int maxRecommendationEmails = 20;

  @Resource
  private Csa csa;

  /**
   * Controller for detail page.
   *
   * @param serviceId the service  id
   */
  @RequestMapping(value = "/app-detail")
  public ModelAndView serviceDetail(@RequestParam(value = "serviceId", required = false) Long serviceId,
                                    @RequestParam(value = "spEntityId", required = false) String spEntityId,
                                    @RequestParam(required = false) String revoked,
                                    HttpServletRequest request) {
    if (null == serviceId && !StringUtils.hasText(spEntityId)) {
      throw new IllegalArgumentException("either service id or sp entity id is required");
    }
    InstitutionIdentityProvider selectedIdp = getSelectedIdp(request);
    Service service = null;
    if (null != spEntityId) {
      service = csa.getServiceForIdp(selectedIdp.getId(), spEntityId);
    } else {
      service = csa.getServiceForIdp(selectedIdp.getId(), serviceId);
    }
    Map<String, Object> m = new HashMap<String, Object>();
    m.put(SERVICE, service);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    m.put("personAttributeLabels", attributeLabelMap);

    m.put("lmngDeepLinkUrl", lmngDeepLinkBaseUrl);

    return new ModelAndView("app-detail", m);
  }

  @RequestMapping(value = "/app-recommend")
  public ModelAndView recommendApp(@RequestParam(value = "serviceId") long serviceId, HttpServletRequest request) {
    Map<String, Object> m = new HashMap<String, Object>();
    InstitutionIdentityProvider selectedIdp = getSelectedIdp(request);
    Service service = csa.getServiceForIdp(selectedIdp.getId(), serviceId);
    m.put(SERVICE, service);
    m.put("maxRecommendationEmails", maxRecommendationEmails);
    return new ModelAndView("app-recommend", m);
  }

  @RequestMapping(value = "/do-app-recommend", method = RequestMethod.POST)
  public
  @ResponseBody
  String doRecommendApp(
          @RequestParam(value = "serviceId") long serviceId,
          @RequestParam(value = "recommendPersonalNote", required = false) String recommendPersonalNote,
          @RequestParam(value = "emailSelect2") String emailSelect2,
          @RequestParam(value = "detailAppStoreLink") String detailAppStoreLink,
          @CookieValue(value = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE", required = false) String localeAbbr,
          HttpServletRequest request) {
    recommendPersonalNote = StringUtils.hasText(recommendPersonalNote) ? ((recommendPersonalNote.replace("\n\r", "")
            .trim().length() == 0) ? null : recommendPersonalNote) : null;
    if (!StringUtils.hasText(emailSelect2)) {
      throw new AjaxResponseException("Required field emails addresses");
    }
    String[] recipients = emailSelect2.split(",");
    Locale locale = StringUtils.hasText(localeAbbr) ? new Locale(localeAbbr) : new Locale("en");
    CoinUser coinUser = SpringSecurity.getCurrentUser();
    InstitutionIdentityProvider selectedIdp = getSelectedIdp(request);
    Service service = csa.getServiceForIdp(selectedIdp.getId(), serviceId);

    String subject = coinUser.getDisplayName() + " would like to recommend " + service.getName();

    Map<String, Object> templateVars = new HashMap<String, Object>();
    templateVars.put("service", service);
    templateVars.put("recommendPersonalNote", recommendPersonalNote);
    templateVars.put("invitername", coinUser.getDisplayName());

    String baseUrl = getBaseUrl(request);

    templateVars.put("appstoreURL", baseUrl + detailAppStoreLink);

    emailService.sendTemplatedMultipartEmail(subject, EmailServiceImpl.RECOMMENTATION_EMAIL_TEMPLATE, locale,
            Arrays.asList(recipients), coinUser.getEmail(), templateVars);
    return "ok";
  }

  private String getBaseUrl(HttpServletRequest request) {
    int serverPort = request.getServerPort();
    String baseUrl;
    if (serverPort != 80) {
      baseUrl = String.format("%s://%s:%d%s/", request.getScheme(), request.getServerName(), request.getServerPort(),
              request.getContextPath());
    } else {
      baseUrl = String.format("%s://%s%s/", request.getScheme(), request.getServerName(), request.getContextPath());
    }
    return baseUrl;
  }

  @SuppressWarnings("unchecked")
  @RequestMapping("/groupsWithMembers.json")
  public
  @ResponseBody
  List<Group20Wrap> groupsWithMembers(HttpServletRequest request) {
    List<Group20Wrap> result = (List<Group20Wrap>) request.getSession().getAttribute(GROUPS_WITH_MEMBERS);
    if (result == null) {
      CoinUser coinUser = SpringSecurity.getCurrentUser();
      List<Group20> groups = apiClient.getGroups20(coinUser.getUid(), coinUser.getUid());
      GroupContext groupsWithMembers = new GroupContext();
      for (Group20 group : groups) {
        List<Person> members = apiClient.getGroupMembers(group.getId(), coinUser.getUid());
        groupsWithMembers.addGroup(group, members);
      }
      result = groupsWithMembers.getEntries();
      request.getSession().setAttribute(GROUPS_WITH_MEMBERS, result);
    }
    return result;
  }

}
