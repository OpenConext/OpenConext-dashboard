package dashboard.control;

import com.google.common.collect.ImmutableMap;
import dashboard.domain.CoinAuthority;
import dashboard.domain.CoinUser;
import dashboard.domain.Policy;
import dashboard.domain.ServiceProvider;
import dashboard.filter.SpringSecurityUtil;
import dashboard.mail.MailBox;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.pdp.PdpService;
import dashboard.pdp.PolicyNameNotUniqueException;
import dashboard.util.CookieThenAcceptHeaderLocaleResolver;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

@RunWith(MockitoJUnitRunner.class)
public class PoliciesControllerTest {

    @InjectMocks
    private PoliciesController controller;

    @Mock
    private PdpService pdpServiceMock;

    @Mock
    private Manage manageMock;

    @Mock
    private MailBox mailBox;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

        this.mockMvc = standaloneSetup(controller)
                .dispatchOptions(true).build();
    }

    @Test
    public void whenPdpIsAvailablePoliciesAllowsGet() throws Exception {
        when(pdpServiceMock.isAvailable()).thenReturn(true);

        mockMvc.perform(options("/dashboard/api/policies"))
                .andExpect(header().string("Allow", containsString("GET")))
                .andExpect(status().isOk());
    }

    @Test
    public void whenPdpIsNotAvailablePoliciesDoesNotAllowAnything() throws Exception {
        when(pdpServiceMock.isAvailable()).thenReturn(false);

        mockMvc.perform(options("/dashboard/api/policies"))
                .andExpect(header().string("Allow", ""))
                .andExpect(status().isOk());
    }

    @Test
    public void creatingAPdpWithADuplicateName() throws Exception {
        CoinUser user = RestDataFixture.coinUser("henk");
        user.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

        SpringSecurityUtil.setAuthentication(user);

        when(pdpServiceMock.create(any(Policy.class))).thenThrow(new PolicyNameNotUniqueException("errormessage"));
        when(manageMock.getServiceProvider("mockServiceProviderId", EntityType.saml20_sp, false)).thenReturn(Optional.of(new ServiceProvider(ImmutableMap.of("entityid", "mockServiceProviderId", "eid", 1L))));

        mockMvc.perform(post("/dashboard/api/policies")
                        .contentType(APPLICATION_JSON)
                        .content("{\"name\": \"duplicate\", \"serviceProviderId\": \"mockServiceProviderId\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void onlyADashboardAdminCanCreateAPolicy() throws Exception {
        SpringSecurityUtil.setAuthentication(RestDataFixture.coinUser("henk"));

        mockMvc.perform(post("/dashboard/api/policies").contentType(APPLICATION_JSON).content("{\"name\": \"my first rule\"}"))
                .andExpect(status().isForbidden());
    }
}
