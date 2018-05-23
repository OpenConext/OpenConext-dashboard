package selfservice.manage;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;
import static selfservice.util.StreamUtils.filterEmpty;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.ServiceProvider;

@SuppressWarnings("unchecked")
public class ClassPathResourceManage implements Manage {

  protected final Logger LOG = LoggerFactory.getLogger(getClass());

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private volatile Map<String, IdentityProvider> identityProviderMap = new HashMap<>();
  private volatile Map<String, ServiceProvider> serviceProviderMap = new HashMap<>();
  private volatile Map<String, ServiceProvider> exampleSingleTenants = new HashMap<>();

  public ClassPathResourceManage(boolean initialize) {
    if (initialize) {
      initializeMetadata();
    }
  }

  @Override
  public Optional<IdentityProvider> getIdentityProvider(String idpEntityId) {
    return Optional.ofNullable(identityProviderMap.get(idpEntityId));
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
    if (this.exampleSingleTenants.containsKey(spId)) {
      return new ArrayList<>();
    }
    ServiceProvider serviceProvider = getServiceProvider(spId).orElseThrow(RuntimeException::new);
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
    filteredList.addAll(exampleSingleTenants.values());
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
    return Optional.ofNullable(exampleSingleTenants.get(spEntityId));
  }

  @Override
  public Optional<ServiceProvider> getServiceProvider(String spEntityId) {
    return Optional.ofNullable(getSingleTenant(spEntityId).orElseGet(() -> serviceProviderMap.get(spEntityId)));
  }

  @Override
  public List<ServiceProvider> getAllServiceProviders() {
    ArrayList<ServiceProvider> serviceProviders = new ArrayList<>(serviceProviderMap.values());
    serviceProviders.addAll(exampleSingleTenants.values());
    return serviceProviders;
  }

  @Override
  public void refreshMetaData() {
    initializeMetadata();
  }

  protected void initializeMetadata() {
    try {
      identityProviderMap = parseProviders(getIdpResource(), this::identityProvider);
      serviceProviderMap = parseProviders(getSpResource(), this::serviceProvider);
      exampleSingleTenants = parseProviders(getSingleTenantResource(), this::serviceProvider);
      exampleSingleTenants.values().forEach(singleTenant -> singleTenant.setExampleSingleTenant(true));
      LOG.debug("Initialized SR Resources. Number of IDPs {}. Number of SPs {}", identityProviderMap.size(), serviceProviderMap.size());
    } catch (Throwable e) {
      /*
       * By design we catch the error and not rethrow it as this would cancel future scheduling
       */
      LOG.error("Error in refreshing / initializing metadata", e);
    }
  }

  protected Resource getIdpResource() throws UnsupportedEncodingException {
    return new ClassPathResource("manage/identity-providers.json");
  }

  protected Resource getSpResource() throws UnsupportedEncodingException {
    return new ClassPathResource("manage/service-providers.json");
  }

  protected Resource getSingleTenantResource() throws UnsupportedEncodingException {
    return new ClassPathResource("manage/single-tenants.json");
  }

  private ServiceProvider parse(File file) {
    try {
      return serviceProvider(objectMapper.readValue(file, new TypeReference<Map<String, Object>>() {}));
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
    List<Map<String, Object>> providers = objectMapper.readValue(resource.getInputStream(), new TypeReference<List<Map<String, Object>>>() { });

    Map<String, T> result = providers.stream()
      .filter(stringObjectMap -> Map.class.cast(stringObjectMap.get("data")).get("state").equals("prodaccepted"))
      .map(this::transformManageMetadata).map(provider).collect(toSet()).stream().collect(toMap(Provider::getId, identity()));
    return result;
  }

  private boolean isConnectionAllowed(ServiceProvider sp, IdentityProvider idp) {
    return (sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idp.getId())) &&
      (idp.isAllowedAll() || idp.getAllowedEntityIds().contains(sp.getId()));
  }

  private Map<String, Object > transformManageMetadata(Map<String, Object> metadata) {
    Map<String, Object> data = (Map<String, Object>) metadata.get("data");
    Map<String, Object> result = new HashMap<>();
    data.entrySet().forEach(entry -> {
      String key = entry.getKey();
      Object value = entry.getValue();
      if (value instanceof Boolean) {
        result.put(key, Boolean.class.cast(value) ? "yes" : "no");
      } else if (value instanceof String) {
        result.put(key, value);
      } else if (value instanceof Number) {
        result.put(key, value);
      }
      switch (key) {
        case "metaDataFields" : {
          Map<String, Object> metaDataFields = (Map<String, Object>) value;
          result.putAll(metaDataFields);
          break;
        }
        case "arp" : {
          Map<String, Object> arp = (Map<String, Object>) value;
          Boolean enabled = (Boolean) arp.get("enabled");
          if (enabled) {
            //Map<String, List<String>>
            Map<String, List<Map<String, String>>> attributes = (Map<String, List<Map<String, String>>>) arp.get("attributes");
            Map<String, List<String>> attributesList = attributes.entrySet().stream()
              .collect(toMap(e -> e.getKey(), e -> Collections.singletonList(e.getValue().get(0).get("value"))));
            result.put("attributes", attributesList);
          }
          break;
        }
        case  "allowedEntities" : {
          List<Map<String, String>> allowedEntities = (List<Map<String, String>>) value;
          List<String> allowedEntitiesList = allowedEntities.stream().map(m -> m.get("name")).collect(toList());
          result.put("allowedEntities", allowedEntitiesList);
          break;
        }
      }

    });
    return result;
  }

}
