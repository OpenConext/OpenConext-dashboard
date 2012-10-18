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

package nl.surfnet.coin.selfservice.interceptor;

import static nl.surfnet.coin.selfservice.control.BaseController.COMPOUND_SP;
import static nl.surfnet.coin.selfservice.control.BaseController.COMPOUND_SPS;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_APPLY_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_QUESTION_ALLOWED;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_LICENSE_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_SURFCONEXT_ADMIN;
import static nl.surfnet.coin.selfservice.domain.Field.Key.INSTITUTION_DESCRIPTION_EN;
import static nl.surfnet.coin.selfservice.domain.Field.Key.INSTITUTION_DESCRIPTION_NL;
import static nl.surfnet.coin.selfservice.domain.Field.Key.TECHNICAL_SUPPORTMAIL;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.selfservice.domain.AttributeScopeConstraints;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * Interceptor to de-scope the visibility {@link CompoundServiceProvider}
 * objects for display
 * 
 * See <a
 * href="https://wiki.surfnetlabs.nl/display/services/App-omschrijving">https
 * ://wiki.surfnetlabs.nl/display/services/App-omschrijving</a>
 */
public class AuthorityScopeInterceptor extends HandlerInterceptorAdapter {

  @SuppressWarnings("unchecked")
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
      throws Exception {

    if (modelAndView != null) {
      final ModelMap map = modelAndView.getModelMap();
      CompoundServiceProvider sp = (CompoundServiceProvider) map.get(COMPOUND_SP);
      if (sp != null) {
        scopeCompoundServiceProvider(map, sp);
      }
      Collection<CompoundServiceProvider> sps = (Collection<CompoundServiceProvider>) map.get(COMPOUND_SPS);
      if (!CollectionUtils.isEmpty(sps)) {
        for (CompoundServiceProvider compoundServiceProvider : sps) {
          scopeCompoundServiceProvider(map, compoundServiceProvider);
        }
      }
    }
  }

  /*
   * Based on https://wiki.surfnetlabs.nl/display/services/App-omschrijving we
   * tell the Service to limit scope access based on the authority
   */
  @SuppressWarnings("unchecked")
  private void scopeCompoundServiceProvider(ModelMap map, CompoundServiceProvider sp) {
    Collection<CoinAuthority> authorities = (Collection<CoinAuthority>) SpringSecurity.getCurrentUser().getAuthorities();

    map.put(SERVICE_QUESTION_ALLOWED,
        containsRole(authorities, ROLE_DISTRIBUTION_CHANNEL_ADMIN, ROLE_IDP_LICENSE_ADMIN, ROLE_IDP_SURFCONEXT_ADMIN));
    map.put(SERVICE_APPLY_ALLOWED, containsRole(authorities, ROLE_DISTRIBUTION_CHANNEL_ADMIN, ROLE_IDP_SURFCONEXT_ADMIN));

    AttributeScopeConstraints constraints = new AttributeScopeConstraints();

    /*
     * We only veto ROLE_USER
     */
    if (CollectionUtils.isEmpty(authorities)
        || ((authorities.size() == 1 && authorities.iterator().next().getEnumAuthority().equals(Authority.ROLE_USER)))) {
      constraints.addAttributeScopeConstraint(INSTITUTION_DESCRIPTION_EN, INSTITUTION_DESCRIPTION_NL, TECHNICAL_SUPPORTMAIL);
    }
    sp.setConstraints(constraints);
  }

  private boolean containsRole(Collection<CoinAuthority> coinAuthorities, Authority... authority) {
    Set<Authority> authorities = new HashSet<CoinAuthority.Authority>();
    for (CoinAuthority grantedAuth : coinAuthorities) {
      authorities.add(grantedAuth.getEnumAuthority());
    }
    for (Authority auth : authority) {
      if (authorities.contains(auth)) {
        return true;
      }
    }
    return false;
  }

}
