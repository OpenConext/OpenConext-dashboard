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

import java.util.Arrays;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpSession;

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.selfservice.domain.CoinUser;

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
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class ApiOAuthFilterTest {

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

    SecurityContextHolder.getContext().setAuthentication(getAuthentication(new CoinUser()));
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
    final CoinUser coinUser = new CoinUser();
    coinUser.setUid("the-users-uid");
    SecurityContextHolder.getContext().setAuthentication(getAuthentication(coinUser));

    filter.setCallbackFlagParameter("myDummyCallback");
    filter.setAdminDistributionTeam("myAdminTeam");

    request.setParameter("myDummyCallback", "true");
    request.setSession(session);
    when(session.getAttribute(ApiOAuthFilter.ORIGINAL_REQUEST_URL)).thenReturn("http://originalUrl");
    filter.doFilter(request, response, chain);
    verify(apiClient).oauthCallback(eq(request), anyString());
    verify(apiClient).getGroups20("the-users-uid", "the-users-uid");
    verify(session).setAttribute(ApiOAuthFilter.PROCESSED, "true");
    assertThat("redirect to original url", response.getRedirectedUrl(), IsEqual.equalTo("http://originalUrl"));
  }

  @Test
  public void filterAndUsePrefetchedAccessTokenButNoAdmin() throws Exception {
    when(apiClient.isAccessTokenGranted(anyString())).thenReturn(true);

    final CoinUser coinUser = new CoinUser();
    coinUser.setUid("the-users-uid");
    SecurityContextHolder.getContext().setAuthentication(getAuthentication(coinUser));

    filter.setAdminDistributionTeam("a-team");
    when(apiClient.getGroups20("the-users-uid", "the-users-id")).thenReturn(null);

    filter.doFilter(request, response, chain);
    assertThat((String) request.getSession().getAttribute(ApiOAuthFilter.PROCESSED), Is.is("true"));
    assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities().size(), Is.is(0));
  }

  @Test
  public void filterAndUsePrefetchedAccessTokenAndIsAdmin() throws Exception {

    filter.setAdminDistributionTeam("a-team");

    final CoinUser coinUser = new CoinUser();
    coinUser.setUid("the-users-uid");
    SecurityContextHolder.getContext().setAuthentication(getAuthentication(coinUser));

    when(apiClient.isAccessTokenGranted(anyString())).thenReturn(true);
    request.getSession(true).setAttribute(ApiOAuthFilter.PROCESSED, null);
    // let apiClient return the admin group
    when(apiClient.getGroups20("the-users-uid", "the-users-uid")).thenReturn(Arrays.asList(new Group20("a-team", null, null)));

    // Before: no authorities
    assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities().size(), Is.is(0));
    filter.doFilter(request, response, chain);
    // After: 1 authority
    assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities().size(), Is.is(1));

    // Verify flag that the process is done.
    assertThat((String) request.getSession().getAttribute(ApiOAuthFilter.PROCESSED), Is.is("true"));
  }

  protected Authentication getAuthentication(CoinUser coinUser) {
    final TestingAuthenticationToken token = new TestingAuthenticationToken(coinUser, "");
    token.setAuthenticated(true);
    return token;
  }
}
