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

import static csa.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;

import csa.Application;
import csa.service.VootClient;
import csa.domain.CoinAuthority;
import csa.domain.CoinUser;

/**
 * Servlet filter that performs Oauth 2.0 (client credentials) against
 * voot.surfconext.nl and gets group information of the 'admin team'. Based on
 * this information, an additional role is set on the users' Authentication
 * object (or not).
 */
public class VootFilter implements Filter {

  public static final String SESSION_KEY_GROUP_ACCESS = "SESSION_KEY_GROUP_ACCESS";
  private static final Logger LOG = LoggerFactory.getLogger(VootFilter.class);

  private final VootClient vootClient;
  private final String adminDistributionTeam;
  private final Environment environment;

  public VootFilter(VootClient vootClient, String adminDistributionTeam, Environment environment) {
    this.vootClient = vootClient;
    this.adminDistributionTeam = adminDistributionTeam;
    this.environment = environment;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    if (!Arrays.asList(environment.getActiveProfiles()).contains(Application.DEV_PROFILE_NAME)) {

      final HttpSession session = ((HttpServletRequest) request).getSession(true);

      if (isFullyAuthenticated()) {
        CoinUser user = (CoinUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Boolean isAdmin = (Boolean) session.getAttribute(SESSION_KEY_GROUP_ACCESS);
        if (isAdmin == null || !isAdmin) {
          isAdmin = vootClient.hasAccess(user.getUid(), adminDistributionTeam);
          LOG.info("User '{}' has access to '{}': {}", user.getUid(), adminDistributionTeam, isAdmin);
          session.setAttribute(SESSION_KEY_GROUP_ACCESS, isAdmin);
        }
        if (isAdmin) {
          user.setAuthorities(new ArrayList<>());
          user.addAuthority(new CoinAuthority(ROLE_DISTRIBUTION_CHANNEL_ADMIN));
        }

        final PreAuthenticatedAuthenticationToken currentAuthentication = (PreAuthenticatedAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();
        final PreAuthenticatedAuthenticationToken withAuthorities = new PreAuthenticatedAuthenticationToken(currentAuthentication.getPrincipal(), currentAuthentication.getCredentials(), user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(withAuthorities);

      }
    }

    chain.doFilter(request, response);
  }

  private static boolean isFullyAuthenticated() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CoinUser;
  }

  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void destroy() {
  }

}
