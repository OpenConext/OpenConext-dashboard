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
import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.GroupContext;
import nl.surfnet.coin.selfservice.domain.GroupContext.Group20Wrap;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.EmailService;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.EmailServiceImpl;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.beans.factory.annotation.Autowired;
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

  @Resource(name = "providerService")
  private ServiceProviderService providerService;

  @Resource
  private CompoundSPService compoundSPService;

  @Resource(name = "oAuthTokenService")
  private OAuthTokenService oAuthTokenService;

  @Resource(name = "emailService")
  private EmailService emailService;

  @Autowired
  private OpenConextOAuthClient apiClient;

  @Autowired
  private ConsentDao consentDao;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Value("${lmngDeepLinkBaseUrl}")
  private String lmngDeepLinkBaseUrl;

  @Value("${maxRecommendationEmails}")
  private int maxRecommendationEmails = 20;

  /**
   * Controller for detail page.
   * 
   * @param compoundSpId
   *          the compound service provider id
   * @return ModelAndView
   */
  @RequestMapping(value = "/app-detail")
  public ModelAndView serviceDetail(@RequestParam(value = "compoundSpId") long compoundSpId,
      @RequestParam(required = false) String revoked,
      @RequestParam(value = "refreshCache", required = false, defaultValue = "false") String refreshCache,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    CompoundServiceProvider compoundServiceProvider = compoundSPService
        .getCSPById(selectedidp, compoundSpId, Boolean.valueOf(refreshCache));
    m.put(COMPOUND_SP, compoundServiceProvider);

    String spEntityId = compoundServiceProvider.getServiceProviderEntityId();
    final Boolean mayHaveGivenConsent = consentDao.mayHaveGivenConsent(SpringSecurity.getCurrentUser().getUid(), spEntityId);
    m.put("mayHaveGivenConsent", mayHaveGivenConsent);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    m.put("personAttributeLabels", attributeLabelMap);

    final List<OAuthTokenInfo> oAuthTokens = oAuthTokenService.getOAuthTokenInfoList(SpringSecurity.getCurrentUser().getUid(),
        compoundServiceProvider.getServiceProvider());

    m.put("oAuthTokens", oAuthTokens);

    m.put("revoked", revoked);

    m.put("lmngDeepLinkUrl", lmngDeepLinkBaseUrl);

    return new ModelAndView("app-detail", m);
  }

  @RequestMapping(value = "/app-recommend")
  public ModelAndView recommendApp(@RequestParam(value = "compoundSpId") long compoundSpId,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();

    CompoundServiceProvider compoundServiceProvider = compoundSPService.getCSPById(selectedidp, compoundSpId, false);
    m.put(COMPOUND_SP, compoundServiceProvider);
    m.put("maxRecommendationEmails", maxRecommendationEmails);
    return new ModelAndView("app-recommend", m);
  }

  @RequestMapping(value = "/do-app-recommend", method = RequestMethod.POST)
  public @ResponseBody
  String doRecommendApp(@RequestParam(value = "compoundSpId") long compoundSpId,
      @RequestParam(value = "recommendPersonalNote", required = false) String recommendPersonalNote,
      @RequestParam(value = "emailSelect2") String emailSelect2, @RequestParam(value = "detailAppStoreLink") String detailAppStoreLink,
      @CookieValue(value = "org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE", required = false) String localeAbbr,
      @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp, HttpServletRequest request) {
    recommendPersonalNote = StringUtils.hasText(recommendPersonalNote) ? ((recommendPersonalNote.replace("\n\r", "").trim().length() == 0) ? null
        : recommendPersonalNote)
        : null;
    if (!StringUtils.hasText(emailSelect2)) {
      throw new RuntimeException("Required field emails addresses");
    }
    String[] recipients = emailSelect2.split(",");
    Locale locale = StringUtils.hasText(localeAbbr) ? new Locale(localeAbbr) : new Locale("en");
    CoinUser coinUser = SpringSecurity.getCurrentUser();

    CompoundServiceProvider csp = compoundSPService.getCSPById(selectedidp, compoundSpId, Boolean.FALSE);

    String subject = coinUser.getDisplayName() + " would like to recommend " + csp.getSp().getName();

    Map<String, Object> templateVars = new HashMap<String, Object>();
    templateVars.put("compoundSp", csp);
    templateVars.put("recommendPersonalNote", recommendPersonalNote);
    templateVars.put("invitername", coinUser.getDisplayName());

    String baseUrl = getBaseUrl(request);

    templateVars.put("appstoreURL", baseUrl + detailAppStoreLink);

    emailService.sendTemplatedMultipartEmail(subject, EmailServiceImpl.RECOMMENTATION_EMAIL_TEMPLATE, locale, Arrays.asList(recipients),
        coinUser.getEmail(), templateVars);
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
      @RequestParam(value = "spEntityId") String spEntityId, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    oAuthTokenService.revokeOAuthTokens(SpringSecurity.getCurrentUser().getUid(), sp);
    return new RedirectView("app-detail.shtml?compoundSpId=" + compoundSpId + "&revoked=true");
  }
}
