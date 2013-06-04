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

import java.util.ArrayList;
import java.util.List;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * TestMenuInterceptorTest.java
 */
public class MenuInterceptorTest {

    private MenuInterceptor menuInterceptor = new MenuInterceptor();

    @Test
    public void test_menu_for_user_has_home() throws Exception {
        Menu menu = executeTestAndReturnMenu("/app-overview.shtml", false, ROLE_USER);
        assertEquals(0, menu.getMenuItems().size());
    }

    @Test
    public void test_menu_for_idp_admin_has_duplicates() throws Exception {
        Menu menu = executeTestAndReturnMenu("who cares", true, ROLE_IDP_SURFCONEXT_ADMIN, ROLE_IDP_LICENSE_ADMIN);

        assertEquals(4, menu.getMenuItems().size());
        assertEquals("jsp.notifications.title", menu.getMenuItems().get(1).getLabel());
    }

    @Test
    public void notificationsOnlyWhenLmngActive() throws Exception {        
        Menu menuWhenLmngActive = executeTestAndReturnMenu("who cares", true, ROLE_IDP_SURFCONEXT_ADMIN, ROLE_IDP_LICENSE_ADMIN);

        Menu menuWhenLmngNotActive = executeTestAndReturnMenu("who cares", false, ROLE_IDP_SURFCONEXT_ADMIN, ROLE_IDP_LICENSE_ADMIN);

        assertEquals(4, menuWhenLmngActive.getMenuItems().size());
        assertEquals(3, menuWhenLmngNotActive.getMenuItems().size());
        assertEquals("jsp.notifications.title", menuWhenLmngActive.getMenuItems().get(1).getLabel());
        assertEquals("jsp.requests-overview.title", menuWhenLmngNotActive.getMenuItems().get(1).getLabel());
    }

    private Menu executeTestAndReturnMenu(String requestUri, Boolean crmActive, Authority... authorities) throws Exception {
        setUpAuthorities(authorities);
        ModelAndView modelAndView = new ModelAndView();
        MockHttpServletRequest request = new MockHttpServletRequest("GET", requestUri);
        request.setAttribute("crmAvailable", crmActive);
        request.setAttribute("statisticsAvailable", Boolean.TRUE);
        menuInterceptor.postHandle(request, null, null, modelAndView);

        ModelMap modelMap = modelAndView.getModelMap();

        Menu menu = (Menu) modelMap.get("menu");
        return menu;
    }

    private void setUpAuthorities(Authority... authorities) {
        CoinUser coinUser = new CoinUser();
        List<CoinAuthority> grantedAuthorities = new ArrayList<CoinAuthority>();
        for (Authority authority : authorities) {
            grantedAuthorities.add(new CoinAuthority(authority));
        }
        coinUser.setAuthorities(grantedAuthorities);
        SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(coinUser, "", coinUser.getAuthorities()));
    }
}
