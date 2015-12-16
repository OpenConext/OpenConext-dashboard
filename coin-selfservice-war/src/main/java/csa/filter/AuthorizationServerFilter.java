package csa.filter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import com.google.common.base.Preconditions;

import csa.domain.CheckTokenResponse;

public class AuthorizationServerFilter extends GenericFilterBean {

  public static final String CHECK_TOKEN_RESPONSE = "CheckTokenResponse";

  private static final Logger LOG = LoggerFactory.getLogger(AuthorizationServerFilter.class);
  private static final String BEARER = "bearer";

  /*
   * Details needed so that we may check tokens presented to us by clients. This application uses them to authenticate via
   * Basic authentication with the oAuth server.
   */
  private final String oauthCheckTokenEndpointUrl;
  private final String oauthCheckTokenClientId;
  private final String oauthCheckTokenSecret;

  private final RestTemplate restTemplate = new RestTemplate();

  public AuthorizationServerFilter(String oauthCheckTokenEndpointUrl, String oauthCheckTokenClientId, String oauthCheckTokenSecret) {
    this.oauthCheckTokenEndpointUrl = oauthCheckTokenEndpointUrl;
    this.oauthCheckTokenClientId = oauthCheckTokenClientId;
    this.oauthCheckTokenSecret = oauthCheckTokenSecret;
  }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest request = (HttpServletRequest) req;
    HttpServletResponse response = (HttpServletResponse) res;

    final String accessToken = getAccessToken(request);

    if (accessToken == null) {
      sendError(response, HttpServletResponse.SC_FORBIDDEN, "OAuth secured endpoint");
      return;
    }
    MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
    formData.add("token", accessToken);
    HttpHeaders headers = new HttpHeaders();
    headers.set("Authorization", getAuthorizationHeader(oauthCheckTokenClientId, oauthCheckTokenSecret));
    try {
      Map<String, Object> map = postForMap(oauthCheckTokenEndpointUrl, formData, headers);
      CheckTokenResponse tokenResponse = parseCheckTokenResponse(map);
      request.setAttribute(CHECK_TOKEN_RESPONSE, tokenResponse);
      chain.doFilter(request, response);
    } catch (HttpClientErrorException e) {
      sendError(response, HttpServletResponse.SC_FORBIDDEN, "invalid token");
    }
  }

  private CheckTokenResponse parseCheckTokenResponse(Map<String, Object> map) {
    @SuppressWarnings("unchecked")
    List<String> scopes = (List<String>) map.get("scope");
    Preconditions.checkArgument(scopes != null, "Authorization server did not return an 'scope' value");

    /*
     * authenticatingAuthority is optional for client-credential clients
     */
    String authenticatingAuthority = (String) map.get("authenticatingAuthority");
    return new CheckTokenResponse(authenticatingAuthority, scopes);
  }

  private String getAuthorizationHeader(String clientId, String clientSecret) {
    String creds = String.format("%s:%s", clientId, clientSecret);
    try {
      return "Basic " + new String(Base64.encode(creds.getBytes("UTF-8")));
    } catch (UnsupportedEncodingException e) {
      throw new IllegalStateException("Could not convert String");
    }
  }

  private Map<String, Object> postForMap(String path, MultiValueMap<String, String> formData, org.springframework.http.HttpHeaders headers) {
    if (headers.getContentType() == null) {
      headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
    }
    return restTemplate.exchange(path, HttpMethod.POST, new HttpEntity<>(formData, headers), Map.class).getBody();
  }

  private String getAccessToken(HttpServletRequest request) {
    String accessToken = null;
    String header = request.getHeader("Authorization");
    if (header != null) {
      int space = header.indexOf(' ');
      if (space > 0) {
        String method = header.substring(0, space);
        if (BEARER.equalsIgnoreCase(method)) {
          accessToken = header.substring(space + 1);
        }
      }
    }
    return accessToken;
  }

  private void sendError(HttpServletResponse response, int statusCode, String reason) {
    LOG.warn("No valid access-token on request. Will respond with error response: {} {}", statusCode, reason);
    try {
      response.sendError(statusCode, reason);
      response.flushBuffer();
    } catch (IOException e) {
      throw new RuntimeException(reason, e);
    }
  }

}
