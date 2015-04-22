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

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.Group;
import nl.surfnet.coin.selfservice.service.VootClient;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;

/**
 * Servlet filter that performs Oauth 2.0 (authorization code) against
 * voot.surfconext.nl and gets group information of the 'admin teams'. Based on
 * this information, an additional role is set on the users' Authentication
 * object (or not).
 */
public class VootFilter implements Filter {

  public static final String SESSION_KEY_GROUP_ACCESS = "SESSION_KEY_GROUP_ACCESS";

  private Logger LOG = LoggerFactory.getLogger(VootFilter.class);

  private VootClient vootClient;

  private String dashboardAdmin;
  private String dashboardViewer;
  private String dashboardSuperUser;

  /**
   * No initialization needed.
   *
   * @param filterConfig the configuration
   * @throws ServletException
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    final HttpSession session = httpRequest.getSession(true);

    if (SpringSecurity.isFullyAuthenticated()) {
      CoinUser user = SpringSecurity.getCurrentUser();

      List<Group> groups = (List<Group>) session.getAttribute(SESSION_KEY_GROUP_ACCESS);
      if (groups == null) {
        groups = vootClient.groups(user.getUid());
        session.setAttribute(SESSION_KEY_GROUP_ACCESS, groups);
      }
      elevateUser(user, groups);
    }
    chain.doFilter(request, response);
  }

  /**
   * Assign the appropriate roles to the given user, if he is member of one the
   * admin teams team.
   *
   * @param coinUser the CoinUser representing the currently logged in user.
   */
  private void elevateUser(CoinUser coinUser, List<Group> groups) {
    if (LOG.isDebugEnabled()) {
      LOG.debug("Memberships of adminTeams '{}' for user '{}'", new Object[]{groups, coinUser.getUid()});
    }
    /*
     * We want to end up with only one role
     */
    coinUser.setAuthorities(new HashSet<CoinAuthority>());
    if (groupsContains(dashboardAdmin, groups)) {
      coinUser.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
    } else if (groupsContains(dashboardViewer, groups)) {
      coinUser.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
    } else if (groupsContains(dashboardSuperUser, groups)) {
      coinUser.addAuthority(new CoinAuthority(ROLE_DASHBOARD_SUPER_USER));
    }

    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(coinUser, "", coinUser.getAuthorities()));
  }

  private boolean groupsContains(String teamId, List<Group> groups) {
    return groups.stream().filter(group -> group.getId().equalsIgnoreCase(teamId)).findFirst().isPresent();
  }

  @Override
  public void destroy() {
  }

  public void setDashboardAdmin(String dashboardAdmin) {
    this.dashboardAdmin = dashboardAdmin;
  }

  public void setDashboardViewer(String dashboardViewer) {
    this.dashboardViewer = dashboardViewer;
  }

  public void setDashboardSuperUser(String dashboardSuperUser) {
    this.dashboardSuperUser = dashboardSuperUser;
  }

}
