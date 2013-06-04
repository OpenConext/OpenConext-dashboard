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

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.GroupContext;
import nl.surfnet.coin.selfservice.domain.GroupContext.Group20Wrap;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.impl.EmailServiceImpl;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.AjaxResponseException;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.apache.commons.lang.NotImplementedException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

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
   * @param serviceId
   *          the service  id
   */
  @RequestMapping(value = "/app-detail")
  public ModelAndView serviceDetail(@RequestParam(value = "id", required = false) Long serviceId,
      @RequestParam(value="spEntityId", required = false) String spEntityId,
      @RequestParam(required = false) String revoked,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    if (null == serviceId && !StringUtils.hasText(spEntityId)) {
      throw new IllegalArgumentException("either service id or sp entity id is required");
    }
    Service service = null;
    if (null != spEntityId) {
      service = csa.getServiceForIdp(selectedidp.getId(), spEntityId);
    } else {
      service = csa.getServiceForIdp(selectedidp.getId(), serviceId);
    }
    Map<String, Object> m = new HashMap<String, Object>();
    m.put(SERVICE, service);

    spEntityId = service.getSpEntityId();
    if ((Boolean) (request.getAttribute("statisticsAvailable"))) {
      // FIXME: integrate with CSA
//      final Boolean mayHaveGivenConsent = consentDao.mayHaveGivenConsent(SpringSecurity.getCurrentUser().getUid(),
//          spEntityId);
//      m.put("mayHaveGivenConsent", mayHaveGivenConsent);
    }

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    m.put("personAttributeLabels", attributeLabelMap);

    if ((Boolean) (request.getAttribute("oauthTokensAvailable"))) {
      // FIXME integration with oauth token service using CSA
//      final List<OAuthTokenInfo> oAuthTokens = oAuthTokenService.getOAuthTokenInfoList(SpringSecurity.getCurrentUser()
//          .getUid(), compoundServiceProvider.getServiceProvider());
//      m.put("oAuthTokens", oAuthTokens);

      m.put("revoked", revoked);
    }

    m.put("lmngDeepLinkUrl", lmngDeepLinkBaseUrl);

    return new ModelAndView("app-detail", m);
  }

  @RequestMapping(value = "/app-recommend")
  public ModelAndView recommendApp(@RequestParam(value = "id") long serviceId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();

    Service service = csa.getServiceForIdp(selectedidp.getId(), serviceId);
    m.put(SERVICE, service);
    m.put("maxRecommendationEmails", maxRecommendationEmails);
    return new ModelAndView("app-recommend", m);
  }

  @RequestMapping(value = "/do-app-recommend", method = RequestMethod.POST)
  public @ResponseBody
  String doRecommendApp(
      @RequestParam(value = "id") long serviceId,
      @RequestParam(value = "recommendPersonalNote", required = false) String recommendPersonalNote,
      @RequestParam(value = "emailSelect2") String emailSelect2,
      @RequestParam(value = "detailAppStoreLink") String detailAppStoreLink,
      @CookieValue(value = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE", required = false) String localeAbbr,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    recommendPersonalNote = StringUtils.hasText(recommendPersonalNote) ? ((recommendPersonalNote.replace("\n\r", "")
        .trim().length() == 0) ? null : recommendPersonalNote) : null;
    if (!StringUtils.hasText(emailSelect2)) {
      throw new AjaxResponseException("Required field emails addresses");
    }
    String[] recipients = emailSelect2.split(",");
    Locale locale = StringUtils.hasText(localeAbbr) ? new Locale(localeAbbr) : new Locale("en");
    CoinUser coinUser = SpringSecurity.getCurrentUser();

    Service service = csa.getServiceForIdp(selectedidp.getId(), serviceId);

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
  public @ResponseBody
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

  @RequestMapping(value = "revokekeys.shtml")
  public RedirectView revokeKeys(@RequestParam(value = "compoundSpId") long compoundSpId,
      @RequestParam(value = "spEntityId") String spEntityId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
// FIXME implement this in CSA
//    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
//    oAuthTokenService.revokeOAuthTokens(SpringSecurity.getCurrentUser().getUid(), sp);
    throw new NotImplementedException("Not implemented in CSA");
//    return new RedirectView("app-detail.shtml?compoundSpId=" + compoundSpId + "&revoked=true");
  }
}
