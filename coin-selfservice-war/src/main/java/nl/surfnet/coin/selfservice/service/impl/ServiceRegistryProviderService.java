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

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.client.RestClientException;

import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.selfservice.domain.ContactPerson;
import nl.surfnet.coin.selfservice.domain.ContactPersonType;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

public class ServiceRegistryProviderService implements ServiceProviderService {

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
      Janus.Metadata.CONTACTS_0_TYPE,
      Janus.Metadata.CONTACTS_0_EMAIL,
      Janus.Metadata.CONTACTS_0_GIVENNAME,
      Janus.Metadata.CONTACTS_0_SURNAME,
      Janus.Metadata.CONTACTS_1_TYPE,
      Janus.Metadata.CONTACTS_1_EMAIL,
      Janus.Metadata.CONTACTS_1_GIVENNAME,
      Janus.Metadata.CONTACTS_1_SURNAME,

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

    if (!StringUtils.isBlank(metadata.get(Janus.Metadata.CONTACTS_0_TYPE.val()))) {
      String name = StringUtils.join(new String[] {metadata.get(Janus.Metadata.CONTACTS_0_GIVENNAME.val()),
          metadata.get(Janus.Metadata.CONTACTS_0_SURNAME.val())}, " ");
      final ContactPerson contactPerson = new ContactPerson(name, metadata.get(Janus.Metadata.CONTACTS_0_EMAIL.val()));
      contactPerson.setContactPersonType(contactPersonTypeByJanusContactType(metadata.get(
          Janus.Metadata.CONTACTS_0_TYPE.val())));
      sp.addContactPerson(contactPerson);
    }
    if (!StringUtils.isBlank(metadata.get(Janus.Metadata.CONTACTS_1_TYPE.val()))) {
      String name = StringUtils.join(new String[] {metadata.get(Janus.Metadata.CONTACTS_1_GIVENNAME.val()),
          metadata.get(Janus.Metadata.CONTACTS_1_SURNAME.val())}, " ");
      final ContactPerson contactPerson = new ContactPerson(name, metadata.get(Janus.Metadata.CONTACTS_1_EMAIL.val()));
      contactPerson.setContactPersonType(contactPersonTypeByJanusContactType(metadata.get(
          Janus.Metadata.CONTACTS_1_TYPE.val())));
      sp.addContactPerson(contactPerson);
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
  public static ContactPersonType contactPersonTypeByJanusContactType(String contactType) {
    ContactPersonType t = null;
    if (contactType.equalsIgnoreCase("technical")) {
      t = ContactPersonType.technical;
    } else if (contactType.equalsIgnoreCase("support")) {
      t = ContactPersonType.help;
    } else if (contactType.equalsIgnoreCase("administrative")) {
      t = ContactPersonType.administrative;
    } else if (contactType.equalsIgnoreCase("billing")) {
      t = ContactPersonType.administrative;
    } else if (contactType.equalsIgnoreCase("other")) {
      t = ContactPersonType.administrative;
    }
    if (t == null) {
      throw new IllegalArgumentException("Unknown Janus-contactType: " + contactType);
    }
    return t;
  }
}