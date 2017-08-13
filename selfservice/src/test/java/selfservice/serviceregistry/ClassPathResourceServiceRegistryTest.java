package selfservice.serviceregistry;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.util.List;

import static org.junit.Assert.*;

//All functional tests reside in UrlResourceServiceRegistryTest
public class ClassPathResourceServiceRegistryTest {

  private ClassPathResourceServiceRegistry subject =
    new ClassPathResourceServiceRegistry(true, new ClassPathResource("dummy-single-tenants-services"));

  @Test
  public void testIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(9, identityProviders.size());
  }

  @Test
  public void testServiceProviders() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
    assertEquals(34, serviceProviders.size());

    ServiceProvider serviceProvider = serviceProviders.stream().filter(sp -> sp.getId().equals("https://teams.test.surfconext.nl/shibboleth")).findFirst().get();
    int size = serviceProvider.getArp().getAttributes().size();
    assertEquals(4, size);
  }
}
