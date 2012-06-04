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

package nl.surfnet.coin.selfservice.control;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;

/**
 * Abstract controller used to set model attributes to the request
 */
@Controller
public abstract class BaseController {

  @Autowired
  private IdentityProviderService idpService;

  @ModelAttribute(value = "idps")
  public List<IdentityProvider> getMyInstitutionIdps() {
    return getCurrentUser().getInstitutionIdps();
  }

  @ModelAttribute(value = "selectedidp")
  public IdentityProvider getRequestedIdp(@RequestParam(required = false) String idpId, HttpServletRequest request) {
    final Object selectedidp = request.getSession().getAttribute("selectedidp");
    if (idpId == null && selectedidp != null) {
      return (IdentityProvider) selectedidp;
    }
    if (idpId == null) {
      idpId = getCurrentUser().getIdp();
    }
    for (IdentityProvider idp : getCurrentUser().getInstitutionIdps()) {
      if (idp.getId().equals(idpId)) {
        request.getSession().setAttribute("selectedidp", idp);
        return idp;
      }
    }
    return null;
  }

  /**
   * Get the IDP Entity Id from the security context.
   * @return String
   * @throws SecurityException in case no principal is found.
   */
  protected static CoinUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      throw new SecurityException("No suitable security context.");
    }
    Object principal = auth.getPrincipal();
    if (principal != null && principal instanceof CoinUser) {
      return (CoinUser) principal;
    }
    throw new SecurityException("No suitable security context.");
  }


}
