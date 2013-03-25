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

package nl.surfnet.coin.selfservice.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabRoleHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.filter.GenericFilterBean;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_LICENSE_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_SURFCONEXT_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_USER;

public class SabEntitlementsFilter extends GenericFilterBean {

  private static final Logger LOG = LoggerFactory.getLogger(SabEntitlementsFilter.class);

  protected static final String PROCESSED = "nl.surfnet.coin.selfservice.filter.SabEntitlementsFilter.PROCESSED";

  private boolean lmngActive;

  @Resource
  private Sab sab;

  private String adminDistributionRole;
  private String adminLicentieIdPRole;
  private String adminSurfConextIdPRole;
  private String viewerSurfConextIdPRole;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    final HttpSession session = httpRequest.getSession(true);

    if (SpringSecurity.isFullyAuthenticated() && session.getAttribute(PROCESSED) == null) {
      CoinUser user = SpringSecurity.getCurrentUser();

      try {
        SabRoleHolder roleHolder = sab.getRoles(user.getUid());
        if (roleHolder == null) {
          LOG.debug("SAB returned no information about user '{}'. Will skip SAB entitlement.", user.getUid());
          session.setAttribute(PROCESSED, "true");
        } else {
          LOG.debug("Roles of user '{}' in organisation {}: {}", user.getUid(), roleHolder.getOrganisation(), roleHolder.getRoles());
          elevateUserIfApplicable(user, roleHolder);
          session.setAttribute(PROCESSED, "true");
          LOG.debug("Authorities of user '{}' after processing SAB entitlements: {}", user.getUid(), user.getAuthorityEnums());
        }
      } catch (IOException e) {
        LOG.info("Skipping SAB entitlement, SAB request got IOException: {}", e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }

  private void elevateUserIfApplicable(CoinUser user, SabRoleHolder roleHolder) {

    if (!adminDistributionRole.isEmpty() && roleHolder.getRoles().contains(adminDistributionRole)) {
      user.setAuthorities(new ArrayList<CoinAuthority>());
      user.addAuthority(new CoinAuthority(ROLE_DISTRIBUTION_CHANNEL_ADMIN));
    } else {
      List<GrantedAuthority> newAuthorities = new ArrayList<GrantedAuthority>();
      if (!adminLicentieIdPRole.isEmpty() && roleHolder.getRoles().contains(adminLicentieIdPRole) && this.lmngActive) {
        newAuthorities.add(new CoinAuthority(ROLE_IDP_LICENSE_ADMIN));
      }
      if (!adminSurfConextIdPRole.isEmpty() && roleHolder.getRoles().contains(adminSurfConextIdPRole)) {
        newAuthorities.add(new CoinAuthority(ROLE_IDP_SURFCONEXT_ADMIN));
      }
      if (!viewerSurfConextIdPRole.isEmpty() && roleHolder.getRoles().contains(viewerSurfConextIdPRole)) {
        // BACKLOG-940: for now, only users having this role will be allowed access.
        // No regular end users yet.
        // In the future, this 'viewer' (SURFconextbeheerder) user probably deserves a role of its own, instead of the USER role.
        newAuthorities.add(new CoinAuthority(CoinAuthority.Authority.ROLE_USER));
      }

      // Now merge with earlier assigned authorities
      if (user.getAuthorityEnums().contains(ROLE_DISTRIBUTION_CHANNEL_ADMIN)) {
        // nothing, highest role possible
      } else if (user.getAuthorityEnums().contains(ROLE_IDP_LICENSE_ADMIN) && newAuthorities.contains(new CoinAuthority(ROLE_IDP_SURFCONEXT_ADMIN))) {
        user.addAuthority(new CoinAuthority(ROLE_IDP_SURFCONEXT_ADMIN));
      } else if (user.getAuthorityEnums().contains(ROLE_IDP_SURFCONEXT_ADMIN) && newAuthorities.contains(new CoinAuthority(ROLE_IDP_LICENSE_ADMIN))) {
        user.addAuthority(new CoinAuthority(ROLE_IDP_LICENSE_ADMIN));
      } else if (newAuthorities.contains(new CoinAuthority(ROLE_USER))) {
        user.addAuthority(new CoinAuthority(ROLE_USER));
      }
    }
  }

  @Override
  public void destroy() {
  }

  public void setAdminDistributionRole(String adminDistributionRole) {
    this.adminDistributionRole = adminDistributionRole;
  }

  public void setAdminLicentieIdPRole(String adminLicentieIdPRole) {
    this.adminLicentieIdPRole = adminLicentieIdPRole;
  }

  public void setAdminSurfConextIdPRole(String adminSurfConextIdPRole) {
    this.adminSurfConextIdPRole = adminSurfConextIdPRole;
  }
  public void setViewerSurfConextIdPRole(String viewerSurfConextIdPRole) {
    this.viewerSurfConextIdPRole = viewerSurfConextIdPRole;
  }

  public void setLmngActive(boolean lmngActive) {
    this.lmngActive = lmngActive;
  }
}
