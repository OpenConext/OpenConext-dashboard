package csa.janus;

import com.github.tomakehurst.wiremock.junit.WireMockClassRule;

import csa.janus.domain.EntityMetadata;
import csa.janus.domain.JanusEntity;
import csa.janus.domain.ARP;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class JanusRestClientTest {

  private String IDP = "http://qixi.dev.surfconext.nl";
  private Janus janus;

  @ClassRule
  public static WireMockClassRule wireMockRule = new WireMockClassRule(8889);

  @Before
  public void setUp() throws Exception {
    janus = new JanusRestClient(new URI("http://localhost:8889/rest"), "engineblock", "secret");
  }

  @Test
  public void testGetMetadataByEntityId() throws Exception {
    stub("janus-responses/metadata_by_entity_id.json");
    EntityMetadata metadata = janus.getMetadataByEntityId("http://mock-idp");
    assertTrue(metadata.isPublishedInEduGain());
  }


  @Test
  public void testGetAllowedSps() throws Exception {
    stub("janus-responses/allowed_sps.json");
    List<String> allowedSps = janus.getAllowedSps(IDP);
    assertEquals(9, allowedSps.size());
  }

  @Test
  public void testGetSpList() throws Exception {
    stub("janus-responses/all_sps.json");
    List<EntityMetadata> sps = janus.getSpList();
    assertEquals(126, sps.size());
    boolean entity = sps.stream().filter(sp -> sp.getAppEntityId().equals("https://beta.foodl.org/")).findFirst().get().isPublishedInEduGain();
    assertTrue(entity);
  }

  @Test
  public void testGetIdpList() throws Exception {
    stub("janus-responses/all_idps.json");
    List<EntityMetadata> idps = janus.getIdpList();
    assertEquals(69, idps.size());
    EntityMetadata idp = idps.stream().filter(entry -> entry.getAppEntityId().equals("http://mock-idp")).findFirst().get();
    assertTrue(idp.isPublishedInEduGain());
    assertEquals("SURFNET", idp.getInstutionId());
  }

  @Test
  public void testGetNoAttributesArp() throws Exception {
    stub("janus-responses/no_attributes_arp.json");
    ARP arp = janus.getArp(IDP);
    assertFalse(arp.isNoArp());
    assertTrue(arp.isNoAttrArp());
  }

  @Test
  public void testGetNoArp() throws Exception {
    stub("janus-responses/no_arp.json");
    ARP arp = janus.getArp(IDP);
    assertTrue(arp.isNoArp());
    assertFalse(arp.isNoAttrArp());
  }

  @Test
  public void testGetArp() throws Exception {
    stub("janus-responses/arp.json");
    ARP arp = janus.getArp(IDP);
    assertFalse(arp.isNoArp());
    assertFalse(arp.isNoAttrArp());
    assertEquals(8, arp.getAttributes().size());
  }

  @Test
  public void testGetEntity() throws Exception {
    stub("janus-responses/entity.json");
    JanusEntity entity = janus.getEntity(IDP);
    assertEquals(IDP, entity.getEntityId());
  }

  private void stub(String jsonFile) throws IOException {
    wireMockRule.stubFor(get(urlMatching("/rest.*")).willReturn(aResponse().withStatus(200).
      withHeader("Content-Type", "application/json").withBody(IOUtils.toString(new ClassPathResource(jsonFile).getInputStream()))));
  }
}
