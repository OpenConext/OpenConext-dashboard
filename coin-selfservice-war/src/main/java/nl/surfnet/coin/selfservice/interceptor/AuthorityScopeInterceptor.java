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

import static nl.surfnet.coin.selfservice.control.BaseController.*;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import static org.springframework.web.bind.annotation.RequestMethod.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.IdentitySwitch;
import nl.surfnet.coin.selfservice.util.SpringSecurity;


/**
 * Interceptor to de-scope the visibility
 * objects for display
 * <p/>
 * See <a
 * href="https://wiki.surfnetlabs.nl/display/services/App-omschrijving">https
 * ://wiki.surfnetlabs.nl/display/services/App-omschrijving</a>
 */
public class AuthorityScopeInterceptor extends HandlerInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorityScopeInterceptor.class);

  private static List<String> TOKEN_CHECK_METHODS = Arrays.asList(POST.name(), DELETE.name(), PUT.name());

  @Resource
  private Csa csa;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (TOKEN_CHECK_METHODS.contains(request.getMethod().toUpperCase())) {
      String token = request.getParameter(TOKEN_CHECK);
      String sessionToken = (String) request.getSession().getAttribute(TOKEN_CHECK);
      if (StringUtils.isBlank(token) || !token.equals(sessionToken)) {
        throw new SecurityException(String.format("Token from session '%s' does not match token '%s' from request", sessionToken, token));
      }
    }
    return true;
  }

  @SuppressWarnings("unchecked")
  @Override
  public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
    throws Exception {

    if (modelAndView != null) {

      List<Authority> authorities = SpringSecurity.getCurrentUser().getAuthorityEnums();

      final ModelMap map = modelAndView.getModelMap();
      scopeGeneralAuthCons(map, authorities);

      addTokenToModelMap(request, response, map);

      addSwitchIdentity(map, authorities, request);
    }
  }

  @SuppressWarnings("unchecked")
  private void addSwitchIdentity(ModelMap map, List<Authority> authorities, HttpServletRequest request) {

    if (containsRole(authorities, ROLE_DASHBOARD_SUPER_USER)) {
      List<InstitutionIdentityProvider> idps = (List<InstitutionIdentityProvider>) request.getSession().getAttribute(INSTITUTION_IDENTITY_PROVIDERS);
      if (idps == null) {
        idps = csa.getAllInstitutionIdentityProviders();
        Collections.sort(idps, new Comparator<InstitutionIdentityProvider>() {
          @Override
          public int compare(final InstitutionIdentityProvider lh, final InstitutionIdentityProvider rh) {
            return lh.getName().compareTo(rh.getName());
          }
        });
        request.getSession().setAttribute(INSTITUTION_IDENTITY_PROVIDERS, idps);
      }
      map.put(INSTITUTION_IDENTITY_PROVIDERS, idps);
    }
    IdentitySwitch identitySwitch = (IdentitySwitch) request.getSession().getAttribute(SWITCHED_IDENTITY_SWITCH);
    if (identitySwitch == null) {
      request.getSession().setAttribute(SWITCHED_IDENTITY_SWITCH, new IdentitySwitch());
    }
  }

  private void scopeGeneralAuthCons(ModelMap map, List<Authority> authorities) {
    map.put(SERVICE_QUESTION_ALLOWED, containsRole(authorities, ROLE_DASHBOARD_ADMIN));
    map.put(SERVICE_APPLY_ALLOWED, containsRole(authorities, ROLE_DASHBOARD_ADMIN));
    map.put(SERVICE_CONNECTION_VISIBLE, containsRole(authorities, ROLE_DASHBOARD_ADMIN, ROLE_DASHBOARD_VIEWER));
    map.put(SHOW_ARP_MATCHES_PROVIDED_ATTRS, containsRole(authorities, ROLE_DASHBOARD_VIEWER, ROLE_DASHBOARD_ADMIN));
  }

  protected static boolean containsRole(List<Authority> authorities, Authority... authority) {
    for (Authority auth : authority) {
      if (authorities.contains(auth)) {
        return true;
      }
    }
    return false;
  }

  /*
   * Add a security token to the modelMap that is rendered as hidden value in
   * POST forms. In the preHandle we check if the request is a POST and expect
   * equality of the token send as request parameter and the token stored in the
   * session
   */
  private void addTokenToModelMap(HttpServletRequest request, HttpServletResponse response, ModelMap map) {
    String token = UUID.randomUUID().toString();
    map.addAttribute(TOKEN_CHECK, token);
    request.getSession().setAttribute(TOKEN_CHECK, token);
  }
}
