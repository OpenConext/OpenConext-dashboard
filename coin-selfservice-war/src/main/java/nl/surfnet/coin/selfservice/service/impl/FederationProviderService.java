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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import nl.surfnet.coin.selfservice.domain.FederatieConfig;
import nl.surfnet.coin.selfservice.domain.Provider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ProviderService;
import nl.surfnet.coin.selfservice.util.XStreamFedConfigBuilder;

/**
 * Provider Service seeded with xml config from federation.
 */
public class FederationProviderService implements ProviderService {

  private static final Logger LOG = LoggerFactory.getLogger(FederationProviderService.class);
  private FederatieConfig federatieConfig;

  /**
   * Constructor
   * @param configurationFilename Filename on classpath that contains federation configuration to parse and use.
   *
   */

  public FederationProviderService(String configurationFilename) {
    final XStream xStream = XStreamFedConfigBuilder.getXStreamForFedConfig(true);
    Resource resource = new ClassPathResource(configurationFilename);
    try {
      federatieConfig = (FederatieConfig) xStream.fromXML(resource.getInputStream());
    } catch (IOException e) {
      LOG.error("While reading file '" + configurationFilename + "'", e);
    }
  }

  @Override
  @Cacheable(value = { "sps-federation" })
  public List<Provider> getLinkedServiceProviders(String idpId) {
    List<Provider> providers = new ArrayList<Provider>();
    for (ServiceProvider sp : federatieConfig.getSps()) {
      if (sp.getAcl() != null && sp.getAcl().getIdpRefs() != null && sp.getAcl().getIdpRefs().contains(idpId)) {
        providers.add(sp);
      }
    }
    return providers;
  }

  @Override
  @Cacheable(value = { "sps-federation" })
  public ServiceProvider getServiceProvider(String spEntityId) {
    for (ServiceProvider sp : federatieConfig.getSps()) {
      if (sp.getId().equals(spEntityId)) {
        return sp;
      }
    }
    return null;
  }
}