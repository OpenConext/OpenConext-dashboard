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
   * Gets a list with Articles with Licenses for the given identityProvider and
   * serviceProvider which are valid on the given day
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param serviceProvider
   *          the serviceProvider to get the licenses for
   * @param validOn
   *          Date on which the license should be valid
   * @return a list of possible articles with valid licenses
   */
  List<Article> getLicenseArticlesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider, ServiceProvider serviceProvider,
      Date validOn);

  /**
   * Gets a list with Articles with Licenses for the given identityProvider and
   * serviceProvider which are valid on the given day
   * 
   * @param identityProvider
   *          the identityProvider to get the licenses for
   * @param serviceProviders
   *          the serviceProviders to get the licenses for
   * @param validOn
   *          Date on which the license should be valid
   * @return a list of possible articles with valid licenses
   */
  List<Article> getLicenseArticlesForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders, Date validOn);

  /**
   * Is the LMNG service active? If not then no calls should be made and the
   * entire distribution channel runs without license / article information from
   * LMNG.
   * 
   * @return whether the LicensingService is active
   */
  public boolean isActiveMode();

  /**
   * Get the Article belonging to te given serviceProvider. We assume we'll get
   * just one article for one serviceprovider (first result). This article will
   * NOT contain a License as this call does not depend on an IDP. This method
   * can be used for retrieving Article information in admin pages or displaying
   * IDP independent article information.
   * 
   * @param serviceProvider
   *          the sp to get the article for
   * @return the article found in LMNG or null
   */
  Article getArticleForServiceProvider(ServiceProvider serviceProvider);

}
