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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
public class CompoundSPService {

  Logger LOG = LoggerFactory.getLogger(CompoundSPService.class);

  @Resource
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Resource(name="providerService")
  private ServiceProviderService serviceProviderService;

  @Resource
  private LicensingService licensingService;

  @Cacheable("default")
  public List<CompoundServiceProvider> getCSPsByIdp(IdentityProvider identityProvider) {

    // Base: the list of all service providers for this IDP
    List<ServiceProvider> allServiceProviders = serviceProviderService.getAllServiceProviders(identityProvider.getId());

    // Reference data: all compound service providers
    List<CompoundServiceProvider> allBareCSPs = compoundServiceProviderDao.findAll();
    // Mapped by its SP entity ID
    Map<String, CompoundServiceProvider> mapByServiceProviderEntityId = mapByServiceProviderEntityId(allBareCSPs);

    // Build a list of CSPs. Create new ones for SPs that have no CSP yet.
    List<CompoundServiceProvider> all = new ArrayList<CompoundServiceProvider>();
    for(ServiceProvider sp : allServiceProviders) {
      if (mapByServiceProviderEntityId.containsKey(sp.getId())) {
        CompoundServiceProvider csp = mapByServiceProviderEntityId.get(sp.getId());
        enrich(identityProvider, csp);
        all.add(csp);
      } else {
        LOG.debug("No CompoundServiceProvider yet for SP with id {}, will create a new one.", sp.getId());
        CompoundServiceProvider csp = createCompoundServiceProvider(sp);
        all.add(csp);
      }
    }
    return all;
  }

  public CompoundServiceProvider createCompoundServiceProvider(ServiceProvider sp) {
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp, new License());
    compoundServiceProviderDao.saveOrUpdate(csp);
    return csp;
  }

  private Map<String, CompoundServiceProvider> mapByServiceProviderEntityId(List<CompoundServiceProvider> allCSPs) {
    Map<String, CompoundServiceProvider> map = new HashMap<String, CompoundServiceProvider>();
    for (CompoundServiceProvider csp : allCSPs) {
      map.put(csp.getServiceProviderEntityId(), csp);
    }
    return map;
  }

  public CompoundServiceProvider getCSPById(IdentityProvider idp, long compoundSpId) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findById(compoundSpId);
    enrich(idp, csp);
    return csp;
  }

  protected void enrich(IdentityProvider idp, CompoundServiceProvider csp) {
    csp.setServiceProvider(serviceProviderService.getServiceProvider(csp.getServiceProviderEntityId()));
    List<License> licenses = licensingService.getLicensesForIdentityProviderAndServiceProvider(idp, csp.getServiceProvider());
    if (licenses.size() == 1) {
      csp.setLicense(licenses.get(0));
    } else if (licenses.isEmpty()) {
      LOG.debug("No license for idp {} and SP {}", idp.getId(), csp.getServiceProvider().getId());
    } else {
      LOG.info("Multiple licenses found for idp {} and SP {}: {}",
        new Object[] {idp.getId(), csp.getServiceProvider().getId(), licenses.size()});
    }
  }
}
