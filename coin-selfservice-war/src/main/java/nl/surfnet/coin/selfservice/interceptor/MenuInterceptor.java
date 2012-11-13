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

package nl.surfnet.coin.selfservice.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.coin.selfservice.domain.MenuItem;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * Interceptor to add the menu
 */
public class MenuInterceptor extends LmngActiveAwareInterceptor {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
      throws Exception {

    if (modelAndView != null) {
      final ModelMap map = modelAndView.getModelMap();
      Menu menu = createMenu();
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

  private Menu createMenu() {
    Menu menu = new Menu();
    menu.addMenuItem(new MenuItem("jsp.home.title", "/app-overview.shtml"));
    List<Authority> authorities = SpringSecurity.getCurrentUser().getAuthorityEnums();
    for (Authority authority : authorities) {
      switch (authority) {
      case ROLE_DISTRIBUTION_CHANNEL_ADMIN:
        if (isLmngActive()) {
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allidplmng.title", "/shopadmin/all-idpslmng.shtml"));
        } else {
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spsconfig.shtml"));
        }
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/requests-overview.shtml"));
        menu.addMenuItem(new MenuItem("jsp.stats.title", "/shopadmin/stats.shtml"));
        break;
      case ROLE_IDP_LICENSE_ADMIN:
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/requests-overview.shtml"));
        menu.addMenuItem(new MenuItem("jsp.stats.title", "/shopadmin/stats.shtml"));
        break;
      case ROLE_IDP_SURFCONEXT_ADMIN:
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/requests-overview.shtml"));
        menu.addMenuItem(new MenuItem("jsp.stats.title", "/shopadmin/stats.shtml"));
        break;
      default:
        break;
      }
    }
    menu.addMenuItem(new MenuItem("jsp.profile.title", "/user.shtml"));
    return menu;
  }

}
