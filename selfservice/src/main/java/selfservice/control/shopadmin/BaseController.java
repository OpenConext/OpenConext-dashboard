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
package selfservice.control.shopadmin;

import java.util.List;
import java.util.Locale;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.LocaleResolver;

import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;

/**
 * Abstract controller used to set model attributes to the request
 */
@Controller
public abstract class BaseController {

  /**
   * The name of the key under which all compoundSps (e.g. the services) are stored
   */
  public static final String COMPOUND_SPS = "compoundSps";

  /**
   * The name of the key under which a compoundSps (e.g. the service) is stored
   * for the detail view
   */
  public static final String COMPOUND_SP = "compoundSp";

  /**
   * The name of the key under which we store the info if the status of a
   * technical connection is visible to the current user.
   */
  public static final String SERVICE_CONNECTION_VISIBLE = "connectionVisible";

  /**
   * The name of the key under which we store the info if the connection facet is visible to the current user.
   */
  public static final String FACET_CONNECTION_VISIBLE = "facetConnectionVisible";

  /**
   * The name of the key that defines whether a deeplink to SURFMarket should be
   * shown.
   */
  public static final String DEEPLINK_TO_SURFMARKET_ALLOWED = "deepLinkToSurfMarketAllowed";

  /**
   * The name of the key under which we store the info if the logged in user is
   * CSA Admin (aka God)
   */
  public static final String IS_GOD = "isGod";

  /**
   * The name of the key under which we store the token used to prevent session
   * hijacking
   */
  public static final String TOKEN_CHECK = "tokencheck";

  /**
   * Key in which we store the currently selected IdP
   */
  protected static final String SELECTED_IDP = "selectedIdp";

  @Resource(name = "localeResolver")
  protected LocaleResolver localeResolver;

  @ModelAttribute(value = "idps")
  public List<IdentityProvider> getMyInstitutionIdps() {
    CoinUser user = (CoinUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return user.getInstitutionIdps();
  }

  @ModelAttribute(value = "locale")
  public Locale getLocale(HttpServletRequest request) {
    return localeResolver.resolveLocale(request);
  }

  protected IdentityProvider getSelectedIdp(HttpServletRequest request) {
    final IdentityProvider selectedIdp = (IdentityProvider) request.getSession().getAttribute(SELECTED_IDP);
    CoinUser user = (CoinUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    if (selectedIdp != null) {
      return selectedIdp;
    }
    return selectProvider(request, user.getIdp().getId());
  }

  private IdentityProvider selectProvider(HttpServletRequest request, String idpId) {
    Assert.hasText(idpId);
    CoinUser user = (CoinUser) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    for (IdentityProvider idp : user.getInstitutionIdps()) {
      if (idp.getId().equals(idpId)) {
        request.getSession().setAttribute(SELECTED_IDP, idp);
        user.setIdp(idp);
        return idp;
      }
    }
    throw new RuntimeException(idpId + " is unknown for " + user.getUsername());
  }


}
