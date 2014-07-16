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

package nl.surfnet.coin.selfservice.filter;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;
import static nl.surfnet.coin.selfservice.filter.SpringSecurityUtil.assertNoRoleIsGranted;
import static nl.surfnet.coin.selfservice.filter.SpringSecurityUtil.assertRoleIsGranted;
import static nl.surfnet.coin.selfservice.filter.SpringSecurityUtil.assertRoleIsNotGranted;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpSession;

import org.hamcrest.core.Is;
import org.hamcrest.core.IsEqual;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;

public class ApiOAuthFilterTest {

  private static final String THE_USERS_UID = "the-users-uid";

  @InjectMocks
  private ApiOAuthFilter filter;

  @Mock
  private FilterChain chain;

  @Mock
  private OpenConextOAuthClient apiClient;
  private static final Logger LOG = LoggerFactory.getLogger(ApiOAuthFilterTest.class);

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Before
  public void setUp() throws Exception {
    filter = new ApiOAuthFilter();
    MockitoAnnotations.initMocks(this);
    request = new MockHttpServletRequest("GET", "/anyUrl");
    response = new MockHttpServletResponse();

    SecurityContextHolder.getContext().setAuthentication(null);
  }

  @Test
  public void filterWhenNotLoggedInAtAll() throws Exception {
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
  }

  @Test
  public void filterWhenAlreadyProcessed() throws Exception {
    request.getSession().setAttribute(ApiOAuthFilter.PROCESSED, "true");

    filter.doFilter(request, response, chain);

    // Filter should skip all logic and call chain.doFilter() straight on.
    verify(chain).doFilter(request, response);
    verifyNoMoreInteractions(apiClient);

  }

  @Test
  public void filterAndStartOauthDance() throws Exception {

    setAuthentication();
    
    when(apiClient.getAuthorizationUrl()).thenReturn("http://authorization-url");

    filter.doFilter(request, response, chain);
    LOG.debug("url: " + request.getRequestURL());
    assertThat("Originally requested url should be stored for later redirect (after oauth)",
            (String) request.getSession().getAttribute(ApiOAuthFilter.ORIGINAL_REQUEST_URL), IsEqual.equalTo("http://localhost:80/anyUrl"));
    assertThat("redirect to oauth authorization url", response.getRedirectedUrl(), IsEqual.equalTo("http://authorization-url"));
  }

  @Test
  public void filterAndProcessCallback() throws Exception {
    final HttpSession session = mock(HttpSession.class);
    setAuthentication();

    filter.setCallbackFlagParameter("myDummyCallback");

    request.setParameter("myDummyCallback", "true");
    request.setSession(session);
    when(session.getAttribute(ApiOAuthFilter.ORIGINAL_REQUEST_URL)).thenReturn("http://originalUrl");
    filter.doFilter(request, response, chain);
    verify(apiClient).oauthCallback(eq(request), anyString());
    verify(apiClient).getGroups20(THE_USERS_UID, THE_USERS_UID);
    verify(session).setAttribute(ApiOAuthFilter.PROCESSED, "true");
    assertThat("redirect to original url", response.getRedirectedUrl(), IsEqual.equalTo("http://originalUrl"));
  }


  @Test
  public void elevateUserNoAdminButHasSomeGroups() throws Exception {

    // This tests whether a user gets the role 'user' when he is member of some random groups, but not any admin group.
    when(apiClient.isAccessTokenGranted(anyString())).thenReturn(true);

    setAuthentication();
    setUpForAuthoritiesCheck(ROLE_DASHBOARD_VIEWER);
    when(apiClient.getGroups20(THE_USERS_UID, THE_USERS_UID)).thenReturn(Arrays.asList(new Group20("id1"), new Group20("id2")));

    filter.doFilter(request, response, chain);
    assertThat((String) request.getSession().getAttribute(ApiOAuthFilter.PROCESSED), Is.is("true"));
    assertRoleIsGranted(ROLE_DASHBOARD_VIEWER);
    assertRoleIsNotGranted(ROLE_DASHBOARD_ADMIN);
  }

  @Test
  public void filterAndUsePrefetchedAccessTokenAndIsAdmin() throws Exception {

    setAuthentication();

    when(apiClient.isAccessTokenGranted(anyString())).thenReturn(true);
    request.getSession(true).setAttribute(ApiOAuthFilter.PROCESSED, null);

    this.setUpGroupMembersShips(ROLE_DASHBOARD_ADMIN);

    filter.doFilter(request, response, chain);

    assertRoleIsGranted(ROLE_DASHBOARD_ADMIN);

    // Verify flag that the process is done.
    assertThat((String) request.getSession().getAttribute(ApiOAuthFilter.PROCESSED), Is.is("true"));
  }

  @Test
  public void test_elevate_user_results_in_two_admins() throws IOException, ServletException {
    setUpForAuthoritiesCheck(ROLE_DASHBOARD_ADMIN);
    assertRoleIsGranted(ROLE_DASHBOARD_ADMIN);
  }


  @Test
  public void test_elevate_user_results_in_no_authorities_in_lmng_disactive_modus() throws IOException, ServletException {
    setUpForAuthoritiesCheck(new Authority[]{});
    assertNoRoleIsGranted();
  }

  private void setUpForAuthoritiesCheck(Authority... groupMemberShips) throws IOException, ServletException {
    request.getSession(true).setAttribute(ApiOAuthFilter.PROCESSED, null);
    when(apiClient.isAccessTokenGranted(anyString())).thenReturn(true);

    setAuthentication();

    setUpGroupMembersShips(groupMemberShips);

    filter.doFilter(request, response, chain);
    
  }

  private void setUpGroupMembersShips(Authority... authorities) {
    List<Group20> groups = new ArrayList<>();
    for (Authority authority : authorities) {
      switch (authority) {
      case ROLE_DASHBOARD_ADMIN:
        filter.setDashboardAdmin(authority.name());
        groups.add(new Group20(authority.name()));
        break;
      case ROLE_DASHBOARD_VIEWER:
        filter.setDashboardViewer(authority.name());
        groups.add(new Group20(authority.name()));
        break;
      default:
      }
    }
    when(apiClient.getGroups20(THE_USERS_UID, THE_USERS_UID)).thenReturn(groups);
  }

  private void setAuthentication() {
    SpringSecurityUtil.setAuthentication(THE_USERS_UID);
  }

}
