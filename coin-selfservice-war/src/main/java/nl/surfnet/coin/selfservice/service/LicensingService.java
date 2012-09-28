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

package nl.surfnet.coin.selfservice.service;

import java.util.Date;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

/**
 * Interface of services that return Licensing information from LMNG (Licentie
 * Modellen Next Generation)
 */
public interface LicensingService {

  /**
   * Gets a list with Licenses for the given identityProvider which are valid
   * today.
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @return a list of possible valid licenses
   */
  List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider);

  /**
   * Gets a list with Licenses for the given identityProvider which are valid on
   * the given day
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param validOn
   *          Date on which the license should be valid
   * @return a list of possible valid licenses
   */
  List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider, Date validOn);
  
  /**
   * Gets a list with Licenses for the given identityProvider and serviceProvider which are valid
   * today.
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param serviceProvider
   *          the serviceProvider to get the licenses for
   * @return a list of possible valid licenses
   */
  List<License> getLicensesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider, ServiceProvider serviceProvider);

  /**
   * Gets a list with Licenses for the given identityProvider and serviceProvider which are valid on
   * the given day
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param serviceProvider
   *          the serviceProvider to get the licenses for
   * @param validOn
   *          Date on which the license should be valid
   * @return a list of possible valid licenses
   */
  List<License> getLicensesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider, ServiceProvider serviceProvider, Date validOn);

}
