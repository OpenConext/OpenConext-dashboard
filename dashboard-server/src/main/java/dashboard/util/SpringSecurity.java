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

package dashboard.util;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.isNullOrEmpty;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;

public class SpringSecurity {

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

  public static void ensureAccess(IdentityProvider idp) {
    validateIdp(idp);
  }

  public static void clearSwitchedIdp() {
    CoinUser currentUser = SpringSecurity.getCurrentUser();
    if (!currentUser.isSuperUser()) {
      throw new SecurityException("You need to be a super user to clear the selected idp");
    }
    currentUser.removeAuthority(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN));
    currentUser.removeAuthority(new CoinAuthority(Authority.ROLE_DASHBOARD_VIEWER));
    SpringSecurity.getCurrentUser().setSwitchedToIdp(null);
  }

  public static void setSwitchedToIdp(IdentityProvider idp, String role) {
    validateIdp(checkNotNull(idp));

    if (!isNullOrEmpty(role)) {
      CoinUser currentUser = SpringSecurity.getCurrentUser();

      currentUser.removeAuthority(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN));
      currentUser.addAuthority(new CoinAuthority(Authority.valueOf(role)));
    }

    SpringSecurity.getCurrentUser().setSwitchedToIdp(idp);
  }

  public static IdentityProvider validateIdp(IdentityProvider idp) {
    if (SpringSecurity.getCurrentUser().isSuperUser()) {
      return idp;
    } else {
      return SpringSecurity.getCurrentUser().getInstitutionIdps().stream()
          .filter(provider -> provider.getId().equals(idp.getId()))
          .findFirst()
          .orElseThrow(() -> new SecurityException(idp.getId() + " is unknown for " + SpringSecurity.getCurrentUser().getUsername()));
    }
  }

}
