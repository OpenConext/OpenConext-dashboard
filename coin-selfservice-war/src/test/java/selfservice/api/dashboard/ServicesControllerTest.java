package selfservice.api.dashboard;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;
import static selfservice.api.dashboard.RestDataFixture.coinUser;
import static selfservice.api.dashboard.RestDataFixture.serviceWithSpEntityId;

import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;

import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;
import selfservice.filter.EnsureAccessToIdpFilter;
import selfservice.filter.SpringSecurityUtil;
import selfservice.service.Csa;
import selfservice.service.IdentityProviderService;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

@RunWith(MockitoJUnitRunner.class)
public class ServicesControllerTest {

  public static final String IDP_ENTITY_ID = "foo";
  public static final String SP_ENTITY_ID = "bar";

  @InjectMocks
  private ServicesController controller;

  @Mock
  private IdentityProviderService idpServiceMock;

  @Mock
  private Csa csaMock;

  private MockMvc mockMvc;

  private List<Service> services;
  private Service service;
  private CoinUser coinUser = coinUser("user");

  @Before
  public void setup() {
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    service = serviceWithSpEntityId(SP_ENTITY_ID);
    services = asList(service);
    EnsureAccessToIdpFilter ensureAccessToIdp = new EnsureAccessToIdpFilter(idpServiceMock);

    mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJackson2HttpMessageConverter())
      .addFilter(ensureAccessToIdp, "/*")
      .build();

    IdentityProvider institutionIdentityProvider = new IdentityProvider(IDP_ENTITY_ID, "institution id", "name");

    coinUser.addInstitutionIdp(institutionIdentityProvider);
    coinUser.setIdp(institutionIdentityProvider);

    SpringSecurityUtil.setAuthentication(coinUser);

    when(idpServiceMock.getIdentityProvider(anyString())).thenReturn(Optional.empty());
    when(idpServiceMock.getIdentityProvider(IDP_ENTITY_ID)).thenReturn(Optional.of(institutionIdentityProvider));
  }

  @After
  public void after() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void thatAllServicesAreReturned() throws Exception {
    when(csaMock.getServicesForIdp(IDP_ENTITY_ID)).thenReturn(services);

    this.mockMvc.perform(get("/dashboard/api/services")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].name").value(service.getName()))
    ;
  }

  @Test(expected = SecurityException.class)
  public void failsWhenUserHasNoAccessToIdp() throws Exception {
    this.mockMvc.perform(get(format("/dashboard/api/services")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, "no access"));
  }

  @Test
  public void thatALinkRequestCanBeMade() throws Exception {
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

    this.mockMvc.perform(
      post("/dashboard/api/services/id/" + service.getId() + "/connect")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
        .param("spEntityId", SP_ENTITY_ID)
    )
      .andExpect(status().isOk());
  }

  @Test
  public void thatADisconnectRequestCanBeMade() throws Exception {
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

    this.mockMvc.perform(
      post("/dashboard/api/services/id/" + service.getId() + "/disconnect")
        .contentType(MediaType.APPLICATION_JSON)
        .param("spEntityId", SP_ENTITY_ID)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
    )
      .andExpect(status().isOk());
  }

  @Test
  public void thatALinkRequestCantBeMadeByASuperUser() throws Exception {
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER));

    this.mockMvc.perform(
      post("/dashboard/api/services/id/" + service.getId() + "/connect")
        .contentType(MediaType.APPLICATION_JSON)
        .param("spEntityId", SP_ENTITY_ID)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
    )
      .andExpect(status().isForbidden());
  }

  @Test
  public void thatALinkRequestCantBeMadeByADashboardViewer() throws Exception {
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER));

    this.mockMvc.perform(
      post("/dashboard/api/services/id/" + service.getId() + "/connect")
        .contentType(MediaType.APPLICATION_JSON)
        .param("spEntityId", SP_ENTITY_ID)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
    )
      .andExpect(status().isForbidden());
  }
}
