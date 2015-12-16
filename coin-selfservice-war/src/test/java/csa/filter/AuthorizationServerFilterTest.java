package csa.filter;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.IOException;
import java.util.Arrays;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.codec.Base64;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import csa.domain.CheckTokenResponse;

@RunWith(MockitoJUnitRunner.class)
public class AuthorizationServerFilterTest {

  private AuthorizationServerFilter subject;
  private String authorizationHeaderValue;
  private String client = "client";
  private String secret = "secret";

  @Mock
  private FilterChain chain;

  private MockHttpServletRequest request;
  private MockHttpServletResponse response;

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8889);

  @Before
  public void before() {
    subject = new AuthorizationServerFilter("http://localhost:8889/oauth/check_token", client, secret);

    request = new MockHttpServletRequest("GET", "/anyUrl");
    response = new MockHttpServletResponse();

    authorizationHeaderValue = "Basic " + new String(Base64.encode(String.format("%s:%s", client, secret).getBytes()));
  }

  @Test
  public void test_filter_without_access_token() throws Exception {
    subject.doFilter(request, response, chain);
    assertEquals(403, response.getStatus());
    assertEquals("OAuth secured endpoint", response.getErrorMessage());
    verifyNoMoreInteractions(chain);
  }

  @Test
  public void test_filter_wrong_access_token() throws Exception {
    stubForCheckToken("authz-server-json/error_response.json", 400);
    assertEquals(403, response.getStatus());
    assertEquals("invalid token", response.getErrorMessage());
    verifyNoMoreInteractions(chain);
  }

  @Test
  public void test_filter_correct_access_token() throws Exception {
    stubForCheckToken("authz-server-json/check_token_response.json", 201);
    verify(chain).doFilter(request, response);
    CheckTokenResponse checkToken = (CheckTokenResponse) request.getAttribute(AuthorizationServerFilter.CHECK_TOKEN_RESPONSE);
    assertEquals(Arrays.asList("stats", "cross-idp-services", "actions"), checkToken.getScopes());
    assertEquals("http://mock-idp", checkToken.getIdPEntityId());
  }

  private void stubForCheckToken(String jsonFileName, int status) throws IOException, ServletException {
    String json = IOUtils.toString(new ClassPathResource(jsonFileName).getInputStream());
    wireMockRule.stubFor(post(urlEqualTo("/oauth/check_token")).withHeader("Authorization", equalTo(authorizationHeaderValue)).willReturn(aResponse().
      withStatus(status).withHeader("Content-Type", "application/json").withHeader("Content-Length", Integer.toString(json.getBytes("UTF-8").length)).withBody(json)));
    request.addHeader("Authorization", "bearer a3106aaf-c6a6-4a0a-bca4-6894c1284053");
    subject.doFilter(request, response, chain);

  }

}
