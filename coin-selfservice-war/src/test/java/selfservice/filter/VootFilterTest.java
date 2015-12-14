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

package selfservice.filter;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.Group;
import selfservice.service.VootClient;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static selfservice.domain.CoinAuthority.Authority.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class VootFilterTest {

  private String VIEWER = "viewer";
  private String SUPER_USER = "super.user";
  private String ADMIN = "admin";
  private String USER = "user";

  @Mock
  private FilterChain chain;

  @Mock
  private VootClient vootClient;

  @InjectMocks
  private VootFilter filter = new VootFilter(vootClient, ADMIN, VIEWER, SUPER_USER);

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @Mock
  private SecurityContext securityContext;

  private CoinUser coinUser;

  @Before
  public void setUp() throws Exception {
    SecurityContextHolder.setContext(securityContext);

    request = new MockHttpServletRequest("GET", "/anyUrl");
    response = new MockHttpServletResponse();
  }

  @After
  public void cleanup() {
    SecurityContextHolder.clearContext();
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
  public void filter_and_promote_user_to_admin() throws Exception {
    testHasRoleBeenGranted(ADMIN, ROLE_DASHBOARD_ADMIN);
  }

  @Test
  public void filter_and_promote_user_to_super() throws Exception {
    testHasRoleBeenGranted(VIEWER, ROLE_DASHBOARD_VIEWER);
  }

  @Test
  public void filter_and_promote_user_to_viewer() throws Exception {
    testHasRoleBeenGranted(SUPER_USER, ROLE_DASHBOARD_SUPER_USER);
  }

  @Test
  public void filter_and_do_not_promote_user() throws Exception {
    setAuthentication();

    when(vootClient.groups(USER)).thenReturn(new ArrayList<Group>());

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    assertEquals(0, coinUser.getAuthorityEnums().size());
  }

  private void assertRoleIsGranted(CoinUser user, CoinAuthority.Authority... expectedAuthorities) {
    List<CoinAuthority.Authority> actualAuthorities = user.getAuthorityEnums();
    assertEquals("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, expectedAuthorities.length, actualAuthorities.size());
    assertTrue("expected roles: " + Arrays.asList(expectedAuthorities) + ", actual roles: " + actualAuthorities, actualAuthorities.containsAll(Arrays.asList(expectedAuthorities)));
  }

  private void testHasRoleBeenGranted(String group, CoinAuthority.Authority role) throws IOException, ServletException {
    setAuthentication();
    when(vootClient.groups(USER)).thenReturn(Arrays.asList(new Group(group)));

    filter.doFilter(request, response, chain);

    verify(chain).doFilter(request, response);
    assertRoleIsGranted(coinUser, role);
  }

  private void setAuthentication() {
    coinUser = new CoinUser();
    coinUser.setUid(USER);
    PreAuthenticatedAuthenticationToken token = new PreAuthenticatedAuthenticationToken(coinUser, "");
    token.setAuthenticated(true);

    when(securityContext.getAuthentication()).thenReturn(token);
  }

}
