package selfservice.api.dashboard;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.options;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

import java.util.Optional;

import com.google.common.collect.ImmutableMap;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.test.web.servlet.MockMvc;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.Policy;
import selfservice.domain.ServiceProvider;
import selfservice.filter.SpringSecurityUtil;
import selfservice.pdp.PdpService;
import selfservice.pdp.PolicyNameNotUniqueException;
import selfservice.service.EmailService;
import selfservice.serviceregistry.ServiceRegistry;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

@RunWith(MockitoJUnitRunner.class)
public class PoliciesControllerTest {

  @InjectMocks
  private PoliciesController controller;

  @Mock
  private PdpService pdpServiceMock;

  @Mock
  private EmailService emailServiceMock;

  @Mock
  private ServiceRegistry serviceRegistryMock;

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
    controller.policiesEnabled = true;

    mockMvc.perform(options("/dashboard/api/policies"))
      .andExpect(header().string("Allow", containsString("GET")))
      .andExpect(status().isOk());
  }

  @Test
  public void whenPdpIsNotAvailablePoliciesDoesNotAllowAnything() throws Exception {
    when(pdpServiceMock.isAvailable()).thenReturn(false);
    controller.policiesEnabled = true;

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
    when(serviceRegistryMock.getServiceProvider("mockServiceProviderId")).thenReturn(Optional.of(new ServiceProvider(ImmutableMap.of("entityid", "mockServiceProviderId"))));

    mockMvc.perform(post("/dashboard/api/policies")
        .contentType(APPLICATION_JSON)
        .content("{\"name\": \"duplicate\", \"serviceProviderId\": \"mockServiceProviderId\"}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  public void whenCreatingAPolicyForAServiceProviderWithoutPolicyEnforcementAnEmailShouldBeSend() throws Exception {
    CoinUser user = RestDataFixture.coinUser("henk");
    user.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

    SpringSecurityUtil.setAuthentication(user);

    when(pdpServiceMock.create(any(Policy.class))).thenReturn(new Policy());
    when(serviceRegistryMock.getServiceProvider("mockServiceProviderId"))
      .thenReturn(Optional.of(new ServiceProvider(ImmutableMap.of("entityid", "mockServiceProviderId", "coin:policy_enforcement_decision_required", "0"))));

    mockMvc.perform(post("/dashboard/api/policies")
        .contentType(APPLICATION_JSON)
        .content("{\"name\": \"my-rule\", \"serviceProviderId\": \"mockServiceProviderId\", \"serviceProviderName\": \"mockServiceProvider\"}"))
      .andExpect(status().isOk());

    verify(emailServiceMock).sendMail(eq("no-reply@surfconext.nl"), eq("Nieuwe autorisatieregel mockServiceProvider"), anyString());
  }

  @Test
  public void whenCreatingAPolicyForAServiceProviderWithPolicyEnforcementNoEmailShouldBeSend() throws Exception {
    CoinUser user = RestDataFixture.coinUser("henk");
    user.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

    SpringSecurityUtil.setAuthentication(user);

    when(pdpServiceMock.create(any(Policy.class))).thenReturn(new Policy());
    when(serviceRegistryMock.getServiceProvider("mockServiceProviderId"))
      .thenReturn(Optional.of(new ServiceProvider(ImmutableMap.of("entityid", "mockServiceProviderId", "coin:policy_enforcement_decision_required", "1"))));

    mockMvc.perform(post("/dashboard/api/policies")
        .contentType(APPLICATION_JSON)
        .content("{\"name\": \"my-rule\", \"serviceProviderId\": \"mockServiceProviderId\"}"))
      .andExpect(status().isOk());

    verifyZeroInteractions(emailServiceMock);
  }

  @Test
  public void onlyADashboardAdminCanCreateAPolicy() throws Exception {
    SpringSecurityUtil.setAuthentication(RestDataFixture.coinUser("henk"));

    mockMvc.perform(post("/dashboard/api/policies").contentType(APPLICATION_JSON).content("{\"name\": \"my first rule\"}"))
      .andExpect(status().isForbidden());
  }
}
