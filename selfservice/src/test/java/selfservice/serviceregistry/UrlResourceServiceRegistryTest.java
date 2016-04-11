package selfservice.serviceregistry;

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

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.*;

public class UrlResourceServiceRegistryTest {

  private static UrlResourceServiceRegistry subject;

  @Rule
  public WireMockRule wireMockRule = new WireMockRule(8889);

  @Before
  public void before() throws Exception {
    //wireMockRule can not be static
    if (subject == null) {
      String spResponse = IOUtils.toString(new ClassPathResource("service-registry-test/service-providers.json").getInputStream());
      stubFor(get(urlEqualTo("/sp")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(spResponse)));

      String idpResponse = IOUtils.toString(new ClassPathResource("service-registry-test/identity-providers.json").getInputStream());
      stubFor(get(urlEqualTo("/idp")).willReturn(aResponse().withStatus(200).withHeader("Content-Type", "application/json").withBody(idpResponse)));

      subject = new UrlResourceServiceRegistry("user", "password", "http://localhost:8889/idp", "http://localhost:8889/sp", 10, new ClassPathResource("dummy-single-tenants-services"));
    }
  }

  @Test
  public void testServiceProviders() throws IOException {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
    assertEquals(694 + 3, serviceProviders.size());
  }

  @Test
  public void testIdentityProviders() throws IOException {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(175, identityProviders.size());
  }

  @Test
  public void testGetSingleTenantService() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://google.dummy.sp").get();
    assertTrue(serviceProvider.isExampleSingleTenant());
  }

  @Test
  public void testGetServiceProvider() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://teams.surfconext.nl/shibboleth").get();
    assertFalse(serviceProvider.isExampleSingleTenant());
  }

  @Test
  public void testAllowedEntityIds() {
    IdentityProvider identityProvider = subject.getIdentityProvider("https://signon.rug.nl/nidp/saml2/metadata").get();
    Set<String> allowedEntityIds = identityProvider.getAllowedEntityIds();
    assertEquals(104, allowedEntityIds.size());
    assertFalse(identityProvider.isAllowedAll());
  }

  @Test
  public void testContactPersons() {
    IdentityProvider identityProvider = subject.getIdentityProvider("https://sso.han.nl/ssp").get();
    List<ContactPerson> contactPersons = identityProvider.getContactPersons();
    assertEquals(3, contactPersons.size());
    assertEquals("Patrick Honing", identityProvider.getContactPerson(ContactPersonType.technical).getName());
    assertEquals("Admin Admin", identityProvider.getContactPerson(ContactPersonType.administrative).getName());
    assertEquals("ICT Helpdesk", identityProvider.getContactPerson(ContactPersonType.help).getName());
  }

  @Test
  public void testGetAllServiceProvidersLinked() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders("https://sso.han.nl/ssp");
    assertEquals(271, serviceProviders.size());
  }

  @Test
  public void testGetServiceProvidersNotLinked() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://serviceregistry.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp", "https://www.onegini.me");
    assertFalse(serviceProvider.isLinked());
  }

  @Test
  public void testGetServiceProvidersLinked() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://conext.proteon.nl/shibboleth-sp", "https://conext.authenticatie.ru.nl/simplesaml/saml2/idp/metadata.php");
    assertTrue(serviceProvider.isLinked());
  }

  @Test
  public void testGetByInstitutionId() {
    List<IdentityProvider> identityProviders = subject.getInstituteIdentityProviders("SURFNET");
    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testGetLinkedIdentityProvidersAllowAll() {
    List<IdentityProvider> identityProviders = subject.getLinkedIdentityProviders("https://conext.proteon.nl/shibboleth-sp");
    assertEquals(11, identityProviders.size());
  }

  @Test
  public void testGetLinkedIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getLinkedIdentityProviders("https://publicapi.avans.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp");
    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testGetLinkedServiceProviderIDs() {
    List<String> ids = subject.getLinkedServiceProviderIDs("http://federatie.amc.nl/adfs/services/trust");
    assertEquals(656, ids.size());
  }

  @Test
  public void testSingleTenantNotLinked() {
    ServiceProvider serviceProvider = subject.getServiceProvider("https://bod.dummy.sp", null);
    assertTrue(serviceProvider.isExampleSingleTenant());
  }

  @Test
  public void testRefreshMetadataNotModified() {
    doRefresh(304);
  }

  @Test
  public void testRefreshMetadataWithException() {
    doRefresh(200);
  }

  private void doRefresh(int response) {
    stubFor(head(urlEqualTo("/sp")).willReturn(aResponse().withStatus(response)));
    stubFor(head(urlEqualTo("/idp")).willReturn(aResponse().withStatus(response)));

    stubFor(get(urlEqualTo("/sp")).willReturn(aResponse().withStatus(500)));

    subject.refreshMetaData();
  }

}
