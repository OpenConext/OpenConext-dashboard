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

import java.util.List;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;

/**
 * Interface of Identity Provider Services.
 */
public interface IdentityProviderService {

  /**
   * Get an identity provider by its id.
   * @param idpEntityId the id.
   * @return IdentityProvider
   */
  IdentityProvider getIdentityProvider(String idpEntityId);

  /**
   * Get a list of all idps that have the same instituteId as the given one.
   * @param instituteId the instituteId
   * @return List&lt;IdentityProvider&gt;
   */
  List<IdentityProvider> getInstituteIdentityProviders(String instituteId);
  
  /**
   * Get a list of all idps 
   * @param instituteId the instituteId
   * @return List&lt;IdentityProvider&gt;
   */
  List<IdentityProvider> getAllIdentityProviders();
  
  
}
