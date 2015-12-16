package selfservice.util;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import selfservice.domain.LicenseContactPerson;
import selfservice.util.LicenseContactPersonService;

import java.util.List;

import static org.junit.Assert.*;

public class LicenseContactPersonServiceTest {

  private LicenseContactPersonService subject = new LicenseContactPersonService(new ClassPathResource("license_contact_persons_surfmarket.csv"));

  @Test
  public void test_parsing() throws Exception {
    subject.onApplicationEvent(null);
    assertEquals(123, subject.getPersons().size());
    List<LicenseContactPerson> licenseContactPersons = subject.licenseContactPersons("https://idservice.zuyd.nl/nidp/saml2/metadata");
    licenseContactPersons.forEach(person -> assertTrue(person.isReachable()));
    assertEquals(1, licenseContactPersons.size());
    LicenseContactPerson person = licenseContactPersons.get(0);
    assertEquals("Andre Hochstenbach", person.getName());
    assertEquals("+31 (0)45 400 6136", person.getPhone());
    assertEquals("andre.hochstenbach@zuyd.nl", person.getEmail());
  }

  @Test
  public void is_reachable() {
    LicenseContactPerson person = new LicenseContactPerson("", "", "", "idp");
    assertFalse(person.isReachable());
  }
}
