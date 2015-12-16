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
package csa.interceptor;

import static csa.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import csa.domain.Menu;
import csa.domain.CoinAuthority;
import csa.domain.CoinAuthority.Authority;
import csa.domain.CoinUser;

public class MenuInterceptorTest {

  private MenuInterceptor menuInterceptor = new MenuInterceptor();

  @Test
  public void test_menu_for_role_distribution_admin() throws Exception {
    Menu menu = executeTestAndReturnMenu("/whatever.shtml", ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    assertEquals(5, menu.getMenuItems().size());
  }

  private Menu executeTestAndReturnMenu(String requestUri, Authority... authorities) throws Exception {
    setUpAuthorities(authorities);
    ModelAndView modelAndView = new ModelAndView();
    MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
    menuInterceptor.postHandle(request, null, null, modelAndView);

    ModelMap modelMap = modelAndView.getModelMap();

    Menu menu = (Menu) modelMap.get("menu");
    return menu;
  }

  private void setUpAuthorities(Authority... authorities) {
    CoinUser coinUser = new CoinUser();
    List<CoinAuthority> grantedAuthorities = new ArrayList<>();
    for (Authority authority : authorities) {
      grantedAuthorities.add(new CoinAuthority(authority));
    }
    coinUser.setAuthorities(grantedAuthorities);

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);
    when(authentication.getPrincipal()).thenReturn(coinUser);
    when(securityContext.getAuthentication()).thenReturn(authentication);

  }
}
