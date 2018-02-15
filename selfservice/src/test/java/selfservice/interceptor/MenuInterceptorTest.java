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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;

import java.util.Arrays;

import org.junit.After;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.CoinUser;
import selfservice.domain.csa.Menu;

public class MenuInterceptorTest {

  private MenuInterceptor menuInterceptor = new MenuInterceptor();

  @After
  public void cleanUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void test_menu_for_role_distribution_admin() throws Exception {
    Menu menu = executeTestAndReturnMenu("/whatever.shtml", ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    assertEquals(4, menu.getMenuItems().size());
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

    Arrays.stream(authorities).forEach(authority -> coinUser.addAuthority(new CoinAuthority(authority)));

    Authentication authentication = mock(Authentication.class);
    SecurityContext securityContext = mock(SecurityContext.class);
    SecurityContextHolder.setContext(securityContext);

    when(authentication.getPrincipal()).thenReturn(coinUser);
    when(securityContext.getAuthentication()).thenReturn(authentication);
  }
}
