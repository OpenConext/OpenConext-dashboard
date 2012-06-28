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

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.surfnet.coin.api.client.OpenConextOAuthClient;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

public class ApiOAuthFilter implements Filter {

  Logger LOG = LoggerFactory.getLogger(ApiOAuthFilter.class);

  private OpenConextOAuthClient apiClient;

  private static final String PROCESSED = "nl.surfnet.coin.selfservice.filter.ApiOAuthFilter.PROCESSED";
  private static final String ORIGINAL_REQUEST_URL = "nl.surfnet.coin.selfservice.filter.ApiOAuthFilter" +
      ".ORIGINAL_REQUEST_URL";
  private String adminTeam;
  private String callbackFlagParameter;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpSession session = httpRequest.getSession(true);
    if (SpringSecurity.isFullyAuthenticated() && session.getAttribute(PROCESSED) == null) {
      CoinUser user = SpringSecurity.getCurrentUser();

      if (apiClient.isAccessTokenGranted(user.getUid())) {
        // already authorized before (we have a token)
        safelyElevateUser(session, user);
        LOG.debug("Access token was already granted, processed elevation. Will fall through filter. " +
            "User is now: {}.", user);
      } else if (httpRequest.getParameter(callbackFlagParameter) != null) {
        // callback from OAuth dance, elevate immediately afterwards

        apiClient.oauthCallback(httpRequest, user.getUid());

        safelyElevateUser(session, user);
        LOG.debug("Processed elevation after callback. Will redirect to originally requested location. User is now: " +
            "{}.", user);
        ((HttpServletResponse) response).sendRedirect((String) session.getAttribute(ORIGINAL_REQUEST_URL));
        return;
      } else {
        // No authorization yet, start the OAuth dance
        final String currentRequestUrl = getCurrentRequestUrl(httpRequest);
        session.setAttribute(ORIGINAL_REQUEST_URL, currentRequestUrl);
        LOG.debug("No auth yet, redirecting to auth url: {}", apiClient.getAuthorizationUrl());
        ((HttpServletResponse) response).sendRedirect(apiClient.getAuthorizationUrl());
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private void safelyElevateUser(HttpSession session, CoinUser user) {
    try {
      elevateUserIfApplicable(user);
      session.setAttribute(PROCESSED, "true");
    } catch (RuntimeException e) {
      // If API receives an error code, it throws a RuntimeException
      if (LOG.isDebugEnabled()) {
        LOG.error("Failed to check user membership elevation", e);
      } else {
        LOG.error("Failed to check user membership elevation", e.getMessage());
      }
    }
  }

  private String getCurrentRequestUrl(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder()
        .append(request.getRequestURL());
    if (!StringUtils.isBlank(request.getQueryString())) {
      sb
        .append("?")
        .append(request.getQueryString());
    }
    return sb.toString();
  }


  public void elevateUserIfApplicable(CoinUser coinUser) {
    if (isMemberOfAdminTeam(coinUser)) {
      coinUser.addAuthority(new CoinAuthority("ROLE_ADMIN"));
      SecurityContextHolder.getContext().setAuthentication(
          new SAMLAuthenticationToken(coinUser, "", coinUser.getAuthorities()));
    }
  }

  /**
   * Whether the given coin user is member of the configured admin team
   *
   * @param user the CoinUser
   * @return boolean
   */
  private boolean isMemberOfAdminTeam(CoinUser user) {
    final Group20 group = apiClient.getGroup20(user.getUid(), adminTeam, user.getUid());

    if (LOG.isDebugEnabled()) {
      LOG.debug("Membership of adminTeam '{}' for user '{}': {}", new Object[]{adminTeam, user.getUid(), group});
    }
    if (group != null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("User {} is member of admin team '{}'", user.getUid(), group.getId());
      }
      return true;
    }
    return false;
  }

  public void setAdminTeam(String adminTeam) {
    this.adminTeam = adminTeam;
  }

  @Override
  public void destroy() {
  }

  public void setApiClient(OpenConextOAuthClient apiClient) {
    this.apiClient = apiClient;
  }

  public void setCallbackFlagParameter(String callbackFlagParameter) {
    this.callbackFlagParameter = callbackFlagParameter;
  }
}
