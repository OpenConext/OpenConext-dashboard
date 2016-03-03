package selfservice.serviceregistry;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.ServiceProvider;
import selfservice.util.StreamUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Function;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static selfservice.util.StreamUtils.filterEmpty;

public class ClassPathResourceServiceRegistry implements ServiceRegistry {

  protected final Logger LOG = LoggerFactory.getLogger(getClass());

  private Map<String, IdentityProvider> identityProviderMap = new HashMap<>();
  private Map<String, ServiceProvider> serviceProviderMap = new HashMap<>();

  private List<ServiceProvider> exampleSingleTenants = new ArrayList<>();

  private Resource singleTenantsConfigPath;

  private final static ObjectMapper objectMapper = new ObjectMapper();

  public ClassPathResourceServiceRegistry(boolean initialize, Resource singleTenantsConfigPath) {
    //this provides subclasses a hook to set properties before initializing metadata
    this.singleTenantsConfigPath = singleTenantsConfigPath;
    this.parseSingleTenants();
    if (initialize) {
      initializeMetadata();
    }
  }

  @Override
  public Optional<IdentityProvider> getIdentityProvider(String idpEntityId) {
    IdentityProvider identityProvider = identityProviderMap.get(idpEntityId);
    return identityProvider == null ? Optional.empty() : Optional.of(identityProvider);
  }

  @Override
  public List<IdentityProvider> getInstituteIdentityProviders(String instituteId) {
    return identityProviderMap.values().stream().filter(identityProvider -> instituteId.equals(identityProvider.getInstitutionId())).collect(toList());
  }

  @Override
  public List<IdentityProvider> getAllIdentityProviders() {
    return new ArrayList<>(identityProviderMap.values());
  }

  @Override
  public List<IdentityProvider> getLinkedIdentityProviders(String spId) {
    //We can't answer this question for single tenant sp as they are virtual
    if (this.exampleSingleTenants.stream().anyMatch(sp -> sp.getId().equals(spId))) {
      return new ArrayList<>();
    }
    ServiceProvider serviceProvider = getServiceProvider(spId);
    if (serviceProvider.isAllowedAll()) {
      return identityProviderMap.values().stream().filter(identityProvider ->
        identityProvider.isAllowedAll() || identityProvider.getAllowedEntityIds().contains(spId)).collect(toList());
    } else {
      return serviceProvider.getAllowedEntityIds().stream().map(this::getIdentityProvider).collect(filterEmpty());
    }
  }

  @Override
  public List<String> getLinkedServiceProviderIDs(String idpId) {
    Optional<IdentityProvider> optional = getIdentityProvider(idpId);
    if (!optional.isPresent()) {
      return Collections.emptyList();
    }
    IdentityProvider identityProvider = optional.get();
    if (identityProvider.isAllowedAll()) {
      return serviceProviderMap.values().stream().filter(sp ->
        sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idpId)).map(Provider::getId).collect(toList());
    } else {
      return new ArrayList<>(identityProvider.getAllowedEntityIds());
    }
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders(String idpId) {
    Collection<ServiceProvider> allSPs = serviceProviderMap.values();

    List<String> myLinkedSPs = getLinkedServiceProviderIDs(idpId);

    List<ServiceProvider> filteredList = new ArrayList<>();
    for (ServiceProvider sp : allSPs) {
      if (myLinkedSPs.contains(sp.getId())) {
        // an already linked SP is visible
        ServiceProvider clone = sp.clone();
        clone.setLinked(true);
        filteredList.add(clone);
      } else if (!sp.isIdpVisibleOnly()) {
        // Not-linked sps are only visible if 'idp visible only' is not true.
        filteredList.add(sp);
      }
    }
    filteredList.addAll(exampleSingleTenants);
    return filteredList;
  }

  @Override
  public ServiceProvider getServiceProvider(String spEntityId, String idpEntityId) {
    Optional<ServiceProvider> optional = getSingleTenant(spEntityId);
    if (optional.isPresent()) {
      return optional.get();
    }
    ServiceProvider serviceProvider = serviceProviderMap.get(spEntityId);

    // Check if the IdP can connect to this service
    if (idpEntityId != null) {
      IdentityProvider identityProvider = identityProviderMap.get(idpEntityId);
      if (isConnectionAllowed(serviceProvider, identityProvider)) {
        ServiceProvider clone = serviceProvider.clone();
        clone.setLinked(true);
        return clone;
      }
    }
    return serviceProvider;
  }

  private Optional<ServiceProvider> getSingleTenant(String spEntityId) {
    return exampleSingleTenants.stream().filter(sp -> sp.getId().equals(spEntityId)).collect(StreamUtils.singletonOptionalCollector());
  }

  @Override
  public ServiceProvider getServiceProvider(String spEntityId) {
    Optional<ServiceProvider> optional = getSingleTenant(spEntityId);
    if (optional.isPresent()) {
      return optional.get();
    }
    return serviceProviderMap.get(spEntityId);
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders() {
    ArrayList<ServiceProvider> serviceProviders = new ArrayList<>(serviceProviderMap.values());
    serviceProviders.addAll(exampleSingleTenants);
    return serviceProviders;
  }

  @Override
  public void refreshMetaData() {
    this.exampleSingleTenants = new ArrayList<>();
    parseSingleTenants();
    initializeMetadata();
  }

  protected void initializeMetadata() {
    try {
      identityProviderMap = parseProviders(getIdpResource(), this::identityProvider);
      serviceProviderMap = parseProviders(getSpResource(), this::serviceProvider);
      LOG.debug("Initialized SR Resources. Number of IDPs {}. Number of SPs {}", identityProviderMap.size(), serviceProviderMap.size());
    } catch (RuntimeException | IOException e) {
      /*
       * By design we catch the error and not rethrow it.
       * UrlResourceServiceRegistry has timing issues when the server reboots and required endpoints are not available yet.
       * ClassPathResourceServiceRegistry is only used in dev mode and any logged errors will end up in Rollbar
       */
      LOG.error("Error in refreshing / initializing metadata", e);
    }
  }

  protected Resource getIdpResource() {
    return new ClassPathResource("service-registry/identity-providers.json");
  }

  protected Resource getSpResource() {
    return new ClassPathResource("service-registry/service-providers.json");
  }


  private void parseSingleTenants() {
    try {
      File[] dummySps = singleTenantsConfigPath.getFile().listFiles((dir, name) -> name.endsWith("json"));
      this.exampleSingleTenants = Arrays.stream(dummySps).map(this::parse).collect(toList());
      this.exampleSingleTenants.forEach(sp -> sp.setExampleSingleTenant(true));
      LOG.info("Read {} example single tenant services from {}", exampleSingleTenants.size(), singleTenantsConfigPath.getFilename());
    } catch (Exception e) {
      throw new RuntimeException("Unable to read example single tenants services", e);
    }
  }

  private ServiceProvider parse(File file) {
    try {
      return serviceProvider(objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {
      }));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private ServiceProvider serviceProvider(Map<String, Object> map) {
    return new ServiceProvider(map);
  }

  private IdentityProvider identityProvider(Map<String, Object> map) {
    return new IdentityProvider(map);
  }

  private <T extends Provider> Map<String, T> parseProviders(Resource resource, Function<Map<String, Object>, T> provider) throws IOException {
    List<Map<String, Object>> providers = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Map<String, Object>>>() {
    });
    return providers.stream().map(provider).collect(toMap(Provider::getId, prov -> prov));
  }

  private boolean isConnectionAllowed(ServiceProvider sp, IdentityProvider idp) {
    return (sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idp.getId())) &&
      (idp.isAllowedAll() || idp.getAllowedEntityIds().contains(sp.getId()));
  }

}
