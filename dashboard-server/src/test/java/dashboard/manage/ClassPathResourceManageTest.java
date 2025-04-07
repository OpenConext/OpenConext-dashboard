package dashboard.manage;

import dashboard.domain.IdentityProvider;
import dashboard.domain.ServiceProvider;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

//All functional tests reside in UrlResourceManageTest
public class ClassPathResourceManageTest {

    private ClassPathResourceManage subject = new ClassPathResourceManage();

    @Test
    public void testIdentityProviders() {
        List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
        assertEquals(1, identityProviders.size());
    }

    @Test
    public void testServiceProviders() {
        List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
        assertEquals(3, serviceProviders.size());

        ServiceProvider serviceProvider = serviceProviders.stream().filter(sp -> sp.getId()
                .equals("playground_client")).findFirst().get();
        assertEquals(0, serviceProvider.getArp().getAttributes().size());

    }
}
