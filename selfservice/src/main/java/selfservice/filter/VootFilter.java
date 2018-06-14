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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;
import selfservice.domain.CoinAuthentication;
import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.Group;
import selfservice.service.VootClient;
import selfservice.util.SpringSecurity;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;

/**
 * Servlet filter that performs Oauth 2.0 (authorization code) against
 * voot.surfconext.nl and gets group information of the 'admin teams'. Based on
 * this information, an additional role is set on the users' Authentication
 * object (or not).
 */
public class VootFilter extends GenericFilterBean {

  private static final Logger logger = LoggerFactory.getLogger(VootFilter.class);

  public static final String SESSION_KEY_GROUP_ACCESS = "SESSION_KEY_GROUP_ACCESS";

  protected static final String PROCESSED = "selfservice.filter.VootFilter.PROCESSED";

  private VootClient vootClient;

  private final String dashboardAdmin;
  private final String dashboardViewer;
  private final String dashboardSuperUser;

  public VootFilter(VootClient vootClient, String dashboardAdmin, String dashboardViewer, String dashboardSuperUser) {
    this.vootClient = vootClient;
    this.dashboardAdmin = dashboardAdmin;
    this.dashboardViewer = dashboardViewer;
    this.dashboardSuperUser = dashboardSuperUser;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpSession session = httpRequest.getSession(true);

    addVootRoles(session);

    chain.doFilter(request, response);
  }

  private void addVootRoles(HttpSession session) {
    if (!SpringSecurity.isFullyAuthenticated() || session.getAttribute(PROCESSED) != null) {
      return;
    }

    CoinUser user = SpringSecurity.getCurrentUser();

    List<Group> groups = vootClient.groups(user.getUid());

    addDashboardRole(user, groups);

    if (logger.isDebugEnabled()) {
      logger.debug("Roles based on VOOT {}", user.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(", ")));
    }

    SecurityContextHolder.getContext().setAuthentication(new CoinAuthentication(user));

    session.setAttribute(PROCESSED, "true");
  }

  private void addDashboardRole(CoinUser user, List<Group> groups) {
    if (groupsContains(dashboardSuperUser, groups)) {
      user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_SUPER_USER));
    } else if (groupsContains(dashboardAdmin, groups)) {
      user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
    } else if (groupsContains(dashboardViewer, groups)) {
      user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
    }
  }

  private boolean groupsContains(String teamId, List<Group> groups) {
    return groups.stream().anyMatch(group -> teamId.equalsIgnoreCase(group.getId()));
  }

}
