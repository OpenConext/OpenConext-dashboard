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

package selfservice.util;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.service.Csa;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class SpringSecurity {

  /*
     * For a super users to be able to switch back to his / hers original identity (including
     * the InstitutionIdentityProvider) we need to store the reference.
     */
  private static InstitutionIdentityProvider impersonatedIdentityProvider;

  /**
   * Get the  currently logged in user from the security context.
   *
   * @return String
   * @throws SecurityException in case no principal is found.
   */
  public static CoinUser getCurrentUser() {
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth == null) {
      return new CoinUser();
    }
    Object principal = auth.getPrincipal();
    if (principal != null && principal instanceof CoinUser) {
      return (CoinUser) principal;
    }
    return new CoinUser();
  }

  /**
   * @return
   */
  public static boolean isFullyAuthenticated() {
    final Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    return authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof CoinUser;
  }

  public static InstitutionIdentityProvider getImpersonatedIdentityProvider() {
    return impersonatedIdentityProvider;
  }

  public static void setImpersonatedIdentityProvider(InstitutionIdentityProvider impersonatedIdentityProvider) {
    SpringSecurity.impersonatedIdentityProvider = impersonatedIdentityProvider;
  }

  public static void ensureAccess(Csa csa, final String idpId) {
    validateIdp(getIdpFromId(csa, idpId));
  }

  public static void setSwitchedToIdp(Csa csa, final String idpId, final String role) {
    InstitutionIdentityProvider idp = (idpId != null ? validateIdp(getIdpFromId(csa, idpId)) : null);
    CoinUser currentUser = SpringSecurity.getCurrentUser();

    if (idp == null) {
      currentUser.setAuthorities(new HashSet<>(Collections.singleton(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER))));
    } else if (!role.isEmpty()) {
      currentUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.valueOf(role)));
    }

    SpringSecurity.getCurrentUser().setSwitchedToIdp(idp);
  }

  public static InstitutionIdentityProvider validateIdp(final InstitutionIdentityProvider idp) {
    if (SpringSecurity.getCurrentUser().isSuperUser()) {
      return idp;
    } else {
      Optional<InstitutionIdentityProvider> currentInstitutionIdentityProvider = SpringSecurity.getCurrentUser().getInstitutionIdps().stream().filter(provider -> provider.getId().equals(idp.getId())).findFirst();
      return currentInstitutionIdentityProvider.orElseThrow(() -> new SecurityException(idp.getId() + " is unknown for " + SpringSecurity.getCurrentUser().getUsername()));
    }
  }

  public static InstitutionIdentityProvider getIdpFromId(Csa csa, String idp) {
    List<InstitutionIdentityProvider> idps = csa.getAllInstitutionIdentityProviders();
    for (InstitutionIdentityProvider identityProvider : idps) {
      if (identityProvider.getId().equalsIgnoreCase(idp)) {
        return identityProvider;
      }
    }
    throw new SecurityException(idp + " does not exist");
  }
}
