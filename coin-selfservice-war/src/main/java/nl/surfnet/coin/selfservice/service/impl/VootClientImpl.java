package nl.surfnet.coin.selfservice.service.impl;

import nl.surfnet.coin.selfservice.domain.Group;
import nl.surfnet.coin.selfservice.service.VootClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class VootClientImpl implements VootClient {

  private static final Logger LOG = LoggerFactory.getLogger(VootClientImpl.class);

  private String accessTokenUri;

  private String clientId;

  private String clientSecret;

  private String spaceDelimitedScopes;

  private String serviceUrl;

  private OAuth2RestTemplate vootService;

  public VootClientImpl(String accessTokenUri, String clientId, String clientSecret, String spaceDelimitedScopes, String serviceUrl) {
    this.accessTokenUri = accessTokenUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.spaceDelimitedScopes = spaceDelimitedScopes;
    this.serviceUrl = serviceUrl;
    vootService = new OAuth2RestTemplate(vootConfiguration());
  }

  private OAuth2ProtectedResourceDetails vootConfiguration() {
    ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
    LOG.debug("clientId: {}", clientId);
    details.setId("dashboard");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setScope(Arrays.asList(spaceDelimitedScopes.split(" ")));
    return details;
  }

  @Override
  public List<Group> groups(String userId) {
    List<Map<String, Object>> maps = vootService.getForObject(serviceUrl + "/internal/groups/{userId}", List.class, userId);
    List<Group> groups = maps.stream().map(item -> new Group((String) item.get("id"))).collect(Collectors.toList());
    LOG.debug("Retrieved groups: {}", groups);
    return groups;

  }

}
