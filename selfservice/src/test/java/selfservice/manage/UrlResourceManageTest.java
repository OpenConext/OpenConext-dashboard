package selfservice.manage;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import selfservice.domain.IdentityProvider;
import selfservice.domain.ServiceProvider;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class UrlResourceManageTest {

    private static UrlResourceManage subject;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8889);

    @Before
    public void before() throws Exception {
        String spResponse = IOUtils.toString(new ClassPathResource("manage/service-providers.json").getInputStream());
        stubFor(post(urlEqualTo("/manage/api/internal/search/saml20_sp")).willReturn(aResponse().withStatus(200)
            .withHeader("Content-Type", "application/json").withBody(spResponse)));

        String idpResponse = IOUtils.toString(new ClassPathResource("manage/identity-providers.json").getInputStream());
        stubFor(post(urlEqualTo("/manage/api/internal/search/saml20_idp")).willReturn(aResponse().withStatus(200)
            .withHeader("Content-Type", "application/json").withBody(idpResponse)));

        String singleTenantTemplateResponse = IOUtils.toString(new ClassPathResource("manage/single-tenants.json")
            .getInputStream());
        stubFor(post(urlEqualTo("/manage/api/internal/search/single_tenant_template")).willReturn(aResponse()
            .withStatus(200)
            .withHeader("Content-Type", "application/json").withBody(singleTenantTemplateResponse)));

        subject = new UrlResourceManage("user", "password", "http://localhost:8889");
    }

    @Test
    public void testApi() {
        List<ServiceProvider> serviceProviders = subject.getAllServiceProviders();
        assertEquals(1280, serviceProviders.size());

        List<IdentityProvider> identityProviders = subject.getAllIdentityProviders();
        assertEquals(194, identityProviders.size());

        ServiceProvider serviceProvider = subject.getServiceProvider(
            "https://dummy.crosscampus.canon.nl/Single-tenant-service_op-aanvraag", EntityType.single_tenant_template).get();
        assertTrue(serviceProvider.isExampleSingleTenant());

        serviceProvider = subject.getServiceProvider("https://teams.surfconext.nl/shibboleth",
            EntityType.saml20_sp).get();
        assertFalse(serviceProvider.isExampleSingleTenant());

    }


}
