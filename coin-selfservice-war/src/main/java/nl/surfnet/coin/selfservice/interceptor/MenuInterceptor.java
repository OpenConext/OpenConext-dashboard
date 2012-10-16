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

import java.util.Collection;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.coin.selfservice.domain.MenuItem;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.security.core.GrantedAuthority;
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
      map.addAttribute("menu", createMenu());
    }
  }

  protected Menu createMenu() {
    Menu menu = new Menu();
    menu.addMenuItem(new MenuItem("jsp.home.title", "/"));
    menu.addMenuItem(new MenuItem("jsp.linkedServices.title", "/user/linked-services.shtml"));
    Collection<? extends GrantedAuthority> authorities = SpringSecurity.getCurrentUser().getAuthorities();
    for (GrantedAuthority grantedAuthority : authorities) {
      if (grantedAuthority instanceof CoinAuthority) {
        Authority authority = fromString(((CoinAuthority) grantedAuthority).getAuthority());
        switch (authority) {
        case ROLE_DISTRIBUTION_CHANNEL_ADMIN:
          menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests-overview.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allsplmng.title", "/shopadmin/all-spslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allidplmng.title", "/shopadmin/all-idpslmng.shtml"));
          menu.addMenuItem(new MenuItem("jsp.allsp.title", "/idpadmin/all-sps.shtml"));
          break;
        case ROLE_IDP_LICENSE_ADMIN:
          menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests-overview.shtml"));
          break;
        case ROLE_IDP_SURFCONEXT_ADMIN:
          menu.addMenuItem(new MenuItem("jsp.requests-overview.title", "/requests-overview.shtml"));
          break;
        default:
          break;
        }
      }
    }
    return menu;
  }

}
