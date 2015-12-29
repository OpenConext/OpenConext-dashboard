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

package selfservice.interceptor;

import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.csa.Menu;
import selfservice.domain.csa.MenuItem;

public class MenuInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    Optional<Authentication> authentication = Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    if (modelAndView != null && authentication.isPresent()) {
      ModelMap map = modelAndView.getModelMap();
      Menu menu = createMenu((CoinUser) authentication.get().getPrincipal());
      setSelected(request, menu);
      map.addAttribute("menu", menu);
    }
  }

  private void setSelected(HttpServletRequest request, Menu menu) {
    String requestURI = request.getRequestURI();
    List<MenuItem> menuItems = menu.getMenuItems();
    for (MenuItem menuItem : menuItems) {
      if (requestURI.endsWith(menuItem.getUrl())) {
        menuItem.setSelected(true);
        break;
      }
    }
  }

  private Menu createMenu(final CoinUser coinUser) {
    Menu menu = new Menu();

    for (CoinAuthority.Authority authority : coinUser.getAuthorityEnums()) {
      switch (authority) {
        case ROLE_DISTRIBUTION_CHANNEL_ADMIN:
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allidplmng.title", "/shopadmin/all-idpslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.taxonomy.title", "/shopadmin/taxonomy-overview.shtml"));
          menu.addMenuItem(new MenuItem("jsp.cspstatus.title", "/shopadmin/csp-status-overview.shtml"));
          menu.addMenuItem(new MenuItem("jsp.license_contact_persons.title", "/shopadmin/license-contact-persons.shtml"));
          break;
        default:
          break;
      }
    }

    return menu;
  }

}
