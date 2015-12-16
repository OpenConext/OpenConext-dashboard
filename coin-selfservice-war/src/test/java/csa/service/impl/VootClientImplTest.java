package csa.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import csa.service.VootClient;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.UnsupportedEncodingException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class VootClientImplTest {

  private String userId = "urn:collab:person:surfnet.nl:user";
  private String groupId = "urn:collab:group:surfnet.nl:group";

  private VootClient subject;

  @Before
  public void setUp() throws Exception {
    subject = new VootClientImpl("http://localhost:8889/oauth/token", "client", "secret", "groups", "http://localhost:8889");;
  }

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8889);

  @Test
  public void test_has_access() throws Exception {
    String response = IOUtils.toString(new ClassPathResource("voot-json/group.json").getInputStream());
    setUpAccessTokenResponse();
    wireMockRule.stubFor(get(urlEqualTo("/internal/groups/" + userId + "/" + groupId)).willReturn(aResponse().withStatus(201).
      withHeader("Content-Length", Integer.toString(response.getBytes("UTF-8").length)).withHeader("Content-Type", "application/json").withBody(response)));
    //very subtle bug in WireMock (combined with the underlying implementation sun.net.www.http.HttpClient because moving to 1.8 makes the Thread.sleep not necessary)
    Thread.sleep(5000);
    assertTrue(subject.hasAccess(userId, groupId));
  }

  @Test
  public void test_no_has_access() throws Exception {
    setUpAccessTokenResponse();
    wireMockRule.stubFor(get(urlEqualTo("/internal/groups/" + userId + "/" + groupId)).willReturn(aResponse().withStatus(404)));
    assertFalse(subject.hasAccess(userId, groupId));
  }

  private void setUpAccessTokenResponse() throws UnsupportedEncodingException {
    String json = "{\"access_token\": \"123456\",\"token_type\": \"client-credentials\"}";
    wireMockRule.stubFor(post(urlEqualTo("/oauth/token")).willReturn(aResponse().withStatus(201).
      withHeader("Content-Length", Integer.toString(json.getBytes("UTF-8").length)).withHeader("Content-Type", "application/json").withBody(json)));
  }

}
