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

import java.util.List;
import java.util.Locale;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.LocaleResolver;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.coin.selfservice.domain.MenuItem;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

/**
 * Abstract controller used to set model attributes to the request
 */
@Controller
public abstract class BaseController {

  @Resource(name = "providerService")
  private IdentityProviderService idpService;

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "idps")
  public List<IdentityProvider> getMyInstitutionIdps() {
    return SpringSecurity.getCurrentUser().getInstitutionIdps();
  }

  @ModelAttribute(value = "locale")
  public Locale getLocale(HttpServletRequest request) {
    return localeResolver.resolveLocale(request);
  }

  /**
   * Exposes the requested IdP for use in RequestMapping methods.
   *
   * @param idpId   the idp selected in the view
   * @param request HttpServletRequest, for storing/retrieving the selected idp in the http session.
   * @return the IdentityProvider selected, or null in case of unknown/invalid idpId
   */
  @ModelAttribute(value = "selectedidp")
  public IdentityProvider getRequestedIdp(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    final Object selectedidp = request.getSession().getAttribute("selectedidp");
    if (idpId == null && selectedidp != null) {
      return (IdentityProvider) selectedidp;
    }
    if (idpId == null) {
      idpId = SpringSecurity.getCurrentUser().getIdp();
    }
    for (IdentityProvider idp : SpringSecurity.getCurrentUser().getInstitutionIdps()) {
      if (idp.getId().equals(idpId)) {
        request.getSession().setAttribute("selectedidp", idp);
        return idp;
      }
    }
    throw new RuntimeException("There is no Selected IdP");
  }

  /**
   * Exposes the current role of the user for use in RequestMapping methods
   *
   * @param role    the name of the {@link GrantedAuthority}
   * @param request {@link HttpServletRequest}, for storing/retrieving the selected role in the http session
   * @return the name of the selected {@link GrantedAuthority}
   */
  @ModelAttribute(value = "currentrole")
  public String getCurrentRole(@RequestParam(required = false) String role, HttpServletRequest request) {
    final Object currentrole = request.getSession().getAttribute("currentrole");
    if (role == null && currentrole != null) {
      return (String) currentrole;
    }
    if (role == null) {
      role = CoinAuthority.Authority.ROLE_USER.name();
    }
    for (GrantedAuthority ga : SpringSecurity.getCurrentUser().getAuthorities()) {
      if (role.equals(ga.getAuthority())) {
        request.getSession().setAttribute("currentrole", role);
        return role;
      }
    }
    throw new RuntimeException("There is no current role");
  }


}
