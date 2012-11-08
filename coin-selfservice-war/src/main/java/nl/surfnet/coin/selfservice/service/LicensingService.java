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

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

/**
 * Interface of services that return Licensing information from LMNG (Licentie
 * Modellen Next Generation)
 */
public interface LicensingService {

  /**
   * Gets a list with Articles with possible Licenses for the given identityProvider and
   * serviceProvider which are valid on the given day
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param serviceProviders
   *          the serviceProviders to get the licenses for
   * @param validOn
   *          Date on which the license should be valid
   * @return a (possible) list of articles with valid licenses
   */
  List<Article> getArticleForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders, Date validOn);

  /**
   * Get the name of the institution in LMNG belonging to given GUID
   * @param guid guid of the IDP to check
   * @return the name of the institution in LMNG
   */
  String getInstitutionName(String guid);
  
  /**
   * Get the name of the service/product in LMNG belonging to given GUID
   * 
   * @param lmngId
   * @return
   */
  String getServiceName(String lmngId);
  
  /**
   * Is the LMNG service active? If not then no calls should be made and the
   * entire distribution channel runs without license / article information from
   * LMNG.
   * 
   * @return whether the LicensingService is active
   */
  public boolean isActiveMode();

}
