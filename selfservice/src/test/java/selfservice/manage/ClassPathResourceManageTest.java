package selfservice.manage;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.util.List;

import static org.junit.Assert.*;

//All functional tests reside in UrlResourceManageTest
public class ClassPathResourceManageTest {

  private ClassPathResourceManage subject = new ClassPathResourceManage();

  @Test
  public void testIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(194, identityProviders.size());
  }

  @Test
  public void testServiceProviders() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
    assertEquals(1279, serviceProviders.size());

    ServiceProvider surfcloud = serviceProviders.stream().filter(sp -> sp.getId()
      .equals("https://teams.surfconext.nl/shibboleth")).findFirst().get();
    assertEquals(5, surfcloud.getArp().getAttributes().size());

  }
}
