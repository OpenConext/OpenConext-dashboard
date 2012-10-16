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

import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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

  @Resource(name = "oAuthTokenService")
  private OAuthTokenService oAuthTokenService;

  @Resource(name = "licensingService")
  private LicensingService licensingService;
  
  @Autowired
  private ConsentDao consentDao;

  @Resource(name = "personAttributeLabelService")
  private PersonAttributeLabelServiceJsonImpl personAttributeLabelService;

  /**
   * Controller for detail page.
   *
   * @param spEntityId the entity id
   * @return ModelAndView
   */
  @RequestMapping(value = "/app-detail")
  public ModelAndView serviceDetail(@RequestParam String spEntityId,
                                    @RequestParam(required = false) String revoked,
                                    @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    Map<String, Object> m = new HashMap<String, Object>();
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    if (sp.isLinked()) {
      m.put("sp", sp);
    }
    m.put("menu", buildMenu(MenuType.USER, "linked-services"));

    final Boolean mayHaveGivenConsent = consentDao.mayHaveGivenConsent(SpringSecurity.getCurrentUser().getUid(), spEntityId);
    m.put("mayHaveGivenConsent", mayHaveGivenConsent);

    final Map<String, PersonAttributeLabel> attributeLabelMap = personAttributeLabelService.getAttributeLabelMap();
    m.put("personAttributeLabels", attributeLabelMap);

    final List<OAuthTokenInfo> oAuthTokens = oAuthTokenService.getOAuthTokenInfoList(SpringSecurity.getCurrentUser().getUid(), sp);
    m.put("oAuthTokens", oAuthTokens);

    m.put("revoked", revoked);
    
    List<License> licenses = licensingService.getLicensesForIdentityProviderAndServiceProvider(selectedidp, sp);
    m.put("licenses", licenses);
    
    return new ModelAndView("app-detail", m);
  }


  @RequestMapping(value = "revokekeys.shtml")
  public RedirectView revokeKeys(@RequestParam String spEntityId,
                                 @ModelAttribute(value = "selectedidp") IdentityProvider selectedidp) {
    final ServiceProvider sp = providerService.getServiceProvider(spEntityId, selectedidp.getId());
    oAuthTokenService.revokeOAuthTokens(SpringSecurity.getCurrentUser().getUid(), sp);
    return new RedirectView("app-detail.shtml?revoked=true&spEntityId=" + spEntityId);
  }
}
