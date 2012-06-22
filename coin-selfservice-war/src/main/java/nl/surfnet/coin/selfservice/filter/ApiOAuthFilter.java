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
import java.util.List;

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
import org.springframework.util.Assert;

import nl.surfnet.coin.api.client.OpenConextApiService;
import nl.surfnet.coin.api.client.domain.Group20;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

public class ApiOAuthFilter implements Filter {

  Logger LOG = LoggerFactory.getLogger(ApiOAuthFilter.class);

  public void setApiService(OpenConextApiService apiService) {
    this.apiService = apiService;
  }

  private OpenConextApiService apiService;

  private static final String PROCESSED = "nl.surfnet.coin.selfservice.filter.ApiOAuthFilter.PROCESSED";
  private static final String CALLBACK_URI = "nl.surfnet.coin.selfservice.filter.ApiOAuthFilter.CALLBACK_URI";
  private String adminTeam;

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpSession session = httpRequest.getSession(true);
    if (SpringSecurity.isFullyAuthenticated() && session.getAttribute(PROCESSED) == null) {
      CoinUser user = SpringSecurity.getCurrentUser();

      if (apiService.isAuthorized(user.getUid())) {
        // already authorized before (we have a token)
        elevateUserIfApplicable(user);
        session.setAttribute(PROCESSED, "true");
      } else if (StringUtils.equals(httpRequest.getParameter("oauthCallback"), "true")) {
        // callback from OAuth dance, elevate immediately afterwards
        apiService.setCallbackUrl((String) session.getAttribute(CALLBACK_URI));
        apiService.oauthCallback(httpRequest, user.getUid());
        elevateUserIfApplicable(user);
        session.setAttribute(PROCESSED, "true");
      } else {
        // No authorization yet, start the OAuth dance
        final String callbackUrl = getCallbackUrl(httpRequest);
        session.setAttribute(CALLBACK_URI, callbackUrl);
        apiService.setCallbackUrl(callbackUrl);
        ((HttpServletResponse) response).sendRedirect(apiService.getAuthorizationUrl());
        return;
      }
    }
    chain.doFilter(request, response);
  }

  private String getCallbackUrl(HttpServletRequest request) {
    StringBuilder sb = new StringBuilder()
        .append(request.getRequestURL())
        .append("?");
    if (!StringUtils.isBlank(request.getQueryString())) {
      sb
          .append(request.getQueryString())
          .append("&");
    }
    sb.append("oauthCallback=true");
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
    final List<Group20> groups = apiService.getGroups20(user.getUid(), user.getUid());
    Assert.notNull(groups, "Groups returned from api.surfconext should not be null");

    if (LOG.isDebugEnabled()) {
      LOG.debug("User {} is member of these groups: {}", user.getUid(), groups);
    }
    for (Group20 group : groups) {
      if (group.getId().equals(adminTeam)) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("User {} is member of admin team '{}'", user.getUid(), group.getId());
        }
        return true;
      }
    }
    return false;
  }

  public void setAdminTeam(String adminTeam) {
    this.adminTeam = adminTeam;
  }

  @Override
  public void destroy() {
  }
}
