package selfservice.api.dashboard;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import selfservice.domain.Action;
import selfservice.domain.CoinAuthority;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;
import selfservice.filter.EnsureAccessToIdpFilter;
import selfservice.filter.SpringSecurityUtil;
import selfservice.manage.EntityType;
import selfservice.manage.Manage;
import selfservice.service.ActionsService;
import selfservice.service.Services;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;
import static selfservice.api.dashboard.EnrichJson.FILTERED_USER_ATTRIBUTES;
import static selfservice.api.dashboard.RestDataFixture.coinUser;
import static selfservice.api.dashboard.RestDataFixture.serviceWithSpEntityId;

@RunWith(MockitoJUnitRunner.class)
public class ServicesControllerTest {

    public static final String IDP_ENTITY_ID = "foo";
    public static final String SP_ENTITY_ID = "bar";
    private final CoinUser coinUser = coinUser("user");
    private final Service service = serviceWithSpEntityId(SP_ENTITY_ID);
    private final List<Service> services = asList(service);
    @InjectMocks
    private ServicesController controller;
    @Mock
    private Manage manageMock;
    @Mock
    private Services servicesMock;
    @Mock
    private ActionsService actionsServiceMock;
    private MockMvc mockMvc;

    @Before
    public void setup() throws IOException {
        controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

        EnsureAccessToIdpFilter ensureAccessToIdp = new EnsureAccessToIdpFilter(manageMock);

        mockMvc = standaloneSetup(controller)
            .setMessageConverters(new GsonHttpMessageConverter("http:://example.com",
                "oauth/authorize.php", "stats-client-id", "stats-scope",
                "stats-redirect", true))
            .addFilter(ensureAccessToIdp, "/*")
            .build();

        IdentityProvider institutionIdentityProvider = new IdentityProvider(IDP_ENTITY_ID, "institution id", "name",
            1L);

        coinUser.addInstitutionIdp(institutionIdentityProvider);
        coinUser.setIdp(institutionIdentityProvider);

        SpringSecurityUtil.setAuthentication(coinUser);

        when(manageMock.getIdentityProvider(anyString())).thenReturn(Optional.empty());
        when(manageMock.getIdentityProvider(IDP_ENTITY_ID)).thenReturn(Optional.of(institutionIdentityProvider));
        when(servicesMock.getServicesForIdp(IDP_ENTITY_ID, Locale.ENGLISH)).thenReturn(services);
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void thatAllServicesAreReturned() throws Exception {
        when(servicesMock.getServicesForIdp(IDP_ENTITY_ID, Locale.ENGLISH)).thenReturn(services);

        this.mockMvc.perform(get("/dashboard/api/services")
            .contentType(MediaType.APPLICATION_JSON)
            .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.apps").isArray())
            .andExpect(jsonPath("$.payload.apps[0].name").value(service.getName()));
    }

    @Test
    public void retrieveAService() throws Exception {
        Service service = new Service(11L, "service-name", "http://logo", "http://website", SP_ENTITY_ID);

        when(servicesMock.getServiceById(IDP_ENTITY_ID, 11L, EntityType.saml20_sp, Locale.ENGLISH))
            .thenReturn(Optional.of(service));

        this.mockMvc.perform(get("/dashboard/api/services/detail?spId=" + 11L + "&entityType=" +
            EntityType.saml20_sp)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.name", is("service-name")))
            .andExpect(jsonPath("$.payload.id", is(11)));
    }

    @Test
    public void retrieveAServiceShouldBeEnriched() throws Exception {
        Service service = new Service(11L, "service-name", "http://logo", "http://website", SP_ENTITY_ID);

        when(servicesMock.getServiceById(IDP_ENTITY_ID, 11L, EntityType.saml20_sp, Locale.ENGLISH))
            .thenReturn(Optional.of(service));

        this.mockMvc.perform(get("/dashboard/api/services/detail?spId=" + 11L + "&entityType=" +
            EntityType.saml20_sp)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload." + FILTERED_USER_ATTRIBUTES).isArray());
    }

    @Test
    public void retrieveANonExistingService() throws Exception {
        when(servicesMock.getServiceById(IDP_ENTITY_ID, 999L, EntityType.saml20_sp, Locale.ENGLISH))
            .thenReturn(Optional.empty());

        this.mockMvc.perform(get("/dashboard/api/services/detail?spId=999&entityType=" + EntityType.saml20_sp)
            .contentType(MediaType.APPLICATION_JSON)
            .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isNotFound());
    }

    @Test(expected = SecurityException.class)
    public void failsWhenUserHasNoAccessToIdp() throws Exception {
        this.mockMvc.perform(get("/dashboard/api/services").contentType(MediaType.APPLICATION_JSON).header
            (HTTP_X_IDP_ENTITY_ID, "no access"));
    }

    @Test
    public void thatALinkRequestCanBeMade() throws Exception {
        coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

        Action expectedAction = Action.builder()
            .type(Action.Type.LINKREQUEST)
            .userName(coinUser.getUsername())
            .spId(SP_ENTITY_ID)
            .idpId(IDP_ENTITY_ID).build();

        when(actionsServiceMock.create(expectedAction, Collections.emptyList())).thenAnswer(invocation -> invocation
            .getArguments()[0]);

        this.mockMvc.perform(
            post("/dashboard/api/services/connect")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
                .param("spEntityId", SP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("payload.spId", is(SP_ENTITY_ID)));
    }

    @Test
    public void thatADisconnectRequestCanBeMade() throws Exception {
        coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN));

        when(actionsServiceMock.create(any(), any())).thenAnswer(invocation -> invocation.getArguments()[0]);

        this.mockMvc.perform(
            post("/dashboard/api/services/disconnect")
                .contentType(MediaType.APPLICATION_JSON)
                .param("spEntityId", SP_ENTITY_ID)
                .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("payload.spId", is(SP_ENTITY_ID)));
    }

    @Test
    public void thatALinkRequestCantBeMadeByASuperUser() throws Exception {
        coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_SUPER_USER));

        this.mockMvc.perform(
            post("/dashboard/api/services/connect")
                .contentType(MediaType.APPLICATION_JSON)
                .param("spEntityId", SP_ENTITY_ID)
                .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatALinkRequestCantBeMadeByADashboardViewer() throws Exception {
        coinUser.addAuthority(new CoinAuthority(CoinAuthority.Authority.ROLE_DASHBOARD_VIEWER));

        this.mockMvc.perform(
            post("/dashboard/api/services/connect")
                .contentType(MediaType.APPLICATION_JSON)
                .param("spEntityId", SP_ENTITY_ID)
                .header(HTTP_X_IDP_ENTITY_ID, IDP_ENTITY_ID)
        )
            .andExpect(status().isForbidden());
    }
}
