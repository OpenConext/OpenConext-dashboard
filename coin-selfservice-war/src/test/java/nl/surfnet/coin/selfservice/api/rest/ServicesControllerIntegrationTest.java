package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.filter.SpringSecurityUtil;
import nl.surfnet.coin.selfservice.interceptor.EnsureAccessToIdp;
import nl.surfnet.coin.selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;
import org.surfnet.cruncher.Cruncher;
import org.surfnet.cruncher.model.SpStatistic;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class ServicesControllerIntegrationTest {
  public static final String IDP_ENTITY_ID = "foo";
  public static final String SP_ENTITY_ID = "bar";
  private MockMvc mockMvc;
  private Date statisticDate = new Date();

  @InjectMocks
  private ServicesController controller;

  @Mock
  private Csa csa;

  private List<Service> services;
  private List<SpStatistic> spStatistics;
  private Service service;
  private CoinUser coinUser;

  @Before
  public void setup() {
    controller = new ServicesController();
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    MockitoAnnotations.initMocks(this);
    service = serviceWithSpEntityId(SP_ENTITY_ID);
    services = asList(service);
    spStatistics = asList(spStatisticFor(SP_ENTITY_ID, statisticDate.getTime()));
    EnsureAccessToIdp ensureAccessToIdp = new EnsureAccessToIdp();
    ensureAccessToIdp.setCsa(csa);

    this.mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJacksonHttpMessageConverter())
      .addInterceptors(ensureAccessToIdp)
      .build();
    coinUser = coinUser("user");
    InstitutionIdentityProvider institutionIdentityProvider = new InstitutionIdentityProvider(IDP_ENTITY_ID, "name", "institution id");
    coinUser.addInstitutionIdp(institutionIdentityProvider);

    SpringSecurityUtil.setAuthentication(coinUser);
    when(csa.getAllInstitutionIdentityProviders()).thenReturn(Arrays.asList(institutionIdentityProvider));
  }

  @After
  public void after() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void thatAllServicesAreReturned() throws Exception {
    when(csa.getServicesForIdp(IDP_ENTITY_ID)).thenReturn(services);

    this.mockMvc.perform(
      get("/services").contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
    )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload").isArray())
      .andExpect(jsonPath("$.payload[0].name").value(service.getName()))
    ;
  }

  @Test
  public void failsWhenUserHasNoAccessToIdp() throws Exception {
    try {
      this.mockMvc.perform(
        get(format("/services")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, "no access")
      );
      fail("expected SecurityException");
    } catch (NestedServletException e) {
      assertEquals(SecurityException.class, e.getRootCause().getClass());
    }
  }

  @Test
  public void thatALinkRequestCanBeMade() throws Exception {
    coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

    this.mockMvc.perform(
      post("/services/id/" + service.getId() + "/connect")
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
      post("/services/id/" + service.getId() + "/disconnect")
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
      post("/services/id/" + service.getId() + "/connect")
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
      post("/services/id/" + service.getId() + "/connect")
        .contentType(MediaType.APPLICATION_JSON)
        .param("spEntityId", SP_ENTITY_ID)
        .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
    )
      .andExpect(status().isForbidden());
  }
}
