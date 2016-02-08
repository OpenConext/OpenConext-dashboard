package selfservice.service.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;
import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import selfservice.domain.CoinUser;
import selfservice.domain.Policy;
import selfservice.domain.Policy.Attribute;
import selfservice.service.PdpService;
import selfservice.util.SpringSecurity;

@Service
public class PdpServiceImpl implements PdpService {

  private static final Logger LOG = LoggerFactory.getLogger(PdpServiceImpl.class);

  protected static final String X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";
  protected static final String X_UNSPECIFIED_NAME_ID = "X-UNSPECIFIED-NAME-ID";
  protected static final String X_DISPLAY_NAME = "X-DISPLAY-NAME";

  private final RestTemplate pdpRestTemplate;
  private final String server;

  @Autowired
  public PdpServiceImpl(@Value("${pdp.server}") String server, @Value("${pdp.username}") String username, @Value("${pdp.password}") String password) {
    checkArgument(server.startsWith("http"));
    checkArgument(!isNullOrEmpty(username));
    checkArgument(!isNullOrEmpty(password));

    this.pdpRestTemplate = new RestTemplate(clientHttpRequestFactory());
    this.pdpRestTemplate.setInterceptors(ImmutableList.of((request, body, execution) -> {
      CoinUser user = SpringSecurity.getCurrentUser();

      request.getHeaders().add(AUTHORIZATION, authorizationHeaderValue(username, password));
      request.getHeaders().add(ACCEPT, APPLICATION_JSON.toString());
      request.getHeaders().add(CONTENT_TYPE, APPLICATION_JSON.toString());
      request.getHeaders().add(X_IDP_ENTITY_ID, user.getIdp().getId());
      request.getHeaders().add(X_UNSPECIFIED_NAME_ID, user.getUid());
      request.getHeaders().add(X_DISPLAY_NAME, user.getDisplayName());

      return execution.execute(request, body);
    }));

    this.server = server;
  }

  private ClientHttpRequestFactory clientHttpRequestFactory() {
    HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
    requestFactory.setReadTimeout(2000);
    requestFactory.setConnectTimeout(2000);

    return requestFactory;
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

  @Override
  public List<Policy> revisions(Long id) {
    RequestEntity<?> request = buildGetRequest("/protected/revisions/" + id);
    ResponseEntity<List<Policy>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<Policy>>() {});

    return response.getBody();
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

  @Override
  public List<Attribute> allowedAttributes() {
    RequestEntity<?> request = buildGetRequest("/protected/attributes/");
    ResponseEntity<List<AllowedAttribute>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<AllowedAttribute>>() {});

    return response.getBody().stream().map(aa -> new Attribute(aa.attributeId, aa.value)).collect(Collectors.toList());
  }

  private String authorizationHeaderValue(String username, String password) {
    return "Basic " + new String(Base64.encode(String.format("%s:%s", username, password).getBytes()));
  }

  private static final class AllowedAttribute {
    @JsonProperty("AttributeId")
    private String attributeId;
    @JsonProperty("Value")
    private String value;
  }

}
