package selfservice.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.RequestEntity.HeadersBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import selfservice.domain.CoinUser;
import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;
import selfservice.service.PdpService;
import selfservice.util.SpringSecurity;

@Service
public class PdpServiceImpl implements PdpService {

  private static final Logger LOG = LoggerFactory.getLogger(PdpServiceImpl.class);

  private static final String X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";
  private static final String X_UNSPECIFIED_NAME_ID = "X-UNSPECIFIED-NAME-ID";
  private static final String X_DISPLAY_NAME = "X-DISPLAY-NAME";

  private final RestTemplate pdpRestTemplate;

  private final String server;
  private final String username;
  private final String password;

  @Autowired
  public PdpServiceImpl(@Value("${pdp.server}") String server, @Value("${pdp.username}") String username, @Value("${pdp.password}") String password) {
    checkArgument(server.startsWith("http"));

    this.pdpRestTemplate = new RestTemplate();

    this.server = server;
    this.username = username;
    this.password = password;
  }

  @Override
  public List<Policy> policies() {
    RequestEntity<?> request = buildGetRequest("/protected/policies");
    ResponseEntity<List<Policy>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<Policy>>() {});

    return response.getBody();
  }

  public Policy policy(Long id) {
    RequestEntity<?> request = buildGetRequest("/protected/policies/" + id);
    ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<Policy>() {});

    return response.getBody();
  }

  @Override
  public Policy create(Policy policy) {
    RequestEntity<?> request = buildPostRequest("/protected/policies", policy);
    try {
      ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<Policy>() {});
      return response.getBody();
    } catch (HttpClientErrorException e) {
      LOG.error("Response error: {} {}:\n {}", e.getStatusCode(), e.getStatusText(), e.getResponseBodyAsString());
      throw Throwables.propagate(e);
    }
  }

  @Override
  public Policy update(Policy policy) {
    RequestEntity<?> request = buildPutRequest("/protected/policies", policy);
    ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<Policy>() {});

    return response.getBody();
  }

  @Override
  public void delete(Long id) {
    RequestEntity<?> request = buildDeleteRequest("/protected/policies/" + id);
    pdpRestTemplate.exchange(request, String.class);
  }

  private RequestEntity<?> buildDeleteRequest(String path) {
    return addDefaultHeaders(RequestEntity.delete(buildUri(path))).build();
  }

  private RequestEntity<?> buildPostRequest(String path, Object body) {
    return addDefaultHeaders(RequestEntity.post(buildUri(path))).body(body);
  }

  private RequestEntity<?> buildPutRequest(String path, Object body) {
    return addDefaultHeaders(RequestEntity.put(buildUri(path))).body(body);
  }

  private RequestEntity<?> buildGetRequest(String path) {
    return addDefaultHeaders(RequestEntity.get(buildUri(path))).build();
  }

  private <B extends HeadersBuilder<B>> B addDefaultHeaders(HeadersBuilder<B> builder) {
    CoinUser user = SpringSecurity.getCurrentUser();

    return builder
        .header(AUTHORIZATION, authorizationHeaderValue())
        .header(ACCEPT, APPLICATION_JSON.toString())
        .header(CONTENT_TYPE, APPLICATION_JSON.toString())
        .header(X_IDP_ENTITY_ID, user.getIdp().getId())
        .header(X_UNSPECIFIED_NAME_ID, user.getUid())
        .header(X_DISPLAY_NAME, user.getDisplayName())
        .accept(APPLICATION_JSON);
  }

  private URI buildUri(String path) {
    checkArgument(path.startsWith("/"));
    return URI.create(String.format("%s/pdp/api%s", server, path));
  }

  @Override
  public List<Attribute> allowedAttributes() {
    RequestEntity<?> request = buildGetRequest("/protected/attributes/");
    ResponseEntity<List<AllowedAttribute>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<AllowedAttribute>>() {});

    return response.getBody().stream().map(aa -> new Attribute(aa.attributeId, aa.value)).collect(Collectors.toList());
  }

  private String authorizationHeaderValue() {
    return "Basic " + new String(Base64.encode(String.format("%s:%s", username, password).getBytes()));
  }

  private static final class AllowedAttribute {
    @JsonProperty("AttributeId")
    private String attributeId;
    @JsonProperty("Value")
    private String value;
  }

}
