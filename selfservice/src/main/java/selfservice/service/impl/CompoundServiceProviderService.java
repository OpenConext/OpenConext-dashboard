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
package selfservice.service.impl;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import selfservice.dao.CompoundServiceProviderDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.serviceregistry.Manage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.StreamSupport.stream;

/**
 * Abstraction for the Compound Service Providers. This deals with persistence
 * and linking to Service Providers
 */
@Service
public class CompoundServiceProviderService {

  private final Logger LOG = LoggerFactory.getLogger(CompoundServiceProviderService.class);

  @Autowired
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Autowired
  private Manage manage;

  public List<CompoundServiceProvider> getAllCSPs() {
    return getCSPs(null, manage.getAllServiceProviders());
  }

  public List<CompoundServiceProvider> getAllBareCSPs() {
    List<ServiceProvider> allServiceProviders = manage.getAllServiceProviders();
    return getCSPs(null, allServiceProviders);
  }

  public List<CompoundServiceProvider> getCompoundServiceProvidersByIdp(IdentityProvider identityProvider) {
    if (identityProvider == null) {
      return new ArrayList<>();
    }

    List<ServiceProvider> allServiceProviders = manage.getAllServiceProviders(identityProvider.getId());

    return getCSPs(identityProvider, allServiceProviders);
  }

  private List<CompoundServiceProvider> getCSPs(IdentityProvider identityProvider, List<ServiceProvider> allServiceProviders) {
    Map<String, CompoundServiceProvider> mapByServiceProviderEntityId = stream(compoundServiceProviderDao.findAll().spliterator(), false)
        .collect(toMap(csp -> csp.getServiceProviderEntityId(), identity()));

    return allServiceProviders.stream()
      .map(sp -> ofNullable(mapByServiceProviderEntityId.get(sp.getId()))
          .map(csp -> compound(identityProvider, sp, csp))
          .orElseGet(() -> {
            LOG.debug("No CompoundServiceProvider yet for SP with id {}, will create a new one.", sp.getId());
            return createCompoundServiceProvider(identityProvider, sp);
          })
      ).collect(Collectors.toList());
  }

  private CompoundServiceProvider compound(IdentityProvider identityProvider, ServiceProvider sp, CompoundServiceProvider csp) {
    csp.setServiceProvider(sp);
    return csp;
  }

  private CompoundServiceProvider createCompoundServiceProvider(IdentityProvider idp, ServiceProvider sp) {
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp);

    try {
      return compoundServiceProviderDao.save(csp);
    } catch (RuntimeException e) {
      if (e instanceof HibernateException || e instanceof DataAccessException) {
        //let's give the database another try, otherwise rethrow
        CompoundServiceProvider cspFromDb = compoundServiceProviderDao.findByServiceProviderEntityId(sp.getId());
        if (cspFromDb != null) {
          return compound(idp, sp, cspFromDb);
        }
      }
      throw e;
    }
  }

  public CompoundServiceProvider getCSPById(IdentityProvider idp, long compoundSpId) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(compoundSpId);
    if (csp == null) {
      LOG.debug("Cannot find CSP by id {}, will return null", compoundSpId);
      return null;
    }
    ServiceProvider sp = manage.getServiceProvider(csp.getServiceProviderEntityId(), idp.getId());
    if (sp == null) {
      LOG.info("Cannot get serviceProvider by known entity id: {}, cannot enrich CSP with SP information.", csp.getServiceProviderEntityId());
      return csp;
    }
    csp.setServiceProvider(sp);

    return csp;
  }

  public CompoundServiceProvider getCSPByServiceProviderEntityId(String serviceProviderEntityId) {
    ServiceProvider serviceProvider = manage.getServiceProvider(serviceProviderEntityId).orElseThrow(RuntimeException::new);
    checkNotNull(serviceProvider, "No such SP with entityId: " + serviceProviderEntityId);

    return getCSPByServiceProvider(serviceProvider);
  }

  public CompoundServiceProvider getCSPByServiceProvider(ServiceProvider serviceProvider) {
    checkNotNull(serviceProvider, "ServiceProvider may not be null");

    CompoundServiceProvider compoundServiceProvider = compoundServiceProviderDao.findByServiceProviderEntityId(serviceProvider.getId());
    if (compoundServiceProvider == null) {
      LOG.debug("No compound Service Provider for SP '{}' yet. Will init one and persist.", serviceProvider.getId());
      compoundServiceProvider = compoundServiceProviderDao.save(compoundServiceProvider);
      LOG.debug("Persisted a CompoundServiceProvider with id {}");
    } else {
      compoundServiceProvider.setServiceProvider(serviceProvider);
      compoundServiceProvider.updateTransientOriginFields();
    }
    return compoundServiceProvider;
  }


}
