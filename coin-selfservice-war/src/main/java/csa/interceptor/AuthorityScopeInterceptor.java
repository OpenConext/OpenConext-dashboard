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

package csa.interceptor;

import static csa.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import csa.control.BaseController;
import csa.domain.CoinAuthority.Authority;
import csa.domain.CoinUser;
import csa.domain.CompoundServiceProvider;

/**
 * Interceptor to de-scope the visibility {@link CompoundServiceProvider}
 * objects for display
 *
 * See <a
 * href="https://wiki.surfnetlabs.nl/display/services/App-omschrijving">https
 * ://wiki.surfnetlabs.nl/display/services/App-omschrijving</a>
 */
public class AuthorityScopeInterceptor extends HandlerInterceptorAdapter {

  /**
   * The OAuth 2.0 scope used for actions-related requests.
   */
  public static final String OAUTH_CLIENT_SCOPE_ACTIONS = "actions";

  /**
   * The OAuth 2.0 scope used for requests that provide an IDP-id themselves, without need to rely on user authorization.
   */
  public static final String OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES = "cross-idp-services";

  /**
   * The OAuth 2.0 scope used for requests that provide statistical information.
   */
  public static final String OAUTH_CLIENT_SCOPE_STATISTICS = "stats";

  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    if (modelAndView == null) {
      return ;
    }

    Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication()).ifPresent(authentication -> {
      CoinUser coinUser = (CoinUser) authentication.getPrincipal();
      ModelMap map = modelAndView.getModelMap();
      scopeGeneralAuthCons(map, coinUser.getAuthorityEnums());
    });
  }

  protected void scopeGeneralAuthCons(ModelMap map, List<Authority> authorities) {
    boolean isAdmin = containsRole(authorities, ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    map.put(BaseController.SERVICE_CONNECTION_VISIBLE, isAdmin);
    map.put(BaseController.FACET_CONNECTION_VISIBLE, isAdmin);
    map.put(BaseController.DEEPLINK_TO_SURFMARKET_ALLOWED, isAdmin);
    map.put(BaseController.IS_GOD, isAdmin);
  }

  protected static boolean containsRole(List<Authority> authorities, Authority... authority) {
    return Arrays.stream(authority).anyMatch(authorities::contains);
  }

}
