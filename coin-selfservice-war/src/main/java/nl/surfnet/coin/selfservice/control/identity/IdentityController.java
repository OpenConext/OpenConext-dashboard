/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.selfservice.control.identity;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentitySwitch;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

@Controller
@RequestMapping("/identity")
public class IdentityController extends BaseController {


  @RequestMapping(value = "/switch.shtml")
  public ModelAndView switchIdentity(HttpServletRequest request, ModelMap modelMap) {
    List<InstitutionIdentityProvider> idps = (List<InstitutionIdentityProvider>) request.getSession().getAttribute(INSTITUTION_IDENTITY_PROVIDERS);

    modelMap.addAttribute("referenceIdentityProviders", referenceIdentityProviders(idps));
    modelMap.addAttribute("referenceRoles", referenceRoles(isDashBoard(request)));

    modelMap.addAttribute("command", new IdentitySwitch());
    return new ModelAndView("identity/switch-identity", modelMap);
  }

  private Boolean isDashBoard(HttpServletRequest request) {
    return (Boolean) request.getAttribute("isDashBoard");
  }

  @RequestMapping(value = "/do-switch.shtml", method = RequestMethod.POST, params = "submit=true")
  public RedirectView doSwitchIdentity(@ModelAttribute("identitySwitch") IdentitySwitch identitySwitch, HttpServletRequest request) {
    CoinAuthority authority = new CoinAuthority(CoinAuthority.Authority.valueOf(identitySwitch.getRole()));
    InstitutionIdentityProvider provider = idpToInstitutionIdentityProvider(request, identitySwitch.getInstitutionName());

    CoinUser currentUser = SpringSecurity.getCurrentUser();
    SpringSecurity.setImpersonatedIdentityProvider(currentUser.getIdp());
    currentUser.setIdp(provider);

    newIdentity(identitySwitch, request, provider);

    clearAuthorities(currentUser, isDashBoard(request));

    currentUser.addAuthority(authority);
    return new RedirectView("/app-overview.shtml", true);
  }

  @RequestMapping(value = "/do-switch.shtml", method = RequestMethod.POST, params = "reset=true")
  public RedirectView resetIdentity(HttpServletRequest request) {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    InstitutionIdentityProvider provider = SpringSecurity.getImpersonatedIdentityProvider();
    currentUser.setIdp(provider);

    newIdentity(null, request, provider);

    clearAuthorities(currentUser, isDashBoard(request));

    return new RedirectView("/app-overview.shtml", true);
  }

  private void newIdentity(IdentitySwitch identitySwitch, HttpServletRequest request, InstitutionIdentityProvider provider) {
    HttpSession session = request.getSession();
    session.setAttribute(SELECTED_IDP, provider);
    session.setAttribute(SWITCHED_IDENTITY_SWITCH, identitySwitch);
    session.setAttribute(NOTIFICATIONS, null);
    session.setAttribute(NOTIFICATION_POPUP_CLOSED, null);
  }

  private InstitutionIdentityProvider idpToInstitutionIdentityProvider(HttpServletRequest request, String idp) {
    List<InstitutionIdentityProvider> idps = (List<InstitutionIdentityProvider>) request.getSession().getAttribute(INSTITUTION_IDENTITY_PROVIDERS);
    for (InstitutionIdentityProvider identityProvider : idps) {
      if (identityProvider.getId().equalsIgnoreCase(idp)) {
        return identityProvider;
      }
    }
    throw new IllegalArgumentException("InstitutionIdentityProvider '" + idp + "' is not a valid InstitutionIdentityProvider");
  }

  private void clearAuthorities(CoinUser currentUser, Boolean isDashBoard) {
    currentUser.getAuthorities().clear();
    currentUser.addAuthority(new CoinAuthority(isDashBoard ? CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER : CoinAuthority.Authority.ROLE_SHOWROOM_SUPER_USER));
  }

  private Map<String, String> referenceIdentityProviders(List<InstitutionIdentityProvider> idps) {
    Map<String, String> result = new LinkedHashMap<String, String>();
    for (InstitutionIdentityProvider idp : idps) {
      result.put(idp.getId(), idp.getName());
    }
    return result;
  }

  /*
   * Maybe not as fancy as possible but explicit and dry so acceptable
   */
  private Collection<String> referenceRoles(Boolean isDashBoard) {
    List<String> roles = new ArrayList<String>();
    if (isDashBoard) {
      roles.add(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER.name());
      roles.add(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN.name());
    } else {
      roles.add(CoinAuthority.Authority.ROLE_SHOWROOM_USER.name());
      roles.add(CoinAuthority.Authority.ROLE_SHOWROOM_ADMIN.name());
    }
    return roles;
  }


}
