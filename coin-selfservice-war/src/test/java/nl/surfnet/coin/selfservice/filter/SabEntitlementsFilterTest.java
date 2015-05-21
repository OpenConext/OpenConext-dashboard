/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.coin.selfservice.filter;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabRoleHolder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Arrays;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class SabEntitlementsFilterTest {

  @InjectMocks
  private SabEntitlementsFilter filter;

  @Mock
  private FilterChain chain;

  @Mock
  private Sab sabClient;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    filter = new SabEntitlementsFilter();

    filter.setAdminSurfConextIdPRole(ROLE_DASHBOARD_ADMIN.name());
    filter.setViewerSurfConextIdPRole(ROLE_DASHBOARD_VIEWER.name());

    MockitoAnnotations.initMocks(this);

    request = new MockHttpServletRequest("GET", "/anyUrl");
    response = new MockHttpServletResponse();

    SecurityContextHolder.getContext().setAuthentication(null);


  }

  @Test
  public void testWhenAlreadyProcessed() throws IOException, ServletException {
    request.getSession().setAttribute(SabEntitlementsFilter.PROCESSED, "true");

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    verifyNoMoreInteractions(sabClient);
  }

  @Test
  public void happyNoRole() throws IOException, ServletException {
    when(sabClient.getRoles("theuser")).thenReturn(new SabRoleHolder("theOrg", Arrays.asList("Foo", "Bar")));

    filter.doFilter(request, response, chain);

    SpringSecurityUtil.assertNoRoleIsGranted();
  }

  @Test
  public void adminScRole() throws IOException, ServletException {
    SpringSecurityUtil.setAuthentication("theuser");
    CoinUser user = SpringSecurity.getCurrentUser();
    user.setSchacHomeOrganization("theOrg");

    when(sabClient.getRoles("theuser")).thenReturn(new SabRoleHolder("theOrg", Arrays.asList("Foo", ROLE_DASHBOARD_ADMIN.name())));

    filter.doFilter(request, response, chain);
    SpringSecurityUtil.assertRoleIsGranted(ROLE_DASHBOARD_ADMIN);
  }

  @Test
  public void viewerRole() throws IOException, ServletException {
    SpringSecurityUtil.setAuthentication("theuser");
    CoinUser user = SpringSecurity.getCurrentUser();
    user.setSchacHomeOrganization("theOrg");

    when(sabClient.getRoles("theuser")).thenReturn(new SabRoleHolder("theOrg", Arrays.asList("Foo", ROLE_DASHBOARD_VIEWER.name())));

    filter.doFilter(request, response, chain);
    SpringSecurityUtil.assertRoleIsGranted(ROLE_DASHBOARD_VIEWER);
  }

  @Test
  public void sabReturnsNada() throws IOException, ServletException {
    SpringSecurityUtil.setAuthentication("theuser");
    CoinUser user = SpringSecurity.getCurrentUser();
    user.setSchacHomeOrganization("theorg");

    when(sabClient.getRoles(anyString())).thenReturn(null);
    filter.doFilter(request, response, chain);
    SpringSecurityUtil.assertNoRoleIsGranted();
  }
}
