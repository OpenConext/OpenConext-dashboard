package selfservice.service.impl;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import selfservice.domain.Group;
import selfservice.service.VootClient;
import org.apache.commons.io.IOUtils;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class VootClientImplTest {

  private String userId = "urn:collab:person:surfnet.nl:user";

  private VootClient subject = new VootClientImpl("http://localhost:8889/oauth/token", "client", "secret", "groups", "http://localhost:8889");

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8889);

  @Test
  public void test_groups() throws Exception {
    stubResponse("voot-json/groups.json");
    List<Group> groups = subject.groups(userId);
    assertEquals(2, groups.size());
    assertEquals("urn:collab:group:surfnet.nl:nl:surfnet:diensten:admins", groups.get(0).getId());
    assertEquals("urn:collab:group:surfnet.nl:nl:surfnet:diensten:members", groups.get(1).getId());
  }

  @Test
  public void test_empty_groups() throws Exception {
    stubResponse("voot-json/empty_groups.json");
    List<Group> groups = subject.groups(userId);
    assertTrue(groups.isEmpty());
  }

  private void stubResponse(String jsonFileName) throws IOException {
    String json = "{\"access_token\": \"123456\",\"token_type\": \"client-credentials\"}";
    wireMockRule.stubFor(post(urlEqualTo("/oauth/token")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(json)));

    String response = IOUtils.toString(new ClassPathResource(jsonFileName).getInputStream());
    wireMockRule.stubFor(get(urlEqualTo("/internal/groups/" + userId)).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(response)));
  }
}
