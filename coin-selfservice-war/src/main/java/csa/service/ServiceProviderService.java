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

package csa.service;

import java.util.List;

import csa.domain.ServiceProvider;

/**
 * Interface of services that returns information about Service Providers (SPs).
 *
 */
public interface ServiceProviderService {

  /**
   * Get a list of all available Service Providers for the given idpId.
   *
   * @param idpId the IDP entity ID to filter on
   * @return list of {@link ServiceProvider}
   */
  List<ServiceProvider> getAllServiceProviders(String idpId);

  /**
   * Get a {@link ServiceProvider} by its entity ID.
   *
   * @param spEntityId the entity id of the ServiceProvider
   * @param idpEntityId the entity id of the Identity Provider.
   * @return the {@link ServiceProvider} object.
   */
  ServiceProvider getServiceProvider(String spEntityId, String idpEntityId);

  /**
   * Get a {@link ServiceProvider} by its entity ID, without a idpEntityId
   *
   * @param spEntityId the entity id of the ServiceProvider
   * @return the {@link ServiceProvider} object.
   */
  ServiceProvider getServiceProvider(String spEntityId);

  /**
   * Get a list of all available Service Providers (IDP independent).
   *
   * @return list of {@link ServiceProvider}
   */
  List<ServiceProvider> getAllServiceProviders(boolean includeArps);

  List<ServiceProvider> getAllServiceProvidersRateLimited(long rateDelay);

  void refreshExampleSingleTenants();
}
