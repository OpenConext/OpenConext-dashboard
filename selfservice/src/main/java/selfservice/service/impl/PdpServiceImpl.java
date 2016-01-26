package selfservice.service.impl;

import static org.springframework.http.HttpHeaders.ACCEPT;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import selfservice.domain.CoinUser;
import selfservice.domain.Policy;
import selfservice.service.PdpService;

@Service
public class PdpServiceImpl implements PdpService {

  private static final String X_IDP_ENTITY_ID = "X-IDP-ENTITY-ID";
  private static final String X_UNSPECIFIED_NAME_ID = "X-UNSPECIFIED-NAME-ID";
  private static final String X_DISPLAY_NAME = "X-DISPLAY-NAME";

  private final RestTemplate pdpRestTemplate;

  private final String username;
  private final String password;

  @Autowired
  public PdpServiceImpl(@Value("${pdp.username}") String username, @Value("${pdp.password}") String password) {
    this.pdpRestTemplate = new RestTemplate();
    this.username = username;
    this.password = password;
  }

  @Override
  public List<Policy> allPolicies(CoinUser user) {
    RequestEntity<?> request = buildRequest("/protected/policies");
    ResponseEntity<List<Policy>> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<List<Policy>>() {});

    return response.getBody();
  }

  public Policy policy(Long id) {
    RequestEntity<?> request = buildRequest("/protected/policies/" + id);
    ResponseEntity<Policy> response = pdpRestTemplate.exchange(request, new ParameterizedTypeReference<Policy>() {});

    return response.getBody();
  }

  private RequestEntity<?> buildRequest(String path) {
    return RequestEntity
        .get(URI.create("https://pdp.test.surfconext.nl/pdp/api" + path))
        .header(AUTHORIZATION, authorizationHeaderValue())
        .header(ACCEPT, APPLICATION_JSON.toString())
        .header(CONTENT_TYPE, APPLICATION_JSON.toString())
        .header(X_IDP_ENTITY_ID, "http://mock-idp")
        .header(X_UNSPECIFIED_NAME_ID, "urn:collab:person:example.com:admin")
        .header(X_DISPLAY_NAME, "Johnny Doe")
//        .header(X_IDP_ENTITY_ID, user.getIdp().getId())
//        .header(X_UNSPECIFIED_NAME_ID, user.getUid())
//        .header(X_DISPLAY_NAME, user.getDisplayName())
        .accept(APPLICATION_JSON).build();
  }

  private String authorizationHeaderValue() {
    System.err.println(username + " " + password);
    return "Basic " + new String(Base64.encode(String.format("%s:%s", username, password).getBytes()));
  }
}
