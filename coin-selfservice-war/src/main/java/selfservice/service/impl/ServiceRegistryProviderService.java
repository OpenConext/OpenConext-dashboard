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

import selfservice.domain.ARP;
import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;
import selfservice.domain.csa.IdentityProvider;
import selfservice.domain.csa.ServiceProvider;
import selfservice.janus.Janus;
import selfservice.janus.domain.Contact;
import selfservice.janus.domain.EntityMetadata;
import selfservice.janus.domain.JanusEntity;
import selfservice.service.IdentityProviderService;
import selfservice.service.ServiceProviderService;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClientException;

import static com.google.common.base.Strings.emptyToNull;
import static java.util.stream.Collectors.toList;
import static org.springframework.util.Assert.notNull;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;

@Service
public class ServiceRegistryProviderService implements ServiceProviderService, IdentityProviderService, ApplicationListener<ContextRefreshedEvent> {

  private static final Logger LOG = LoggerFactory.getLogger(ServiceRegistryProviderService.class);
  private static final String IN_PRODUCTION = "prodaccepted";

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<ServiceProvider> exampleSingleTenants = new ArrayList<>();

  @Autowired
  private Janus janusClient;

  @Autowired
  private ResourceLoader resourceLoader;

  @Value("${singleTenants.config.path}")
  private String singleTenantsConfigPath;

  @Override
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    List<ServiceProvider> allSPs = getAllServiceProvidersUnfiltered(false, 0);

    List<String> myLinkedSPs = getLinkedServiceProviderIDs(idpId);

    List<ServiceProvider> filteredList = new ArrayList<>();
    for (ServiceProvider sp : allSPs) {
      if (myLinkedSPs.contains(sp.getId())) {
        // an already linked SP is visible
        sp.setLinked(true);
        filteredList.add(sp);
      } else if (!sp.isIdpVisibleOnly()) {
        // Not-linked sps are only visible if 'idp visible only' is not true.
        filteredList.add(sp);
      }
    }
    return filteredList;
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders(boolean includeArps) {
    return getAllServiceProvidersUnfiltered(includeArps, 0);
  }

  @Override
  public List<ServiceProvider> getAllServiceProvidersRateLimited(long rateDelay) {
    return getAllServiceProvidersUnfiltered(true, rateDelay);
  }

  @Override
  public void refreshExampleSingleTenants() {
    this.onApplicationEvent(null);
  }

  @Override
  public List<String> getLinkedServiceProviderIDs(String idpId) {
    try {
      return janusClient.getAllowedSps(idpId);
    } catch (RestClientException e) {
      LOG.error("Could not retrieve allowed SPs from Janus client", e);
      return Collections.emptyList();
    }
  }

  private List<ServiceProvider> getAllServiceProvidersUnfiltered(boolean includeArps, long callDelay) {
    List<ServiceProvider> spList = new ArrayList<>();
    try {
      final List<EntityMetadata> sps = janusClient.getSpList();
      for (EntityMetadata metadata : sps) {
        if (StringUtils.equals(metadata.getWorkflowState(), IN_PRODUCTION)) {
          spList.add(buildServiceProviderByMetadata(metadata, includeArps));
          if (callDelay > 0) {
            try {
              Thread.sleep(callDelay);
            } catch (InterruptedException e) {
              break;
            }
          }
        }
      }
    } catch (RestClientException e) {
      LOG.error("Could not retrieve 'all SPs' from Janus client", e);
    }
    spList.addAll(exampleSingleTenants);
    return spList;
  }

  @Override
  public ServiceProvider getServiceProvider(String spEntityId, String idpEntityId) {
    List<ServiceProvider> sps = exampleSingleTenants.stream().filter(sp -> sp.getId().equals(spEntityId)).collect(Collectors.toList());
    if (!sps.isEmpty()) {
      return sps.get(0);
    }
    try {
      // first get JanusEntity. This holds the information about the workflow
      // only allow production status
      final JanusEntity entity = janusClient.getEntity(spEntityId);
      if (entity == null || !(IN_PRODUCTION.equals(entity.getWorkflowStatus()))) {
        return null;
      }
      // Get the metadata and build a ServiceProvider with this metadata
      EntityMetadata metadata = janusClient.getMetadataByEntityId(spEntityId);
      final ServiceProvider serviceProvider = buildServiceProviderByMetadata(metadata, true);

      // Check if the IdP can connect to this service
      if (idpEntityId != null) {
        final boolean linked = janusClient.isConnectionAllowed(spEntityId, idpEntityId);
        serviceProvider.setLinked(linked);
      }
      return serviceProvider;
    } catch (RestClientException e) {
      LOG.error("Could not retrieve metadata from Janus client", e);
    }
    return null;
  }

  @Override
  public ServiceProvider getServiceProvider(String spEntityId) {
    return getServiceProvider(spEntityId, null);
  }

  /**
   * Create a ServiceProvider and inflate it with the given metadata attributes.
   *
   * @param metadata Janus metadata
   * @return {@link ServiceProvider}
   */
  public ServiceProvider buildServiceProviderByMetadata(EntityMetadata metadata, boolean includeArps) {
    Assert.notNull(metadata, "metadata cannot be null");
    final String appEntityId = metadata.getAppEntityId();
    // Get the ARP (if there is any)
    return doBuildServiceProviderByMetadata(metadata, appEntityId, includeArps ? Optional.of(janusClient.getArp(appEntityId)) : Optional.empty());
  }

  private ServiceProvider doBuildServiceProviderByMetadata(EntityMetadata metadata, String appEntityId, Optional<ARP> arp) {
    String name = metadata.getNames().get("en");
    if (StringUtils.isBlank(name)) {
      name = appEntityId;
    }
    ServiceProvider sp = new ServiceProvider(appEntityId);
    // this is needed for sorting
    sp.setName(name);
    sp.setNames(metadata.getNames());
    sp.setLogoUrl(metadata.getAppLogoUrl());
    sp.setHomeUrls(metadata.getAppHomeUrls());
    sp.setDescriptions(metadata.getDescriptions());
    sp.setIdpVisibleOnly(metadata.isIdpVisibleOnly());
    sp.setEulaURL(metadata.getEula());
    sp.setUrls(metadata.getUrls());
    sp.setApplicationUrl(metadata.getApplicationUrl());
    sp.setGadgetBaseUrl(metadata.getOauthConsumerKey());
    sp.setInstitutionId(metadata.getInstutionId());
    sp.setPublishedInEdugain(metadata.isPublishedInEduGain());
    for (Contact c : metadata.getContacts()) {
      ContactPerson p = new ContactPerson(StringUtils.join(new Object[]{c.getGivenName(), c.getSurName()}, " "), c.getEmailAddress());
      p.setContactPersonType(contactPersonTypeByJanusContactType(c.getType()));
      p.setTelephoneNumber(c.getTelephoneNumber());
      sp.addContactPerson(p);
    }
    if (arp.isPresent()) {
      sp.setArp(arp.get());
    }
    return sp;
  }

  /**
   * Create a IdentityProvider and inflate it with the given metadata attributes.
   *
   * @param metadata Janus metadata
   * @return {@link IdentityProvider}
   */
  public static IdentityProvider buildIdentityProviderByMetadata(EntityMetadata metadata) {
    notNull(metadata, "metadata cannot be null");

    String appEntityId = metadata.getAppEntityId();
    String name = Optional.ofNullable(emptyToNull(metadata.getNames().get("en"))).orElse(appEntityId);
    IdentityProvider idp = new IdentityProvider(appEntityId, metadata.getInstutionId(), name);
    // this is needed for sorting
    idp.setName(name);
    idp.setNames(metadata.getNames());
    idp.setLogoUrl(metadata.getAppLogoUrl());
    idp.setHomeUrls(metadata.getAppHomeUrls());
    idp.setDescriptions(metadata.getDescriptions());

    metadata.getContacts().stream().map(c -> {
      ContactPerson p = new ContactPerson(Joiner.on(' ').join(c.getGivenName(), c.getSurName()), c.getEmailAddress());
      p.setContactPersonType(contactPersonTypeByJanusContactType(c.getType()));
      p.setTelephoneNumber(c.getTelephoneNumber());
      return p;
    }).forEach(idp::addContactPerson);

    return idp;
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

  @Override
  public Optional<IdentityProvider> getIdentityProvider(String idpEntityId) {
    try {
      return Optional.of(buildIdentityProviderByMetadata(janusClient.getMetadataByEntityId(idpEntityId)));
    } catch (Exception e) {
      LOG.error("Unable to getIdentityProvider " + idpEntityId, e);
      return Optional.empty();
    }
  }

  @Override
  public List<IdentityProvider> getInstituteIdentityProviders(final String instituteId) {
    if (StringUtils.isBlank(instituteId)) {
      Collections.emptyList();
    }

    return getAllIdentityProviders().stream().filter(idp -> instituteId.equals(idp.getInstitutionId())).collect(toList());
  }

  @Override
  public List<IdentityProvider> getAllIdentityProviders() {
    try {
      return janusClient.getIdpList().stream()
          .filter(metadata -> StringUtils.equals(metadata.getWorkflowState(), IN_PRODUCTION))
          .map(ServiceRegistryProviderService::buildIdentityProviderByMetadata).collect(toList());
    } catch (RestClientException e) {
      LOG.warn("Could not retrieve 'all IdPs' from Janus client", e);
      return Collections.emptyList();
    }
  }

  @Override
  public List<IdentityProvider> getLinkedIdentityProviders(String spId) {
    //We can't answer this question for single tenant sp as they are virtual
    if (this.exampleSingleTenants.stream().anyMatch(sp -> sp.getId().equals(spId))) {
      return new ArrayList<>();
    }
    List<String> allowedIdps = janusClient.getAllowedIdps(spId);
    return this.getAllIdentityProviders().stream().filter(idp -> allowedIdps.contains(idp.getId())).collect(Collectors.toList());
  }

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    Resource resource = resourceLoader.getResource(singleTenantsConfigPath);
    try {
      File[] dummySps = resource.getFile().listFiles((dir, name) -> name.endsWith("json"));
      this.exampleSingleTenants = Arrays.stream(dummySps).map(this::parse).collect(toList());
      this.exampleSingleTenants.forEach(sp -> sp.setExampleSingleTenant(true));
      LOG.info("Read {} example single tenant services from {}", exampleSingleTenants.size(), resource.getFilename());
    } catch (Exception e) {
      throw new RuntimeException("Unable to read example single tenants services", e);
    }
  }

  private ServiceProvider parse(File file) {
    try {
      Map<String, Object> map = objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {});
      EntityMetadata metadata = EntityMetadata.fromMetadataMap(map);
      @SuppressWarnings("unchecked")
      ARP arp = map.containsKey("attributes") ? ARP.fromAttributes((List<String>) map.get("attributes")) : ARP.fromRestResponse(new HashMap<>());
      return doBuildServiceProviderByMetadata(metadata, (String) map.get("entityid"),Optional.of(arp));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public void setJanusClient(Janus janusClient) {
    this.janusClient = janusClient;
  }

  public void setSingleTenantsConfigPath(String singleTenantsConfigPath) {
    this.singleTenantsConfigPath = singleTenantsConfigPath;
  }
}
