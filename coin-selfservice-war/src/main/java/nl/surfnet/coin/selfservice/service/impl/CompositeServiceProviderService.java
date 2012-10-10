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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.Provider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.apache.commons.collections.CollectionUtils;

/**
 * Service Provider Service that can query multiple ServiceProviderServices and
 * will combine the results.
 */
public class CompositeServiceProviderService implements ServiceProviderService, IdentityProviderService {

  private List<ServiceProviderService> serviceProviderServices;
  private List<IdentityProviderService> identityProviderServices;

  @Override
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    // we don't want double entries
    Set<ServiceProvider> ret = new HashSet<ServiceProvider>();
    for (ServiceProviderService p : serviceProviderServices) {
      final List<ServiceProvider> providers = p.getAllServiceProviders(idpId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>(ret);
    Collections.sort(sps, Provider.firstStatusThenName());
    return sps;
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders() {
    // we don't want double entries
    Set<ServiceProvider> ret = new HashSet<ServiceProvider>();
    for (ServiceProviderService p : serviceProviderServices) {
      final List<ServiceProvider> providers = p.getAllServiceProviders();
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>(ret);
    Collections.sort(sps, Provider.firstStatusThenName());
    return sps;
  }

  @Override
  public List<ServiceProvider> getLinkedServiceProviders(String idpId) {
    // we don't want double entries
    Set<ServiceProvider> ret = new HashSet<ServiceProvider>();
    for (ServiceProviderService p : serviceProviderServices) {
      final List<ServiceProvider> providers = p.getLinkedServiceProviders(idpId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    List<ServiceProvider> sps = new ArrayList<ServiceProvider>(ret);
    Collections.sort(sps, Provider.firstStatusThenName());
    return sps;
  }

  /**
   * Returns the first SP found or null if none found.
   * 
   * 
   * @param spEntityId
   * @param idpEntityId
   * @return
   */
  @Override
  public ServiceProvider getServiceProvider(String spEntityId, String idpEntityId) {
    for (ServiceProviderService p : serviceProviderServices) {
      final ServiceProvider sp = p.getServiceProvider(spEntityId, idpEntityId);
      if (sp != null) {
        return sp;
      }
    }
    return null;
  }

  public void setServiceProviderServices(List<ServiceProviderService> serviceProviderServices) {
    this.serviceProviderServices = serviceProviderServices;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.selfservice.service.IdentityProviderService#getIdentityProvider
   * (java.lang.String)
   */
  @Override
  public IdentityProvider getIdentityProvider(String idpEntityId) {
    for (IdentityProviderService p : identityProviderServices) {
      final IdentityProvider idp = p.getIdentityProvider(idpEntityId);
      if (idp != null) {
        return idp;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.selfservice.service.IdentityProviderService#
   * getInstituteIdentityProviders(java.lang.String)
   */
  @Override
  public List<IdentityProvider> getInstituteIdentityProviders(String instituteId) {
    // we don't want double entries
    Set<IdentityProvider> ret = new HashSet<IdentityProvider>();
    for (IdentityProviderService p : identityProviderServices) {
      final List<IdentityProvider> providers = p.getInstituteIdentityProviders(instituteId);
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    List<IdentityProvider> idps = new ArrayList<IdentityProvider>(ret);
    Collections.sort(idps, Provider.firstStatusThenName());
    return idps;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.selfservice.service.IdentityProviderService#
   * getAllIdentityProviders()
   */
  @Override
  public List<IdentityProvider> getAllIdentityProviders() {
    // we don't want double entries
    Set<IdentityProvider> ret = new HashSet<IdentityProvider>();
    for (IdentityProviderService p : identityProviderServices) {
      final List<IdentityProvider> providers = p.getAllIdentityProviders();
      if (CollectionUtils.isNotEmpty(providers)) {
        ret.addAll(providers);
      }
    }
    List<IdentityProvider> idps = new ArrayList<IdentityProvider>(ret);
    Collections.sort(idps, Provider.firstStatusThenName());
    return idps;
  }

  public void setIdentityProviderServices(List<IdentityProviderService> identityProviderServices) {
    this.identityProviderServices = identityProviderServices;
  }

}
