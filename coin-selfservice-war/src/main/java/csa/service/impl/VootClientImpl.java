package csa.service.impl;

import csa.service.VootClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.Map;

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
    details.setId("csa");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setScope(Arrays.asList(spaceDelimitedScopes.split(" ")));
    return details;
  }

  @Override
  public boolean hasAccess(String personId, String groupId) {
    try {
      Map<String, ?> group = vootService.getForObject(serviceUrl + "/internal/groups/{userId}/{groupId}", Map.class, personId, groupId);
      LOG.debug("Retrieved group: {}", group);
      return true;
    } catch (HttpClientErrorException e) {
      LOG.debug(String.format("Unauthorized access. User does not belong to the group %s", groupId), e);
      return false;
    }
  }

}
