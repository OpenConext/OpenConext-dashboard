package dashboard.manage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import dashboard.domain.IdentityProvider;
import dashboard.domain.ServiceProvider;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class UrlResourceManage implements Manage {
    private final static Logger LOG = LoggerFactory.getLogger(UrlResourceManage.class);

    private final String manageBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();
    private final HttpHeaders httpHeaders;

    private String requestedAttributes = "\"state\":\"prodaccepted\",\"ALL_ATTRIBUTES\":true";
    private String body = "{" + requestedAttributes + "}";
    private String bodyForEntity = "{\"entityid\":\"@@entityid@@\", " + requestedAttributes + "}";
    private String bodyForEid = "{\"eid\":@@eid@@, " + requestedAttributes + "}";
    private String bodyForInstitutionId =
        "{\"metaDataFields.coin:institution_id\":\"@@institution_id@@\", \"ALL_ATTRIBUTES\":true}";

    private String linkedQuery = "{$and: [{$or:[{\"data.allowedEntities.name\": {$in: [\"@@entityid@@\"]}}, {\"data" +
        ".allowedall\": true}]}, {\"data.state\":\"prodaccepted\"}]}";

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
    public List<ServiceProvider> getAllServiceProviders() {
        List<Map<String, Object>> providers = getMaps(getSpInputStream(body));
        List<Map<String, Object>> singleTenants = getMaps(getSingleTenantInputStream(body));

        List<ServiceProvider> serviceProviders = providers.stream()
            .map(this::transformManageMetadata)
            .map(sp -> this.serviceProvider(sp, EntityType.saml20_sp))
            .filter(sp -> !sp.isHidden())
            .collect(Collectors.toList());
        List<ServiceProvider> singleTenantsProviders = singleTenants.stream().map(this::transformManageMetadata).map
            (sp -> this.serviceProvider(sp, EntityType.single_tenant_template))
            .collect(Collectors.toList());
        singleTenantsProviders.forEach(tenant -> tenant.setExampleSingleTenant(true));
        serviceProviders.addAll(singleTenantsProviders);
        return serviceProviders;
    }

    @Override
    public Optional<ServiceProvider> getServiceProvider(String spEntityId, EntityType type, boolean searchRevisions) {
        if (StringUtils.isEmpty(spEntityId)) {
            return Optional.empty();
        }
        String body = bodyForEntity.replace("@@entityid@@", spEntityId);
        InputStream inputStream = type.equals(EntityType.saml20_sp) ? getSpInputStream(body) :
            getSingleTenantInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        if (providers.isEmpty()) {
            providers = getMaps(getSpRevisionInputStream(body));
        }
        return providers.stream().map(this::transformManageMetadata).map(sp -> this.serviceProvider(sp, type)).findFirst();
    }

    @Override
    public Optional<ServiceProvider> getServiceProviderById(Long spId, EntityType entityType) {
        if (spId == null) {
            return Optional.empty();
        }
        String body = bodyForEid.replace("@@eid@@", spId.toString());
        InputStream inputStream = entityType.equals(EntityType.saml20_sp) ? getSpInputStream(body) :
            getSingleTenantInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(sp -> this.serviceProvider(sp, entityType)).findFirst();
    }

    @Override
    public Optional<IdentityProvider> getIdentityProvider(String idpEntityId, boolean searchRevisions) {
        if (StringUtils.isEmpty(idpEntityId)) {
            return Optional.empty();
        }
        String body = bodyForEntity.replace("@@entityid@@", idpEntityId);
        InputStream inputStream = getIdpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        if (providers.isEmpty()) {
            providers = getMaps(getIdpRevisionInputStream(body));
        }
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider).findFirst();
    }

    @Override
    public List<IdentityProvider> getInstituteIdentityProviders(String instituteId) {
        String body = bodyForInstitutionId.replace("@@institution_id@@", instituteId);
        InputStream inputStream = getIdpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider)
            .collect(Collectors.toList());
    }

    @Override
    public List<IdentityProvider> getAllIdentityProviders() {
        InputStream inputStream = getIdpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider)
            .collect(Collectors.toList());
    }

    @Override
    public List<IdentityProvider> getLinkedIdentityProviders(String spId) {
        String replaced = linkedQuery.replace("@@entityid@@", spId);
        InputStream inputStream = searchIdp(replaced);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(this::identityProvider)
            .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProvider> getLinkedServiceProviders(String idpId) {
        String replaced = linkedQuery.replace("@@entityid@@", idpId);
        InputStream inputStream = searchSp(replaced);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream().map(this::transformManageMetadata).map(sp -> this.serviceProvider(sp, EntityType.saml20_sp))
                .collect(Collectors.toList());
    }

    @Override
    public List<ServiceProvider> getInstitutionalServicesForIdp(String instituteId) {
        String body = bodyForInstitutionId.replace("@@institution_id@@", instituteId);
        InputStream inputStream = getSpInputStream(body);
        List<Map<String, Object>> providers = getMaps(inputStream);
        return providers.stream()
            .map(this::transformManageMetadata)
            .map(sp -> this.serviceProvider(sp, EntityType.saml20_sp))
            .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getMaps(InputStream inputStream) {
        try {
            return objectMapper.readValue(inputStream, List.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private InputStream getIdpInputStream(String body) {
        return getIdpInputStreamFromCollection(body,"saml20_idp");
    }

    private InputStream getIdpRevisionInputStream(String body) {
        return getIdpInputStreamFromCollection(body,"saml20_idp_revision");
    }

    private InputStream getIdpInputStreamFromCollection(String body, String collection) {
        LOG.debug("Fetching IDP metadata entries from {} with body {}", manageBaseUrl);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange
            (manageBaseUrl + "/manage/api/internal/search/"+collection, HttpMethod.POST,
                new HttpEntity<>(body, this.httpHeaders), byte[].class);
        return new BufferedInputStream(new ByteArrayInputStream(responseEntity.getBody()));
    }

    private InputStream getSpInputStream(String body) {
        return getSpInputStreamFromCollection(body, "saml20_sp");
    }

    private InputStream getSpRevisionInputStream(String body) {
        return getSpInputStreamFromCollection(body, "saml20_sp_revision");
    }

    private InputStream getSpInputStreamFromCollection(String body, String collection) {
        LOG.debug("Fetching SP metadata entries from {} with body {}", manageBaseUrl, body);
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange
            (manageBaseUrl + "/manage/api/internal/search/"+collection, HttpMethod.POST,
                new HttpEntity<>(body, this.httpHeaders), byte[].class);
        return new BufferedInputStream(new ByteArrayInputStream(responseEntity.getBody()));
    }

    private InputStream searchIdp(String query) {
        return getSearchInputStream(query, "idp");
    }

    private InputStream searchSp(String query) {
        return getSearchInputStream(query, "sp");
    }

    private InputStream getSearchInputStream(String query, String type) {
        LOG.debug("Quering " + type + " metadata entries from {} with query {}", manageBaseUrl, body);
        String url;
        try {
            url = manageBaseUrl + "/manage/api/internal/rawSearch/saml20_" + type + "?query=" + URLEncoder.encode(query,
                    "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalArgumentException(e);
        }
        ResponseEntity<byte[]> responseEntity = restTemplate.exchange(url, HttpMethod.GET,
                new HttpEntity<>(this.httpHeaders), byte[].class);
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
