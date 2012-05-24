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
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestClientException;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ProviderService;

public class ServiceRegistryProviderService implements ProviderService {

  private static final Logger log = LoggerFactory.getLogger(ServiceRegistryProviderService.class);

  private static final Janus.Metadata[] metadataToGet = new Janus.Metadata[]{
      Janus.Metadata.OAUTH_APPICON,
      Janus.Metadata.OAUTH_APPTITLE,
      Janus.Metadata.ORGANIZATION_NAME,
      Janus.Metadata.ORGANIZATION_URL,
      Janus.Metadata.LOGO_URL,
      Janus.Metadata.DISPLAYNAME,
      Janus.Metadata.NAME,
      Janus.Metadata.DESCRIPTION,

      /* Included because it's one of the properties that all
        entries have in Janus. This works around getting
        a 404 if none of the other properties are set.
      */
      Janus.Metadata.NAMEIDFORMAT
  };

  @Resource(name = "janusClient")
  private Janus janusClient;

  @Override
  @Cacheable(value = {"sps-janus"})
  public List<ServiceProvider> getLinkedServiceProviders(String idpId) {
    List<ServiceProvider> spList = new ArrayList<ServiceProvider>();
    try {
      final List<String> sps = janusClient.getAllowedSps(idpId);
      for (String spEntityId : sps) {
        final ServiceProvider serviceProvider = getServiceProvider(spEntityId);
        if (serviceProvider != null) {
          spList.add(serviceProvider);
        }
      }
    } catch (RestClientException e) {
      log.warn("Could not retrieve allowed SPs from Janus client", e.getMessage());
    }
    return spList;
  }

  @Override
  @Cacheable(value = {"sps-janus"})
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    List<ServiceProvider> spList = new ArrayList<ServiceProvider>();
    try {
      final Map<String, Map<String, String>> sps = janusClient.getSpList(metadataToGet);
      for (String spEntityId : sps.keySet()) {
        Map<String, String> metadata = sps.get(spEntityId);
        buildServiceProviderByMetadata(metadata);
        final ServiceProvider serviceProvider = getServiceProvider(spEntityId);
        if (serviceProvider != null) {
          spList.add(serviceProvider);
        }
      }
    } catch (RestClientException e) {
      log.warn("Could not retrieve allowed SPs from Janus client", e.getMessage());
    }
    return spList;
  }

  @Override
  @Cacheable(value = {"sps-janus"})
  public ServiceProvider getServiceProvider(String spEntityId) {
    try {
      Map<String, String> metadata = janusClient.getMetadataByEntityId(spEntityId, metadataToGet);
      return buildServiceProviderByMetadata(metadata);
    } catch (RestClientException e) {
      log.warn("Could not retrieve metadata from Janus client", e.getMessage());
    }
    return null;
  }

  /**
   * Create a ServiceProvider and inflate it with the given metadata attributes.
   * @param metadata Janus metadata
   * @return {@link ServiceProvider}
   */
  public static ServiceProvider buildServiceProviderByMetadata(Map<String, String> metadata) {
    ServiceProvider sp = new ServiceProvider((String) metadata.get(Janus.Metadata.ENTITY_ID.val()),
        (String) metadata.get(Janus.Metadata.DISPLAYNAME.val()));
    sp.setLogoUrl((String) metadata.get(Janus.Metadata.LOGO_URL.val()));
    sp.setHomeUrl((String) metadata.get(Janus.Metadata.ORGANIZATION_URL.val()));
    sp.setDescription((String) metadata.get(Janus.Metadata.DESCRIPTION.val()));

    return sp;
  }
}
