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

import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.coin.selfservice.domain.MenuItem;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

/**
 * Interceptor to add the menu
 */
public class MenuInterceptor extends HandlerInterceptorAdapter {

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
    List<Authority> authorities = SpringSecurity.getCurrentUser().getAuthorityEnums();
    for (Authority authority : authorities) {
      switch (authority) {
        //same menu items, but different rights
        case ROLE_DASHBOARD_ADMIN:
        case ROLE_DASHBOARD_VIEWER:
          menu.addMenuItem(new MenuItem("jsp.home.title", "/app-overview.shtml"));
          menu.addMenuItem(new MenuItem("jsp.notifications.title", "/notifications.shtml"));
          menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/history.shtml"));
          menu.addMenuItem(new MenuItem("jsp.stats.title", "/stats/stats.shtml"));
          menu.addMenuItem(new MenuItem("jsp.idp.title", "/idp.shtml"));
          break;
        default:
          break;
      }
    }
    return menu;
  }


}
