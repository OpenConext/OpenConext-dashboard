package dashboard.pdp;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableList;
import dashboard.control.Constants;
import dashboard.domain.Attribute;
import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.domain.Policy;
import dashboard.util.SpringSecurity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON;

public class PdpServiceImpl implements PdpService, Constants {

    private static final Logger LOG = LoggerFactory.getLogger(PdpServiceImpl.class);

    protected static final String X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";
    protected static final String X_UNSPECIFIED_NAME_ID = "X-UNSPECIFIED-NAME-ID";
    protected static final String X_DISPLAY_NAME = "X-DISPLAY-NAME";

    private final RestTemplate pdpRestTemplate;
    private final String server;
    private final ObjectMapper objectMapper;

    public PdpServiceImpl(String server, String username, String password) {
        checkArgument(server.startsWith("http"));
        checkArgument(!isNullOrEmpty(username));
        checkArgument(!isNullOrEmpty(password));

        this.pdpRestTemplate = new RestTemplate(clientHttpRequestFactory(10 * 1000));

        this.pdpRestTemplate.setInterceptors(ImmutableList.of((request, body, execution) -> {
            CoinUser user = SpringSecurity.getCurrentUser();

            IdentityProvider idp = user.getSwitchedToIdp().orElse(user.getIdp());

            HttpHeaders headers = request.getHeaders();
            headers.setContentType(APPLICATION_JSON);
            headers.setAccept(ImmutableList.of(APPLICATION_JSON));
            headers.set(AUTHORIZATION, authorizationHeaderValue(username, password));
            headers.set(X_IDP_ENTITY_ID, idp.getId());
            headers.set(X_UNSPECIFIED_NAME_ID, user.getUid());
            headers.set(X_DISPLAY_NAME, user.getDisplayName());

            return execution.execute(request, body);
        }));

        this.server = server;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public boolean isAvailable() {
        try {
            Set<HttpMethod> options = pdpRestTemplate.optionsForAllow(buildUri("/protected/policies"));

            // the default spring-boot options call will return all methods.
            // check if it does not contain PATCH to make sure we get an answer from our own endpoint
            return options.contains(GET) && !options.contains(PATCH);
        } catch (RestClientException e) {
            LOG.warn("PDP protected api was not available", e);
            return false;
        }
    }

    @Override
    public List<Policy> policies() {
        RequestEntity<?> request = buildGetRequest("/protected/policies");

        return executeWithExceptionLogging(() -> {
            return pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<Policy>>() {
            }).getBody();
        });
    }

    public Policy policy(Long id) {
        RequestEntity<?> request = buildGetRequest("/protected/policies/" + id);

        return executeWithExceptionLogging(() -> {
            return pdpRestTemplate.exchange(request, new ParameterizedTypeReference<Policy>() {
            }).getBody();
        });
    }

    @Override
    public Policy create(Policy policy) {
        RequestEntity<?> request = buildPostRequest("/protected/policies", policy);
        try {
            try {
                String json = new ObjectMapper().writerWithDefaultPrettyPrinter().writeValueAsString(policy);
                LOG.info("creation of policy {}", json);
            } catch (IOException e) {
                LOG.error("oeps", e);
            }
            ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<>() {
            });
            return response.getBody();
        } catch (HttpStatusCodeException sce) {
            if (sce.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                Optional<PolicyNameNotUniqueException> exception = extractException(sce.getResponseBodyAsByteArray());
                if (exception.isPresent()) {
                    throw exception.get();
                }
            }

            LOG.error("Response error: {} {}:\n {}", sce.getStatusCode(), sce.getStatusText(), sce.getResponseBodyAsString());
            throw new RuntimeException(sce);
        }
    }

    private Optional<PolicyNameNotUniqueException> extractException(byte[] byteArray) {
        try {
            JsonNode readTree = objectMapper.readTree(byteArray);

            return Optional.ofNullable(readTree.findValue("details")).map(details -> {
                Iterable<Map.Entry<String, JsonNode>> fields = () -> details.fields();
                return StreamSupport.stream(fields.spliterator(), false)
                        .map(entry -> entry.getValue().asText())
                        .filter(StringUtils::hasText)
                        .collect(Collectors.joining(", "));
            }).map(PolicyNameNotUniqueException::new);
        } catch (IOException e) {
            return Optional.empty();
        }
    }

    @Override
    public Policy update(Policy policy) {
        RequestEntity<?> request = buildPutRequest("/protected/policies", policy);
        return executeWithExceptionLogging(() -> {
            ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<>() {
            });
            return response.getBody();
        });
    }

    @Override
    public ResponseEntity<String> delete(Long id) {
        RequestEntity<?> request = buildDeleteRequest("/protected/policies/" + id);
        return executeWithExceptionLogging(() -> pdpRestTemplate.exchange(request, String.class));
    }

    @Override
    public List<Policy> revisions(Long id) {
        RequestEntity<?> request = buildGetRequest("/protected/revisions/" + id);

        return executeWithExceptionLogging(() -> {
            ResponseEntity<List<Policy>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<>() {
            });
            return response.getBody();
        });
    }

    @Override
    public List<Attribute> allowedAttributes() {
        RequestEntity<?> request = buildGetRequest("/protected/attributes/");

        return executeWithExceptionLogging(() -> {
            ResponseEntity<List<AllowedAttribute>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<>() {
            });
            return response.getBody().stream().map(aa -> new Attribute(aa.attributeId, aa.value)).collect(Collectors.toList());
        });
    }

    private <T> T executeWithExceptionLogging(Supplier<T> makeRequest) {
        try {
            return makeRequest.get();
        } catch (HttpStatusCodeException sce) {
            LOG.error("Response error: {} {}:\n {}", sce.getStatusCode(), sce.getStatusText(), sce.getResponseBodyAsString());
            throw new RuntimeException(sce);
        }
    }

    private RequestEntity<?> buildDeleteRequest(String path) {
        return RequestEntity.delete(buildUri(path)).build();
    }

    private RequestEntity<?> buildPostRequest(String path, Object body) {
        return RequestEntity.post(buildUri(path)).body(body);
    }

    private RequestEntity<?> buildPutRequest(String path, Object body) {
        return RequestEntity.put(buildUri(path)).body(body);
    }

    private RequestEntity<?> buildGetRequest(String path) {
        return RequestEntity.get(buildUri(path)).build();
    }

    private URI buildUri(String path) {
        checkArgument(path.startsWith("/"));
        return URI.create(String.format("%s/pdp/api%s", server, path));
    }


    private static final class AllowedAttribute {
        @JsonProperty("AttributeId")
        private String attributeId;
        @JsonProperty("Value")
        private String value;
    }

}
