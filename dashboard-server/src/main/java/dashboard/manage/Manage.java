package dashboard.manage;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import dashboard.domain.*;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

public interface Manage {

    ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Get a list of all available Service Providers
     *
     * @return list of {@link ServiceProvider}
     */
    List<ServiceProvider> getAllServiceProviders();

    /**
     * Get a {@link ServiceProvider} by its entity ID, without a idpEntityId
     *
     * @param spEntityId      the entity id of the ServiceProvider
     * @param searchRevisions
     * @return the {@link ServiceProvider} object.
     */
    Optional<ServiceProvider> getServiceProvider(String spEntityId, EntityType type, boolean searchRevisions);

    Optional<ServiceProvider> getServiceProviderById(Long spId, EntityType entityType);

    /**
     * Get an identity provider by its id.
     *
     * @param idpEntityId     the id.
     * @param searchRevisions
     * @return IdentityProvider
     */
    Optional<IdentityProvider> getIdentityProvider(String idpEntityId, boolean searchRevisions);

    /**
     * Get a list of all idps that have the same instituteId as the given one.
     *
     * @param instituteId the instituteId
     * @return List&lt;IdentityProvider&gt;
     */
    List<IdentityProvider> getInstituteIdentityProviders(String instituteId);


    /**
     * Get a list of all ServiceProviders that have the same instituteId as the given one.
     *
     * @param instituteId the instituteId
     * @return List&lt;ServiceProvider&gt;
     */
    List<ServiceProvider> getInstitutionalServicesForIdp(String instituteId);

    /**
     * Get a list of all idps
     *
     * @return List&lt;IdentityProvider&gt;
     */
    List<IdentityProvider> getAllIdentityProviders();

    /**
     * Get a list of all idps connected to a SP
     *
     * @return List&lt;IdentityProvider&gt;
     */
    List<IdentityProvider> getLinkedIdentityProviders(String spId);

    List<ServiceProvider> getLinkedServiceProviders(String idpId);

    List<ServiceProvider> getByEntityIdin(List<String> entityIds);

    default ServiceProvider serviceProvider(Map<String, Object> map, EntityType entityType) {
        ServiceProvider serviceProvider = new ServiceProvider(map);
        serviceProvider.setEntityType(entityType);
        return serviceProvider;
    }

    default IdentityProvider identityProvider(Map<String, Object> map) {
        return new IdentityProvider(map);
    }

    default <T extends Provider> Map<String, T> parseProviders(Resource resource, Function<Map<String, Object>, T>
            provider) throws IOException {
        List<Map<String, Object>> providers = objectMapper.readValue(resource.getInputStream(), new
                TypeReference<List<Map<String, Object>>>() {
                });

        Map<String, T> result = providers.stream()
                .map(this::transformManageMetadata).map(provider).collect(toSet()).stream().collect(toMap(Provider::getId,
                        identity()));
        return result;
    }

    default Map<String, Object> transformManageMetadata(Map<String, Object> metadata) {
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
                case "metaDataFields": {
                    Map<String, Object> metaDataFields = (Map<String, Object>) value;
                    result.putAll(metaDataFields);
                    break;
                }
                case "arp": {
                    Map<String, Object> arp = (Map<String, Object>) value;
                    Boolean enabled = (Boolean) arp.get("enabled");
                    if (enabled) {
                        Map<String, List<Map<String, String>>> attributes =
                                (Map<String, List<Map<String, String>>>) arp.get("attributes");
                        Map<String, List<String>> attributesMap = attributes.entrySet().stream()
                                .collect(toMap(
                                        e -> e.getKey(),
                                        e -> e.getValue().stream().map(m -> m.get("value")).collect(toList())));
                        Map<String, String> motivationsMap = attributes.entrySet().stream()
                                .collect(toMap(
                                        e -> e.getKey(),
                                        e -> e.getValue().get(0).getOrDefault("motivation", "")));
                        result.put("attributes", attributesMap);
                        result.put("motivations", motivationsMap);
                    }
                    break;
                }
                case "allowedEntities": {
                    List<Map<String, String>> allowedEntities = (List<Map<String, String>>) value;
                    List<String> allowedEntitiesList = allowedEntities.stream().map(m -> m.get("name")).collect(toList());
                    result.put("allowedEntities", allowedEntitiesList);
                    break;
                }
                case "disableConsent": {
                    List<Map<String, String>> disableConsent = (List<Map<String, String>>) value;
                    result.put("disableConsent", disableConsent.stream().map(m ->
                            new Consent(m.get("name"),
                                    m.containsKey("type") ? ConsentType.valueOf(m.get("type").toUpperCase()) : ConsentType.DEFAULT_CONSENT,
                                    m.get("explanation:nl"),
                                    m.get("explanation:en"))).collect(Collectors.toList()));
                    break;

                }
                case "stepupEntities": {
                    List<Map<String, String>> stepupEntities = (List<Map<String, String>>) value;
                    result.put("stepupEntities", stepupEntities);
                    break;
                }
                case "allowedResourceServers": {
                    List<Map<String, String>> allowedResourceServers = (List<Map<String, String>>) value;
                    result.put("allowedResourceServers", allowedResourceServers.stream().map(rs -> rs.get("name")).collect(toList()));
                    break;
                }
            }

        });
        return result;
    }

    String connectWithoutInteraction(String username, String idpId, String spId, String type);
}
