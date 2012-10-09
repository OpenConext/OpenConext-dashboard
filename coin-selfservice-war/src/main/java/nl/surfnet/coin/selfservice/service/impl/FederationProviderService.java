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
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

import nl.surfnet.coin.selfservice.domain.FederatieConfig;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.IdentityProviderService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.SpringSchedulerLoadConfigurationService;
import nl.surfnet.coin.selfservice.util.XStreamFedConfigBuilder;

/**
 * Provider Service seeded with xml config from federation.
 */
public class FederationProviderService implements ServiceProviderService, IdentityProviderService,
    SpringSchedulerLoadConfigurationService {

  private static final Logger LOG = LoggerFactory.getLogger(FederationProviderService.class);
  private final String configurationLocation;
  private FederatieConfig federatieConfig = new FederatieConfig();

  /**
   * Constructor
   *
   * @param configurationLocation Location for the SURFfederatie configuration.
   */
  public FederationProviderService(String configurationLocation) {
    this.configurationLocation = configurationLocation;
    loadConfiguration();
  }

  // configured in coin-selfservice-scheduler.xml
  @Override
  public void loadConfiguration() {
    LOG.debug("Loading configuration from {}", configurationLocation);
    Resource resource;
    try {
      resource = getConfigurationFileAsResource(configurationLocation);
    } catch (MalformedURLException e) {
      LOG.error("URL for SURFfederatie metadata '{}' is malformed. Fix it in coin-selfservice.properties. Error: {}",
          configurationLocation, e);
      return;
    }
    final XStream xStream = XStreamFedConfigBuilder.getXStreamForFedConfig(true);
    try {
      final FederatieConfig config = (FederatieConfig) xStream.fromXML(resource.getInputStream());
      writeUpdateReport(config);
      if (config != null) {
        federatieConfig = config;
        LOG.debug("Updated SURFfederatie config with content from {}", configurationLocation);
      }
    } catch (IOException e) {
      LOG.error("Could not retrieve SURFfederatie metadata from location '{}', message: {}",
          configurationLocation, e.getMessage());
    }
  }

  private Resource getConfigurationFileAsResource(String configurationFilename) throws MalformedURLException {
    Resource resource;
    if (configurationFilename.matches("^(http://|https://|ftp://|file://)(.+)")) {
      resource = new UrlResource(configurationFilename);
    } else {
      resource = new ClassPathResource(configurationFilename);
    }
    return resource;
  }

  private void writeUpdateReport(FederatieConfig config) {
    if (!LOG.isDebugEnabled()) {
      return;
    }
    if (config == null) {
      LOG.debug("Got empty SURFfederatie config file, will not update the existing configuration");
    } else {
      LOG.debug("Parsed SURFfederatie config. List of IdP's contains items: {}, list of SP's contains items: {}",
          CollectionUtils.isNotEmpty(config.getIdPs()),
          CollectionUtils.isNotEmpty(config.getSps()));
    }
  }

  private boolean isLinked(String idpId, ServiceProvider sp) {
    return sp.getAcl() != null && sp.getAcl().getIdpRefs() != null && sp.getAcl().getIdpRefs().contains(idpId);
  }

  @Override
  @Cacheable(value = {"sps-federation"})
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    List<ServiceProvider> providers = new ArrayList<ServiceProvider>();
    if (federatieConfig.getSps() == null) {
      return providers;
    }
    for (ServiceProvider sp : federatieConfig.getSps()) {
      sp.setLinked(isLinked(idpId, sp));
      providers.add(sp);
    }
    return providers;
  }

  @Override
  @Cacheable(value = {"sps-federation"})
  public List<ServiceProvider> getAllServiceProviders() {
    return federatieConfig.getSps();
  }

  @Override
  public List<ServiceProvider> getLinkedServiceProviders(String idpId) {
    List<ServiceProvider> linked = new ArrayList<ServiceProvider>();
    final List<ServiceProvider> allServiceProviders = getAllServiceProviders(idpId);
    if (CollectionUtils.isEmpty(allServiceProviders)) {
      return linked;
    }

    for (ServiceProvider sp : allServiceProviders) {
      if (sp.isLinked()) {
        linked.add(sp);
      }
    }
    return linked;
  }

  @Override
  @Cacheable(value = {"sps-federation"})
  public ServiceProvider getServiceProvider(String spEntityId, String idpEntityId) {
    if (federatieConfig.getSps() == null) {
      return null;
    }
    for (ServiceProvider sp : federatieConfig.getSps()) {
      if (sp.getId().equals(spEntityId)) {
        sp.setLinked(isLinked(idpEntityId, sp));
        return sp;
      }
    }
    return null;
  }

  @Override
  @Cacheable(value = {"sps-federation"})
  public IdentityProvider getIdentityProvider(String idpEntityId) {
    if (federatieConfig.getIdPs() == null) {
      return null;
    }
    for (IdentityProvider idp : federatieConfig.getIdPs()) {
      if (idp.getId().equals(idpEntityId)) {
        return idp;
      }
    }
    return null;
  }


  @Override
  @Cacheable(value = {"sps-federation"})
  public List<IdentityProvider> getInstituteIdentityProviders(String instituteId) {

    List<IdentityProvider> idps = new ArrayList<IdentityProvider>();
    if (federatieConfig.getIdPs() == null) {
      return idps;
    }

    for (IdentityProvider idp : federatieConfig.getIdPs()) {
      if (!StringUtils.isBlank(instituteId) && idp.getInstitutionId().equals(instituteId)) {
        idps.add(idp);
      }
    }
    return idps;
  }

  /* (non-Javadoc)
   * @see nl.surfnet.coin.selfservice.service.IdentityProviderService#getAllIdentityProviders()
   */
  @Override
  @Cacheable(value = {"sps-federation"})
  public List<IdentityProvider> getAllIdentityProviders() {
    return federatieConfig.getIdPs();

  }


}