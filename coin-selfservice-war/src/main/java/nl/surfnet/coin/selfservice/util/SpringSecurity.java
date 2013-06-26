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

package nl.surfnet.coin.selfservice.util;

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

}