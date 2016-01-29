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

package csa.filter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;
import javax.servlet.FilterChain;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.env.Environment;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import csa.Application;
import csa.domain.CoinAuthority;
import csa.service.VootClient;
import csa.domain.CoinUser;

@RunWith(MockitoJUnitRunner.class)
public class VootFilterTest {

  public static final String ADMINS = "admins";
  public static final String TEST_USER = "test";

  private VootFilter filter;

  @Mock
  private FilterChain chain;

  @Mock
  private VootClient vootClient;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Environment environment;

  private CoinUser coinUser;

  @Before
  public void setUp() throws Exception {
    request = new MockHttpServletRequest("GET", "/anyUrl");
    response = new MockHttpServletResponse();
    String[] activeProfiles = {"foo", "bar"};
    when(environment.getActiveProfiles()).thenReturn(activeProfiles);
    SecurityContextHolder.setContext(securityContext);
    filter = new VootFilter(vootClient, ADMINS, environment);

  }

  @Test
  public void do_nothing_when_on_dev() throws Exception {
    String[] activeProfiles = {Application.DEV_PROFILE_NAME};
    when(environment.getActiveProfiles()).thenReturn(activeProfiles);
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoMoreInteractions(vootClient);
  }

  @Test
  public void do_nothing_when_not_fully_logged_in() throws Exception {
    Authentication mockAuthentication = mock(Authentication.class);
    when(securityContext.getAuthentication()).thenReturn(mockAuthentication);
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    verifyNoMoreInteractions(vootClient);
  }

  @Test
  public void filter_and_promote_user() throws Exception {
    setAuthentication();
    when(vootClient.hasAccess(TEST_USER, ADMINS)).thenReturn(true);
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    assertRoleIsGranted(coinUser, CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN);
  }

  @Test
  public void filter_and_do_not_promote_user() throws Exception {
    setAuthentication();
    when(vootClient.hasAccess(TEST_USER, ADMINS)).thenReturn(false);
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    assertEquals(0, coinUser.getAuthorityEnums().size());
  }

  @Test
  public void filter_and_promote_based_on_session_key() throws Exception {
    setAuthentication();
    request.getSession(true).setAttribute(VootFilter.SESSION_KEY_GROUP_ACCESS, true);
    filter.doFilter(request, response, chain);
    verify(chain).doFilter(request, response);
    assertRoleIsGranted(coinUser, CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    verifyNoMoreInteractions(vootClient);
  }

  protected static void assertRoleIsGranted(CoinUser user, CoinAuthority.Authority... expectedAuthorities) {
    List<CoinAuthority.Authority> actualAuthorities = user.getAuthorityEnums();
    assertEquals("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, expectedAuthorities.length, actualAuthorities.size());
    assertTrue("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, actualAuthorities.containsAll(Arrays.asList(expectedAuthorities)));
  }


  private void setAuthentication() {
    coinUser = new CoinUser();
    coinUser.setUid(TEST_USER);
    final PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(coinUser, "");
    token.setAuthenticated(true);
    when(securityContext.getAuthentication()).thenReturn(token);
  }

}
