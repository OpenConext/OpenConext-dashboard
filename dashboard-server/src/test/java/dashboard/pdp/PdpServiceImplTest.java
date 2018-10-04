package dashboard.pdp;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.options;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.google.common.io.BaseEncoding.base64;
import static com.google.common.net.HttpHeaders.ALLOW;
import static com.google.common.net.HttpHeaders.AUTHORIZATION;
import static com.google.common.net.HttpHeaders.CONTENT_TYPE;
import static com.google.common.net.MediaType.JSON_UTF_8;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.List;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;

import dashboard.domain.CoinUser;
import dashboard.domain.IdentityProvider;
import dashboard.domain.Policy;
import dashboard.filter.SpringSecurityUtil;

public class PdpServiceImplTest {

  private PdpService pdpService = new PdpServiceImpl("http://localhost:8889", "pdp-user", "pdp-password");

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Before
  public void setup() {
    CoinUser coinUser = new CoinUser();
    coinUser.setUid("user-id");
    coinUser.setIdp(new IdentityProvider("idp-id", "institution-id", "idp-name", 1L));
    SpringSecurityUtil.setAuthentication(coinUser);
  }

  @After
  public void after() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void pdpApiIsNotAvailableWhenTheAllowHeaderContainsThePatchMethod() {
    stubFor(options(urlEqualTo("/pdp/api/protected/policies"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(ALLOW, "GET, POST, PUT, DELETE, TRACE, PATCH, OPTIONS")));

    assertThat(pdpService.isAvailable(), is(false));
  }

  @Test
  public void pdpApiIsAvailableWhenTheAllowHeaderIsCorrect() {
    stubFor(options(urlEqualTo("/pdp/api/protected/policies"))
        .willReturn(aResponse()
            .withStatus(200)
            .withHeader(ALLOW, "GET, POST, PUT, DELETE, OPTIONS")));

    assertThat(pdpService.isAvailable(), is(true));
  }

  @Test
  public void pdpApiRequestsShoudContainAuthorizationHeader() {
    stubFor(get(urlEqualTo("/pdp/api/protected/policies"))
        .withHeader(AUTHORIZATION, matching("Basic " + base64().encode("pdp-user:pdp-password".getBytes(UTF_8))))
        .willReturn(aResponse().withStatus(200)
            .withHeader(CONTENT_TYPE, JSON_UTF_8.toString())
            .withBody("[{\"id\": 2}]")));

    List<Policy> policies = pdpService.policies();

    assertThat(policies, hasSize(1));
  }

  @Test
  public void pdpApiRequestsShoudContainIdpAuthorizationHeader() {
    stubFor(get(urlEqualTo("/pdp/api/protected/policies"))
        .withHeader(PdpServiceImpl.X_IDP_ENTITY_ID, matching("idp-id"))
        .willReturn(aResponse().withStatus(200)
            .withHeader(CONTENT_TYPE, JSON_UTF_8.toString())
            .withBody("[{\"id\": 2}]")));

    List<Policy> policies = pdpService.policies();

    assertThat(policies, hasSize(1));
  }

  @Test
  public void createPdpRequestShouldHaveCorrectContentType() {
    stubFor(post(urlEqualTo("/pdp/api/protected/policies"))
        .withHeader(CONTENT_TYPE, matching("application/json"))
        .willReturn(aResponse().withStatus(200)
            .withHeader(CONTENT_TYPE, JSON_UTF_8.toString())
            .withBody("{\"id\": 2}")));

    Policy policy = pdpService.create(new Policy());

    assertThat(policy.getId(), is(2L));
  }

  @Test
  public void createPdpRequestWithADuplicateName() {
    stubFor(post(urlEqualTo("/pdp/api/protected/policies"))
        .willReturn(aResponse().withStatus(400)
            .withHeader(CONTENT_TYPE, JSON_UTF_8.toString())
            .withBody("{\"timestamp\":1457623248928,\"status\":400,\"error\":\"Bad Request\",\"path\":\"//protected/policies\",\"details\":{\"name\":\"Policy name must be unique. asdf is already taken\"}}")));

    try {
      pdpService.create(new Policy());
      fail("Should throw PolicyException");
    } catch (PolicyNameNotUniqueException e) {
      assertThat(e.getMessage(), startsWith("Policy name must be unique"));
    }
  }
}
