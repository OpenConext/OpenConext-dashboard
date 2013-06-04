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
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to add the menu
 */
public class MenuInterceptor extends HandlerInterceptorAdapter {

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
      throws Exception {

    if (modelAndView != null) {
      final ModelMap map = modelAndView.getModelMap();
      Menu menu = createMenu(request);
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
  
  private Menu createMenu(final HttpServletRequest request) {
    Menu menu = new Menu();
    menu.addMenuItem(new MenuItem("jsp.home.title", "/app-overview.shtml"));
    List<Authority> authorities = SpringSecurity.getCurrentUser().getAuthorityEnums();
    for (Authority authority : authorities) {
      switch (authority) {
      case ROLE_DISTRIBUTION_CHANNEL_ADMIN:
        if (isCrmAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allidplmng.title", "/shopadmin/all-idpslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.cspstatus.title", "/shopadmin/csp-status-overview.shtml"));
          menu.addMenuItem(new MenuItem("jsp.taxonomy.title", "/shopadmin/taxonomy-overview.shtml"));
        } else {
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spsconfig.shtml"));
          menu.addMenuItem(new MenuItem("jsp.cspstatus.title", "/shopadmin/csp-status-overview.shtml"));
        }
        menu.addMenuItem(new MenuItem("jsp.notifications.title", "/notifications.shtml"));
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/history.shtml"));
        if (isStatisticsAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.stats.title", "/stats/stats.shtml"));
        }
        break;
      case ROLE_IDP_LICENSE_ADMIN:
        if (isCrmAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.notifications.title", "/notifications.shtml"));
        }
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/history.shtml"));
        if (isStatisticsAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.stats.title", "/stats/stats.shtml"));
        }
        break;
      case ROLE_IDP_SURFCONEXT_ADMIN:
        if (isCrmAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.notifications.title", "/notifications.shtml"));
        }
        menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests/history.shtml"));
        if (isStatisticsAvailable(request)) {
          menu.addMenuItem(new MenuItem("jsp.stats.title", "/stats/stats.shtml"));
        }
        break;
      case ROLE_USER:
        //no need for menu
        menu.getMenuItems().clear();
        break;
      default:
        break;
      }
    }
    return menu;
  }
  
  private Boolean isCrmAvailable(final HttpServletRequest request) {
    Boolean result = Boolean.FALSE;
    if (null != request.getAttribute("crmAvailable")) {
      result = (Boolean) request.getAttribute("crmAvailable");
    }
    return result;
  }
 
  private Boolean isStatisticsAvailable(final HttpServletRequest request) {
    Boolean result = Boolean.FALSE;
    if (null != request.getAttribute("statisticsAvailable")) {
      result = (Boolean) request.getAttribute("statisticsAvailable");
    }
    return result;
  }

}
