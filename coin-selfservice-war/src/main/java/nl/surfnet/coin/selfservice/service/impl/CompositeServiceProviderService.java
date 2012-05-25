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

package nl.surfnet.coin.selfservice.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;

import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

/**
 * Service Provider Service that can query multiple ServiceProviderServices and will combine the results.
 */
public class CompositeServiceProviderService implements ServiceProviderService {

  private List<ServiceProviderService> serviceProviderServices;

  @Override
  public List<ServiceProvider> getLinkedServiceProviders(String idpId) {
    List<ServiceProvider> ret = new ArrayList<ServiceProvider>();
    for (ServiceProviderService p : serviceProviderServices) {
      final List<ServiceProvider> providers = p.getLinkedServiceProviders(idpId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    Collections.sort(ret);
    return ret;
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    List<ServiceProvider> ret = new ArrayList<ServiceProvider>();
    for (ServiceProviderService p : serviceProviderServices) {
      final List<ServiceProvider> providers = p.getAllServiceProviders(idpId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    Collections.sort(ret);
    return ret;
  }

  /**
   * Returns the first SP found or null if none found.
   *
   * @param spEntityId
   * @return
   */
  @Override
  public ServiceProvider getServiceProvider(String spEntityId) {
    for (ServiceProviderService p : serviceProviderServices) {
      final ServiceProvider sp = p.getServiceProvider(spEntityId);
      if (sp != null) {
        return sp;
      }
    }
    return null;
  }

  public void setServiceProviderServices(List<ServiceProviderService> serviceProviderServices) {
    this.serviceProviderServices = serviceProviderServices;
  }

}
