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

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.api.client.domain.Person;
import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProviderRepresenter;
import nl.surfnet.coin.selfservice.domain.GroupContext;
import nl.surfnet.coin.selfservice.domain.GroupContext.Entry;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @Autowired
  private OpenConextOAuthClient apiClient;

  @Autowired
  private ConsentDao consentDao;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  @Value("${lmngDeepLinkBaseUrl}")
  private String lmngDeepLinkBaseUrl;

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

    return new ModelAndView("app-recommend", m);
  }

  @RequestMapping("/groupsWithMembers.json")
  public @ResponseBody
  List<GroupContext.Entry> groupsWithMembers() {
    CoinUser coinUser = SpringSecurity.getCurrentUser();
    List<Group20> groups = apiClient.getGroups20(coinUser.getUid(), coinUser.getUid());
    GroupContext groupsWithMembers = new GroupContext();
    for (Group20 group : groups) {
      List<Person> members = apiClient.getGroupMembers(group.getId(), coinUser.getUid());
      groupsWithMembers.addGroup(group, members);
    }
    return groupsWithMembers.getEntries();
  }

  @RequestMapping(value = "revokekeys.shtml")
  public RedirectView revokeKeys(@RequestParam(value = "compoundSpId") long compoundSpId,
      @RequestParam(value = "spEntityId") String spEntityId, @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    oAuthTokenService.revokeOAuthTokens(SpringSecurity.getCurrentUser().getUid(), sp);
    return new RedirectView("app-detail.shtml?compoundSpId=" + compoundSpId + "&revoked=true");
  }
}
