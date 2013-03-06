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
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabRoleHolder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_LICENSE_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_SURFCONEXT_ADMIN;

public class SabEntitlementsFilter extends GenericFilterBean {

  private static final Logger LOG = LoggerFactory.getLogger(SabEntitlementsFilter.class);

  private static final String PROCESSED = "nl.surfnet.coin.selfservice.filter.SabEntitlementsFilter.PROCESSED";

  private boolean lmngActive;

  private Sab sab;
  private String adminDistributionRole;
  private String adminLicentieIdPRole;
  private String adminSurfConextIdPRole;

  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    final HttpSession session = httpRequest.getSession(true);

    if (SpringSecurity.isFullyAuthenticated() && session.getAttribute(PROCESSED) == null) {
      CoinUser user = SpringSecurity.getCurrentUser();

      try {
        SabRoleHolder roleHolder = sab.getRoles(user.getUid());
        LOG.debug("Roles of user {} in organisation {}: {}", user.getUid(), roleHolder.getOrganisation(), roleHolder.getRoles());
        elevateUserIfApplicable(user, roleHolder);
        session.setAttribute(PROCESSED, "true");
        LOG.debug("Authorities of user {} after 'processing SAB entitlements: {}", user.getAuthorities());
      } catch (IOException e) {
        LOG.info("Skipping SAB entitlement, SAB request got IOException: {}", e.getMessage());
      }
    }
    chain.doFilter(request, response);
  }

  private void elevateUserIfApplicable(CoinUser user, SabRoleHolder roleHolder) {

    if (roleHolder.getOrganisation().equals(user.getSchacHomeOrganization())) {

      if (roleHolder.getRoles().contains(adminDistributionRole)) {
        user.setAuthorities(Arrays.asList(new CoinAuthority(ROLE_DISTRIBUTION_CHANNEL_ADMIN)));
      } else {
        List<CoinAuthority> authories = new ArrayList<CoinAuthority>();
        if (roleHolder.getRoles().contains(adminLicentieIdPRole) && this.lmngActive) {
          authories.add(new CoinAuthority(ROLE_IDP_LICENSE_ADMIN));
        }
        if (roleHolder.getRoles().contains(adminSurfConextIdPRole)) {
          authories.add(new CoinAuthority(ROLE_IDP_SURFCONEXT_ADMIN));
        }
        user.setAuthorities(authories.isEmpty() ? Arrays.asList(new CoinAuthority(CoinAuthority.Authority.ROLE_USER)) : authories);
      }
    } else {
      LOG.debug("User ({})'s SchacHomeOrg ({}) does not match organisation in SAB ({}). Will not apply roles.",
        user.getUid(), user.getSchacHomeOrganization(), roleHolder.getOrganisation());
      return;
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

  public void setLmngActive(boolean lmngActive) {
    this.lmngActive = lmngActive;
  }

  public void setSab(Sab sab) {
    this.sab = sab;
  }
}
