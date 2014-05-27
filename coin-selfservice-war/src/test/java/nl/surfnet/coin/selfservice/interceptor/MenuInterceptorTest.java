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

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.Menu;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashSet;
import java.util.Set;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import static org.junit.Assert.assertEquals;

/**
 * TestMenuInterceptorTest.java
 */
public class MenuInterceptorTest {

  private MenuInterceptor menuInterceptor = new MenuInterceptor();

  @Test
  public void test_menu_for_user_has_one() throws Exception {
    Menu menu = executeTestAndReturnMenu(false, ROLE_SHOWROOM_USER);
    assertEquals(0, menu.getMenuItems().size());
  }

  @Test
  public void test_menu_for_dashboard_admin_equals_admin_viewer_has_none() throws Exception {
    Menu menuAdmin = executeTestAndReturnMenu(false, ROLE_DASHBOARD_ADMIN);
    Menu menuViewer = executeTestAndReturnMenu(false, ROLE_DASHBOARD_VIEWER);
    assertEquals(menuAdmin.getMenuItems().size(), menuViewer.getMenuItems().size());
    assertEquals(5, menuViewer.getMenuItems().size());
  }

  @Test
  public void test_menu_for_showroom_admin() throws Exception {
    Menu menuAdmin = executeTestAndReturnMenu(false, ROLE_SHOWROOM_ADMIN);
    assertEquals(2, menuAdmin.getMenuItems().size());
  }


  private Menu executeTestAndReturnMenu(Boolean isDashBoard, Authority... authorities) throws Exception {
    setUpAuthorities(authorities);
    ModelAndView modelAndView = new ModelAndView();
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.setAttribute("isDashBoard", isDashBoard);
    menuInterceptor.postHandle(request, null, null, modelAndView);

    ModelMap modelMap = modelAndView.getModelMap();

    Menu menu = (Menu) modelMap.get("menu");
    return menu;
  }

  private void setUpAuthorities(Authority... authorities) {
    CoinUser coinUser = new CoinUser();
    Set<CoinAuthority> grantedAuthorities = new HashSet<CoinAuthority>();
    for (Authority authority : authorities) {
      grantedAuthorities.add(new CoinAuthority(authority));
    }
    coinUser.setAuthorities(grantedAuthorities);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(coinUser, "", coinUser.getAuthorities()));
  }
}
