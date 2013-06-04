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

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.ui.ModelMap;
import org.springframework.util.CollectionUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.collection.LambdaCollections.with;
import ch.lambdaj.function.matcher.HasArgumentWithValue;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICES;
import static nl.surfnet.coin.selfservice.control.BaseController.DEEPLINK_TO_SURFMARKET_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.FACET_CONNECTION_VISIBLE;
import static nl.surfnet.coin.selfservice.control.BaseController.FILTER_APP_GRID_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.IS_ADMIN_USER;
import static nl.surfnet.coin.selfservice.control.BaseController.IS_GOD;
import static nl.surfnet.coin.selfservice.control.BaseController.RAW_ARP_ATTRIBUTES_VISIBLE;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_APPLY_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_CONNECTION_VISIBLE;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_QUESTION_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.TOKEN_CHECK;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_LICENSE_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_SURFCONEXT_ADMIN;
import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

/**
 * Interceptor to de-scope the visibility {@link CompoundServiceProvider}
 * objects for display
 * 
 * See <a
 * href="https://wiki.surfnetlabs.nl/display/services/App-omschrijving">https
 * ://wiki.surfnetlabs.nl/display/services/App-omschrijving</a>
 */
public class AuthorityScopeInterceptor extends HandlerInterceptorAdapter {

  private static final Logger LOG = LoggerFactory.getLogger(AuthorityScopeInterceptor.class);

  private static List<String> TOKEN_CHECK_METHODS = Arrays.asList(new String[] { POST.name(), DELETE.name(), PUT.name() });

  private boolean exposeTokenCheckInCookie;

  @Override
  public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
    if (TOKEN_CHECK_METHODS.contains(request.getMethod().toUpperCase())) {
      String token = request.getParameter(TOKEN_CHECK);
      String sessionToken = (String) request.getSession().getAttribute(TOKEN_CHECK);
      if (StringUtils.isBlank(token) || !token.equals(sessionToken)) {
        throw new SecurityException(String.format("Token from session '%s' sdoes not match token '%s' from request", sessionToken, token));
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
      Service service = (Service) map.get(SERVICE);
      if (service != null) {
        scopeService(map, service, authorities);
      }

      Collection<Service> services = (Collection<Service>) map.get(SERVICES);
      if (!CollectionUtils.isEmpty(services)) {
        services = scopeListOfServices(services, authorities);
        for (Service service1 : services) {
          scopeService(map, service1, authorities);
        }
        map.put(SERVICES, services);
      }

      scopeGeneralAuthCons(map, authorities, request);

      addTokenToModelMap(request, response,  map);
    }
  }

  protected void scopeGeneralAuthCons(ModelMap map, List<Authority> authorities, final HttpServletRequest request) {
    boolean isAdmin = containsRole(authorities, ROLE_DISTRIBUTION_CHANNEL_ADMIN, ROLE_IDP_LICENSE_ADMIN, ROLE_IDP_SURFCONEXT_ADMIN);
    map.put(SERVICE_QUESTION_ALLOWED, containsRole(authorities, ROLE_IDP_SURFCONEXT_ADMIN));
    map.put(SERVICE_APPLY_ALLOWED, containsRole(authorities, ROLE_IDP_SURFCONEXT_ADMIN));
    map.put(SERVICE_CONNECTION_VISIBLE, containsRole(authorities, ROLE_IDP_SURFCONEXT_ADMIN, ROLE_DISTRIBUTION_CHANNEL_ADMIN));
    map.put(FACET_CONNECTION_VISIBLE, isAdmin);
    map.put(DEEPLINK_TO_SURFMARKET_ALLOWED, containsRole(authorities, ROLE_IDP_LICENSE_ADMIN, ROLE_DISTRIBUTION_CHANNEL_ADMIN));
    map.put(FILTER_APP_GRID_ALLOWED, false); //isAdmin);
    map.put(IS_ADMIN_USER, isAdmin);
    map.put(IS_GOD, isDistributionChannelGod(authorities));
    map.put(RAW_ARP_ATTRIBUTES_VISIBLE, containsRole(authorities, ROLE_IDP_SURFCONEXT_ADMIN));
  }

  /**
   * Reduce list based on whether the SP 'is linked' to the current IdP.
   * 
   * @return a reduced list, or the same, if no changes.
   */
  protected Collection<Service> scopeListOfServices(Collection<Service> services,
                                                                    List<Authority> authorities) {
    HasArgumentWithValue<Object, Boolean> linkedSpsMatcher = having(on(Service.class).isConnected());
    if (isRoleUser(authorities)) {
      services = with(services).retain(having(on(Service.class).isConnected()));
      LOG.debug("Reduced the list of services to only linked services, because user '{}' is an enduser.", SpringSecurity.getCurrentUser().getUid());
    } else if (isRoleIdPLicenseAdmin(authorities)) {
      HasArgumentWithValue<Object, Boolean> articleAvailableMatcher = having(on(Service.class).isHasCrmLink());

      services = with(services).retain(linkedSpsMatcher.or(articleAvailableMatcher));

      LOG.debug("Reduced the list of services to only linked services, because user '{}' is an license IdP user", SpringSecurity.getCurrentUser()
          .getUid());
    }
    return services;
  }

  /*
   * Based on https://wiki.surfnetlabs.nl/display/services/App-omschrijving we
   * tell the Service to limit scope access based on the authority
   */
  protected void scopeService(ModelMap map, Service service, List<Authority> authorities) {

    // Do not allow normal users to view 'unlinked' services, even if requested
    // explicitly.
    if (isRoleUser(authorities) && !service.isConnected()) {
      LOG.info(
        "user requested service details of service with id {} although this SP is not 'linked'. Will throw AccessDeniedException('Access denied').",
        service.getId());
      throw new AccessDeniedException("Access denied");
    }

    // Remove all properties from service that user does not have access to.
    if (isRoleUser(authorities)) {
      service.setSupportMail(null);
    }
  }

  protected boolean isRoleUser(List<Authority> authorities) {
    return CollectionUtils.isEmpty(authorities) || ((authorities.size() == 1 && authorities.get(0).equals(Authority.ROLE_USER)));
  }

  protected boolean isRoleIdPLicenseAdmin(List<Authority> authorities) {
    return containsRole(authorities, ROLE_IDP_LICENSE_ADMIN)
        && !containsRole(authorities, ROLE_IDP_SURFCONEXT_ADMIN, ROLE_DISTRIBUTION_CHANNEL_ADMIN);
  }

  protected static boolean containsRole(List<Authority> authorities, Authority... authority) {
    for (Authority auth : authority) {
      if (authorities.contains(auth)) {
        return true;
      }
    }
    return false;
  }
  
  protected boolean isDistributionChannelGod(List<Authority> authorities) {
    return containsRole(authorities, ROLE_DISTRIBUTION_CHANNEL_ADMIN);
  }

  public static boolean isDistributionChannelAdmin() {
    return containsRole(SpringSecurity.getCurrentUser().getAuthorityEnums(), ROLE_DISTRIBUTION_CHANNEL_ADMIN);
  }

  public void setExposeTokenCheckInCookie(boolean exposeTokenCheckInCookie) {
    this.exposeTokenCheckInCookie = exposeTokenCheckInCookie;
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
    if (exposeTokenCheckInCookie) {
      response.addCookie(new Cookie(TOKEN_CHECK, token));
    }
  }

}
