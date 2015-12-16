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
package selfservice.service;

import java.util.List;

import selfservice.domain.License;
import selfservice.domain.csa.Account;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.IdentityProvider;
import selfservice.service.impl.LmngException;

/**
 * Interface of services that return Licensing and article information from LMNG
 * (Licentie Modellen Next Generation)
 */
public interface CrmService {

  /**
   * Gets a list with Licenses for the given
   * identityProvider and services (lmngIdentifiers) which are valid on the given day
   *
   * @param identityProvider the identityProvider to get the licenses for
   * @param articleIdentifier lmngIdentifier (belonging to SP's) where the licenses are for.
   * @return a (possible) list of licenses
   */
  List<License> getLicensesForIdpAndSp(IdentityProvider identityProvider, String articleIdentifier);

  /**
   * Get articles for the given serviceProviders.
   * @param serviceProviderEntityIds list of ID's of serviceproviders to get the lmng article for
   * @return a list of possible articles
   * @throws LmngException
   */
  List<Article> getArticlesForServiceProviders(List<String> serviceProviderEntityIds) throws LmngException;

  /**
   * Get the name of the institution in LMNG belonging to given GUID
   *
   * @param guid
   *          guid of the IDP to check
   * @return the name of the institution in LMNG
   */
  String getInstitutionName(String guid);

  /**
   * Get LMNG article by article ID
   *
   * @param guid
   *          guid of the article
   * @return the article found or null
   */
  Article getService(final String guid);

  /**
   * Get the name of the service/product in LMNG belonging to given GUID
   *
   * @param lmngId
   * @return
   */
  String getServiceName(String lmngId);

  /**
   * Get all institutions
   *
   * @return all accounts of the institutions / serviceproviders known in LMNG
   */
  List<Account> getAccounts(boolean isInstitution);

  /**
   * Convenience method to run various queries
   * @param rawQuery the fetch string
   * @return the raw response
   */
  String performQuery(String rawQuery);

  void evictCache();

}
