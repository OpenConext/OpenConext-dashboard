package selfservice.service.impl;

import selfservice.domain.Group;
import selfservice.service.VootClient;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class VootClientImpl implements VootClient {

  private final String serviceUrl;

  private final OAuth2RestTemplate vootService;

  public VootClientImpl(String accessTokenUri, String clientId, String clientSecret, String spaceDelimitedScopes, String serviceUrl) {
    this.serviceUrl = serviceUrl;
    this.vootService = new OAuth2RestTemplate(vootConfiguration(clientId, clientSecret, accessTokenUri, Arrays.asList(spaceDelimitedScopes.split(" "))));
  }

  private OAuth2ProtectedResourceDetails vootConfiguration(String clientId, String clientSecret, String accessTokenUri, List<String> scopes) {
    ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
    details.setId("dashboard");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setScope(scopes);

    return details;
  }

  @Override
  public List<Group> groups(String userId) {
    @SuppressWarnings("unchecked")
    List<Map<String, Object>> maps = vootService.getForObject(serviceUrl + "/internal/groups/{userId}", List.class, userId);

    return maps.stream().map(map -> new Group((String) map.get("id"))).collect(toList());
  }

}
