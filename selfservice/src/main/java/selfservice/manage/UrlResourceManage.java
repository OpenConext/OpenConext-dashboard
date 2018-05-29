package selfservice.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UrlResourceManage implements Manage {
    private final static Logger LOG = LoggerFactory.getLogger(ClassPathResourceManage.class);

    private final String manageBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders;

    private String requestedAttributes = "\"state\":\"prodaccepted\",\"ALL_ATTRIBUTES\":true";
    private String body = "{\"" + requestedAttributes + "}";
    private String bodyForEntity = "{\"entityid\":\"@@entityid@@\", \"" + requestedAttributes + "}";
    private String bodyForEntityIdIn = "{\"entityid\":[@@entityids@@], \"" + requestedAttributes + "}";
    private String bodyForInstitutionId = "{\"metaDataFields.coin:institution_id\":\"@@institution_id@@\", \"" +
        requestedAttributes + "}";
    private String bodyForLinkedIdps = "{\"allowedall\":true,\"allowedEntities.name\":[\"@@entityid@\"], " +
        "\"REQUESTED_ATTRIBUTES\": [\"metaDataFields.coin:institution_id\", \"allowedall\", \"allowedEntities\"], " +
        "\"LOGICAL_OPERATOR_IS_AND\": false }";

    public UrlResourceManage(
        String username,
        String password,
        String manageBaseUrl) {
        String basicAuth = "Basic " + new String(Base64.getEncoder().encode((username + ":" + password).getBytes()));
        this.manageBaseUrl = manageBaseUrl;

        this.httpHeaders = new HttpHeaders();
        this.httpHeaders.add(HttpHeaders.CONTENT_TYPE, "application/json");
        this.httpHeaders.add(HttpHeaders.AUTHORIZATION, basicAuth);

        SimpleClientHttpRequestFactory requestFactory = (SimpleClientHttpRequestFactory) restTemplate
            .getRequestFactory();
        requestFactory.setConnectTimeout(10 * 1000);
    }

    @Override
    public List<ServiceProvider> getAllServiceProviders(String idpId) {
        InputStream inputStream = getSpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::serviceProvider)
            .collect(Collectors.toList());
    }

    @Override
    public Optional<ServiceProvider> getServiceProvider(String spEntityId, EntityType type) {
        String body = bodyForEntity.replace("@@entityid@@", spEntityId);
        InputStream inputStream = type.equals(EntityType.saml20_sp) ? getSpInputStream(body) :
            getSingleTenantInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::serviceProvider).findFirst();
    }

    @Override
    public Optional<IdentityProvider> getIdentityProvider(String idpEntityId) {
        String body = bodyForEntity.replace("@@entityid@@", idpEntityId);
        InputStream inputStream = getIdpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider).findFirst();
    }

    @Override
    public List<IdentityProvider> getInstituteIdentityProviders(String instituteId) {
        String body = bodyForInstitutionId.replace("@@institution_id@@", instituteId);
        InputStream inputStream = getSpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider)
            .collect(Collectors.toList());
    }

    @Override
    public List<IdentityProvider> getAllIdentityProviders() {
        InputStream inputStream = getSpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider)
            .collect(Collectors.toList());
    }

    @Override
    public List<IdentityProvider> getLinkedIdentityProviders(String spId) {
        return null;
    }

    @Override
    public List<String> getLinkedServiceProviderIDs(String idpId) {
        return null;
    }

    private List<Map<String, Object>> getMaps(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, List.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getIdpInputStream(String body) {
        LOG.debug("Fetching IDP metadata entries from {} with body {}", manageBaseUrl);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange
            (manageBaseUrl + "/manage/api/internal/search/saml20_idp", HttpMethod.POST,
                new HttpEntity<>(body, this.httpHeaders), byte[].class);
        return new BufferedInputStream(new ByteArrayInputStream(responseEntity.getBody()));
    }

    private InputStream getSpInputStream(String body) {
        LOG.debug("Fetching SP metadata entries from {} with body {}", manageBaseUrl, body);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange
            (manageBaseUrl + "/manage/api/internal/search/saml20_sp", HttpMethod.POST,
                new HttpEntity<>(body, this.httpHeaders), byte[].class);
        return new BufferedInputStream(new ByteArrayInputStream(responseEntity.getBody()));
    }

    private InputStream getSingleTenantInputStream(String body) {
        LOG.debug("Fetching Single Tenant Templates metadata entries from {} with body {}", manageBaseUrl, body);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange
            (manageBaseUrl + "/manage/api/internal/search/single_tenant_template", HttpMethod.POST,
                new HttpEntity<>(body, this.httpHeaders), byte[].class);
        return new BufferedInputStream(new ByteArrayInputStream(responseEntity.getBody()));
    }

}
