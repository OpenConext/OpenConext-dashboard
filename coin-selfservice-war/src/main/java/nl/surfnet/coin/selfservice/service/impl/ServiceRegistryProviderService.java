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

import org.springframework.cache.annotation.Cacheable;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.selfservice.domain.Provider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ProviderService;

public class ServiceRegistryProviderService implements ProviderService {

  private static final Janus.Metadata[] metadataToGet = new Janus.Metadata[] {
    Janus.Metadata.OAUTH_APPICON,
    Janus.Metadata.OAUTH_APPTITLE,
    Janus.Metadata.ORGANIZATION_NAME,
    Janus.Metadata.ORGANIZATION_URL,
    Janus.Metadata.LOGO_URL,

    /* Included because it's one of the properties that all
      entries have in Janus. This works around getting
      a 404 if none of the other properties are set.
    */
    Janus.Metadata.NAMEIDFORMAT
  };

  @Resource(name="janusClient")
  private Janus janusClient;

  @Override
  @Cacheable(value = { "sps-janus" })
  public List<Provider> getLinkedServiceProviders(String idpId) {
    final List<String> sps = janusClient.getAllowedSps(idpId);
    List<Provider> spList = new ArrayList<Provider>();
    for (String spEntityId : sps) {
      spList.add(getServiceProvider(spEntityId));
    }
    return spList;
  }

  @Override
  @Cacheable(value = { "sps-janus" })
  public ServiceProvider getServiceProvider(String spEntityId) {
      final Map<String,String> metadata = janusClient.getMetadataByEntityId(spEntityId, metadataToGet);
    return buildServiceProviderByMetadata(metadata);
  }

  public static ServiceProvider buildServiceProviderByMetadata(Map<String, String> metadata) {
    ServiceProvider sp = new ServiceProvider(metadata.get(Janus.Metadata.ENTITY_ID.val()),
        metadata.get(Janus.Metadata.OAUTH_APPTITLE.val()));
    sp.setHomeUrl(metadata.get(Janus.Metadata.LOGO_URL.val()));
    return sp;
  }
}
