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

  @Autowired
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
    return null;
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
      role = "ROLE_USER";
    }
    for (GrantedAuthority ga : SpringSecurity.getCurrentUser().getAuthorities()) {
      if (role.equals(ga.getAuthority())) {
        request.getSession().setAttribute("currentrole", role);
        return role;
      }
    }
    return null;
  }

  /**
   * Builds a {@link Menu} based on {@link MenuType} and the identifier of the selected menu item
   *
   * @param type         {@link MenuType}
   * @param selectedItem identifier of the selected menu item, can be {@literal null} or empty
   * @return Menu
   */
  protected static Menu buildMenu(MenuType type, String selectedItem) {
    Menu menu = new Menu(type.toString());
    MenuItem home = new MenuItem("jsp.home.title", "/home.shtml", "home".equals(selectedItem));
    menu.addMenuItem(home);
    switch (type) {
      case IDPADMIN:
        MenuItem allSPs = new MenuItem("jsp.allsp.title", "/idpadmin/all-sps.shtml", "all-sps".equals(selectedItem));
        menu.addMenuItem(allSPs);
        MenuItem action = new MenuItem("jsp.actions.title", "/idpadmin/actions.shtml", "actions".equals(selectedItem));
        menu.addMenuItem(action);
        break;
      case USER:
        MenuItem linkedServices = new MenuItem("jsp.linkedServices.title", "/user/linked-services.shtml",
            "linked-services".equals(selectedItem));
        menu.addMenuItem(linkedServices);
        break;
    }

    return menu;
  }

  public enum MenuType {
    IDPADMIN,
    USER;

    MenuType() {
    }
  }

}
