package selfservice.manage;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UrlResourceManageTest {

  private static UrlResourceManage subject;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Before
  public void before() throws Exception {
    // wireMockRule can not be static
    if (subject == null) {
      String spResponse = IOUtils.toString(new ClassPathResource("manage/service-providers.json").getInputStream());
      stubFor(post(urlEqualTo("/manage/api/internal/search/saml20_sp")).willReturn(aResponse().withStatus(200)
        .withHeader("Content-Type", "application/json").withBody(spResponse)));

      String idpResponse = IOUtils.toString(new ClassPathResource("manage/identity-providers.json").getInputStream());
      stubFor(post(urlEqualTo("/manage/api/internal/search/saml20_idp")).willReturn(aResponse().withStatus(200)
        .withHeader("Content-Type", "application/json").withBody(idpResponse)));

      String singleTenantTemplateResponse = IOUtils.toString(new ClassPathResource("manage/single-tenants.json").getInputStream());
      stubFor(post(urlEqualTo("/manage/api/internal/search/single_tenant_template")).willReturn(aResponse().withStatus(200)
        .withHeader("Content-Type", "application/json").withBody(singleTenantTemplateResponse)));

      subject = new UrlResourceManage("user", "password", "http://localhost:8889");
    }
  }

  @Test
  public void testServiceProviders() throws IOException {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders("idp");
    assertEquals(66, serviceProviders.size());
  }

  @Test
  public void testIdentityProviders() throws IOException {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(11, identityProviders.size());
  }

  @Test
  public void testGetServiceProvider() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://teams.surfconext.nl/shibboleth", EntityType.saml20_sp).get();
    assertFalse(serviceProvider.isExampleSingleTenant());
  }

  @Test
  public void testAllowedEntityIds() {
    IdentityProvider identityProvider = subject.getIdentityProvider(
      "https://thki-sid.pt-48.utr.surfcloud.nl/ssp/saml2/idp/metadata.php").get();
    Set<String> allowedEntityIds = identityProvider.getAllowedEntityIds();
    assertEquals(5, allowedEntityIds.size());
    assertFalse(identityProvider.isAllowedAll());
  }

  @Test
  public void testContactPersons() {
    IdentityProvider identityProvider = subject.getIdentityProvider("https://idp.surfnet.nl").get();
    List<ContactPerson> contactPersons = identityProvider.getContactPersons();
    assertEquals(3, contactPersons.size());
    assertEquals("john doe", identityProvider.getContactPerson(ContactPersonType.technical).getName());
    assertEquals("mary doe", identityProvider.getContactPerson(ContactPersonType.administrative).getName());
    assertEquals("steward doe", identityProvider.getContactPerson(ContactPersonType.help).getName());
  }

  @Test
  public void testGetAllServiceProvidersLinked() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders(
      "https://idp.diy.surfconext.nl/saml2/idp/metadata.php");
    assertEquals(66, serviceProviders.size());
  }

  @Test
  public void testGetServiceProvidersNotLinked() {
    ServiceProvider serviceProvider = subject.getServiceProvider(
      "https://eduproxy.localhost.surfconext.nl",
      EntityType.saml20_sp).get();
    assertFalse(serviceProvider.isLinked());
  }

  @Test
  public void testGetServiceProvidersLinked() {
    ServiceProvider serviceProvider = subject.getServiceProvider(
      "https://imogen.surfnet.nl/testsp/module.php/saml/sp/metadata.php/default-sp",
      EntityType.saml20_sp).get();
    assertTrue(serviceProvider.isLinked());
  }

  @Test
  public void testGetByInstitutionId() {
    List<IdentityProvider> identityProviders = subject.getInstituteIdentityProviders("surfconext");
    assertEquals(1, identityProviders.size());
  }

  @Test
  public void testGetLinkedIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getLinkedIdentityProviders(
      "https://manage.test2.surfconext.nl/shibboleth"
    );
    assertEquals(8, identityProviders.size());
  }

//  @Test
//  public void testGetLinkedServiceProviderIDs() {
//    List<String> ids = subject.getLinkedServiceProviderIDs("http://login.aai.braindrops.org/adfs/services/trust");
//    assertEquals(0, ids.size());
//  }
//
//  @Test
//  public void service_provider_should_have_policy_enforcement_descision_required() {
//    ServiceProvider serviceProvider = subject.getServiceProvider(
//      "https://profile.test2.surfconext.nl/authentication/metadata").get();
//
//    assertTrue(serviceProvider.isPolicyEnforcementDecisionRequired());
//  }
//
//  @Test
//  public void service_provider_should_not_have_policy_enforcement_descision_required() {
//    ServiceProvider sp = subject.getServiceProvider(
//      "https://engine.test2.surfconext.nl/authentication/sp/metadata"
//    ).get();
//
//    assertFalse(sp.isPolicyEnforcementDecisionRequired());
//  }
//
//  @Test
//  public void testSingleTenantNotLinked() {
//    ServiceProvider serviceProvider = subject.getServiceProvider("https://dummy.blackboard.nl/Single-tenant-service_op-aanvraag", null);
//    assertTrue(serviceProvider.isExampleSingleTenant());
//  }


}
