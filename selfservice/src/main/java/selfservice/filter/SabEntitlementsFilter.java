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

package selfservice.filter;

import selfservice.domain.CoinAuthentication;
import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.util.SpringSecurity;
import selfservice.sab.Sab;
import selfservice.sab.SabRoleHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER;

public class SabEntitlementsFilter extends GenericFilterBean {

  private static final Logger LOG = LoggerFactory.getLogger(SabEntitlementsFilter.class);

  protected static final String PROCESSED = "selfservice.filter.SabEntitlementsFilter.PROCESSED";

  private final Sab sab;

  private final String adminSurfConextIdpRole;
  private final String viewerSurfConextIdpRole;

  public SabEntitlementsFilter(Sab sab, String adminSurfConextIdpRole, String viewerSurfConextIdpRole) {
    this.sab = sab;
    this.adminSurfConextIdpRole = adminSurfConextIdpRole;
    this.viewerSurfConextIdpRole = viewerSurfConextIdpRole;
  }

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
    HttpSession session = ((HttpServletRequest) request).getSession(true);

    addSabRoles(session);

    chain.doFilter(request, response);
  }

  private void addSabRoles(HttpSession session) {
    if (!SpringSecurity.isFullyAuthenticated() || session.getAttribute(PROCESSED) != null) {
      return;
    }

    CoinUser user = SpringSecurity.getCurrentUser();

    sab.getRoles(user.getUid()).ifPresent(roleHolder -> {
      LOG.debug("Roles of user '{}' in organisation {}: {}", user.getUid(), roleHolder.getOrganisation(), roleHolder.getRoles());
      elevateUserIfApplicable(user, roleHolder);
      LOG.debug("Authorities of user '{}' after processing SAB entitlements: {}", user.getUid(), user.getAuthorityEnums());
      SecurityContextHolder.getContext().setAuthentication(new CoinAuthentication(user));
    });

    session.setAttribute(PROCESSED, "true");
  }

  private void elevateUserIfApplicable(CoinUser user, SabRoleHolder roleHolder) {
    if (needToAddRole(roleHolder, adminSurfConextIdpRole)) {
      user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_ADMIN));
    } else if (needToAddRole(roleHolder, viewerSurfConextIdpRole)) {
      user.addAuthority(new CoinAuthority(ROLE_DASHBOARD_VIEWER));
    }
  }

  private boolean needToAddRole(SabRoleHolder roleHolder, String adminLicentieIdPRole) {
    return StringUtils.hasText(adminLicentieIdPRole) && roleHolder.getRoles().contains(adminLicentieIdPRole);
  }

}
