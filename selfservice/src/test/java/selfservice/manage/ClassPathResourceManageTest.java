package selfservice.manage;

import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.util.List;

import static org.junit.Assert.*;

//All functional tests reside in UrlResourceManageTest
public class ClassPathResourceManageTest {

  private ClassPathResourceManage subject =
    new ClassPathResourceManage();

  @Test
  public void testIdentityProviders() {
    List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
    assertEquals(11, identityProviders.size());
  }

  @Test
  public void testServiceProviders() {
    List<ServiceProvider> serviceProviders = subject.getAllServiceProviders("TODO");
    assertEquals(66, serviceProviders.size());

    ServiceProvider surfcloud = serviceProviders.stream().filter(sp -> sp.getId()
      .equals("https://mailer.pt-75.utr.surfcloud.nl")).findFirst().get();
    assertEquals(17, surfcloud.getArp().getAttributes().size());

    ServiceProvider blackboard = serviceProviders.stream().filter(sp -> sp.getId()
      .equals("https://dummy.blackboard.nl/Single-tenant-service_op-aanvraag")).findFirst().get();
    assertEquals(4, blackboard.getArp().getAttributes().size());
  }
}
