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

package nl.surfnet.coin.selfservice.dao;

public interface LmngIdentifierDao {
  
  /**
   * Method that returns a identifier for the IdP in LMNG belonging to the given IdP-Id
   *  
   * @param identityProviderId the identifier as used in this project for an IdP
   * @return the identifier as used in LMNG for an IdP
   */
  String getLmngIdForIdentityProviderId(String identityProviderId);
  
  /**
   * Method that returns a identifier for the IdP belonging to the given LMNG-Id
   *  
   * @param lnmgId the identifier as used in LMNG for an IdP
   * @return the identifier as used in this project for an IdP
   */
  String getIdentityProviderIdForLmngId(String lnmgId);

  /**
   * Method that returns a identifier for the SP in LMNG belonging to the given SP-Id
   *  
   * @param spId the identifier as used in this project for an SP
   * @return the identifier as used in LMNG for an SP
   */
  String getLmngIdForServiceProviderId(String spId);
  
}
