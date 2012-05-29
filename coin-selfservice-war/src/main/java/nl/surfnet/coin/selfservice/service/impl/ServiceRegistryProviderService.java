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

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestClientException;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.domain.Contact;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.selfservice.domain.ContactPerson;
import nl.surfnet.coin.selfservice.domain.ContactPersonType;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

public class ServiceRegistryProviderService implements ServiceProviderService {

  private static final Logger log = LoggerFactory.getLogger(ServiceRegistryProviderService.class);


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
      final List<EntityMetadata> sps = janusClient.getSpList();
      for (EntityMetadata metadata : sps) {
        buildServiceProviderByMetadata(metadata);
        final ServiceProvider serviceProvider = getServiceProvider(metadata.getAppEntityId());
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
      EntityMetadata metadata= janusClient.getMetadataByEntityId(spEntityId);
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
  public static ServiceProvider buildServiceProviderByMetadata(EntityMetadata metadata) {
    ServiceProvider sp = new ServiceProvider(metadata.getAppEntityId(), metadata.getAppTitle());
    sp.setLogoUrl(metadata.getAppLogoUrl());
    sp.setHomeUrl(metadata.getAppHomeUrl());
    sp.setDescription(metadata.getAppDescription());

    for (Contact c : metadata.getContacts()) {
      ContactPerson p = new ContactPerson(StringUtils.join(new Object[]{c.getGivenName(), c.getSurName()}, " "),
          c.getEmailAddress());
      p.setContactPersonType(contactPersonTypeByJanusContactType(c.getType()));
      sp.addContactPerson(p);
    }
    return sp;
  }

  /**
   * Convert a Janus contact type to a ServiceProvider's ContactPersonType.
   *
   * @param contactType the Janus type
   * @return the {@link ContactPersonType}
   * @throws IllegalArgumentException in case no match can be made.
   */
  public static ContactPersonType contactPersonTypeByJanusContactType(Contact.Type contactType) {
    ContactPersonType t = null;
    if (contactType == Contact.Type.technical) {
      t = ContactPersonType.technical;
    } else if (contactType == Contact.Type.support) {
      t = ContactPersonType.help;
    } else if (contactType == Contact.Type.administrative) {
      t = ContactPersonType.administrative;
    } else if (contactType == Contact.Type.billing) {
      t = ContactPersonType.administrative;
    } else if (contactType == Contact.Type.other) {
      t = ContactPersonType.administrative;
    }
    if (t == null) {
      throw new IllegalArgumentException("Unknown Janus-contactType: " + contactType);
    }
    return t;
  }
}