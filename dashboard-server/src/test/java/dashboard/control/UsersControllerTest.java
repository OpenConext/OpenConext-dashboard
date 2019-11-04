package dashboard.control;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.NestedServletException;
import dashboard.domain.*;
import dashboard.domain.CoinAuthority.Authority;
import dashboard.filter.EnsureAccessToIdpFilter;
import dashboard.filter.SpringSecurityUtil;
import dashboard.manage.Manage;
import dashboard.service.ActionsService;
import dashboard.service.Services;
import dashboard.util.CookieThenAcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.*;

import static java.lang.String.format;
import static java.util.Collections.singletonList;
import static junit.framework.TestCase.assertTrue;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static dashboard.control.Constants.HTTP_X_IDP_ENTITY_ID;
import static dashboard.control.RestDataFixture.coinUser;
import static dashboard.control.RestDataFixture.idp;

@RunWith(MockitoJUnitRunner.class)
public class UsersControllerTest {

    private static final String FOO_IDP_ENTITY_ID = "foo";
    private static final String BAR_IDP_ENTITY_ID = "bar";
    private final CoinUser coinUser = coinUser("user", FOO_IDP_ENTITY_ID, BAR_IDP_ENTITY_ID);
    @InjectMocks
    private UsersController controller;
    @Mock
    private Manage manage;
    @Mock
    private Services services;
    @Mock
    private ActionsService actionsService;

    private MockMvc mockMvc;

    @Before
    public void setup() throws IOException {
        controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

        EnsureAccessToIdpFilter ensureAccessToIdp = new EnsureAccessToIdpFilter(manage);

        mockMvc = standaloneSetup(controller)
            .setMessageConverters(new GsonHttpMessageConverter(true))
            .addFilter(ensureAccessToIdp, "/*")
            .build();

        SpringSecurityUtil.setAuthentication(coinUser);

        when(manage.getIdentityProvider(anyString(), anyBoolean())).thenAnswer(answer -> Optional.of(idp((String) answer
            .getArguments()[0])));
        when(manage.getAllIdentityProviders()).thenReturn(ImmutableList.of(idp(BAR_IDP_ENTITY_ID), idp
            (FOO_IDP_ENTITY_ID)));
       when(services.getInstitutionalServicesForIdp("my-institution-id", Locale.ENGLISH)).thenReturn(singletonList(service()));

        RequestAttributes requestAttributes = new ServletRequestAttributes(new MockHttpServletRequest());
        RequestContextHolder.setRequestAttributes(requestAttributes);
    }

    private Service service() {
        Service service = new Service(1, "name", "logo", "website", "sp-entity-id");
        service.setInstitutionId("my-institution-id");
        return service;
    }

    @After
    public void after() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void returnsCurrentUser() throws Exception {
        mockMvc.perform(get("/dashboard/api/users/me")
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.attributeMap['name-id']").value(coinUser.getUid()))
            .andExpect(jsonPath("$.payload.uid").value(coinUser.getUid()));
    }

    @Test
    public void returnsIdps() throws Exception {
       coinUser.setAuthorities(Collections.singleton(new CoinAuthority(Authority.ROLE_DASHBOARD_SUPER_USER)));

        mockMvc.perform(get("/dashboard/api/users/super/idps")
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload.idps").exists())
            .andExpect(jsonPath("$.payload.roles").exists());
    }

    @Test
    public void adminUserCantAccessIdps() throws Exception {
        coinUser.setAuthorities(Collections.singleton(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN)));

        mockMvc.perform(get("/dashboard/api/users/super/idps")
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isForbidden());
    }

    @Test
    public void thatIdpCanBeSwitchedToEmpty() throws Exception {
        coinUser.setAuthorities(Sets.newHashSet(new CoinAuthority(Authority.ROLE_DASHBOARD_SUPER_USER), new
            CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN)));
        coinUser.setSwitchedToIdp(new IdentityProvider("idp-id", "idp-institution-id", "idp-name", 1L));

        mockMvc.perform(get("/dashboard/api/users/me/switch-to-idp")
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isNoContent());

        assertThat(coinUser.getAuthorities(), contains(new CoinAuthority(Authority.ROLE_DASHBOARD_SUPER_USER)));
        assertThat(coinUser.getSwitchedToIdp(), is(Optional.empty()));
    }

    @Test
    public void nonSuperUserCanSwitchIdpWithoutSpecifyingTheRole() throws Exception {
        coinUser.setAuthorities(Sets.newHashSet(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN)));
        coinUser.setSwitchedToIdp(new IdentityProvider("idp-id", "idp-institution-id", "idp-name", 1L));

        mockMvc.perform(get("/dashboard/api/users/me/switch-to-idp?idpId=" + BAR_IDP_ENTITY_ID)
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isNoContent());

        assertThat(coinUser.getAuthorities(), contains(new CoinAuthority(Authority.ROLE_DASHBOARD_ADMIN)));
        assertThat(coinUser.getSwitchedToIdp().get().getId(), is(BAR_IDP_ENTITY_ID));
    }

    @Test
    public void thatIdpCanBeSwitched() throws Exception {
        mockMvc.perform(get(format("/dashboard/api/users/me/switch-to-idp?idpId=%s&role=%s", BAR_IDP_ENTITY_ID,
            Authority.ROLE_DASHBOARD_ADMIN))
            .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
            .andExpect(status().isNoContent());
    }

    @Test
    public void cannotSwitchToIdpWithoutAccessToIt() throws Exception {
        try {
            mockMvc.perform(get(format("/dashboard/api/users/me/switch-to-idp?idpId=%s&role=%s", "no access",
                Authority.ROLE_DASHBOARD_ADMIN))
                .contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID));
            fail("expected SecurityException");
        } catch (NestedServletException e) {
            assertEquals(SecurityException.class, e.getRootCause().getClass());
        }
    }

    @Test
    public void aUserWithoutAnInstituionIdHasNoServices() throws Exception {
        CoinUser user = new CoinUser();
        user.setInstitutionId(null);

        SpringSecurityUtil.setAuthentication(user);

        mockMvc.perform(get("/dashboard/api/users/me/serviceproviders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload").isArray())
            .andExpect(jsonPath("$.payload").isEmpty());
    }

    @Test
    public void aUserWithAnInstitutionIdHasServices() throws Exception {
        CoinUser user = new CoinUser();
        user.setInstitutionId("my-institution-id");

        Service serviceWithInsitutionId = new Service();
        serviceWithInsitutionId.setInstitutionId("my-institution-id");
        Service serviceWithWrongInstitiontid = new Service();
        serviceWithWrongInstitiontid.setInstitutionId("wrong-institution-id");
        Service serviceWithoutAnInstitutionId = new Service();
        serviceWithoutAnInstitutionId.setInstitutionId(null);

        SpringSecurityUtil.setAuthentication(user);


        mockMvc.perform(get("/dashboard/api/users/me/serviceproviders"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.payload").isArray())
            .andExpect(jsonPath("$.payload", hasSize(1)))
            .andExpect(jsonPath("$.payload[0].institutionId", is("my-institution-id")));
    }

    @Test
    public void updateSettings() throws IOException {
        CoinUser user = new CoinUser();
        IdentityProvider idp = new IdentityProvider("id", "institutionId", "name", 1L);
        user.setIdp(idp);
        user.setInstitutionId(idp.getInstitutionId());
        SpringSecurityUtil.setAuthentication(user);

        Settings settings = new Settings();
        settings.setStateType(StateType.prodaccepted);

        List<ServiceProviderSettings> serviceProviderSettings = new ArrayList<>();
        ServiceProviderSettings serviceProviderSetting = new ServiceProviderSettings();
        serviceProviderSetting.setPublishedInEdugain(true);
        serviceProviderSetting.setSpEntityId("spEntityId");
        serviceProviderSettings.add(serviceProviderSetting);

        settings.setServiceProviderSettings(serviceProviderSettings);

        List<Service> servicesOfIdp = new ArrayList<>();

        Service service = new Service();
        service.setSpEntityId("spEntityId");
        servicesOfIdp.add(service);

        when(services.getInstitutionalServicesForIdp(idp.getInstitutionId(),Locale.ENGLISH)).thenReturn(servicesOfIdp);

        List<Change> changes = controller.getChanges(Locale.ENGLISH, settings, idp);
        assertEquals(2, changes.size());
        assertEquals("Change the attribute 'state' for 'id' from old value 'null' to new value 'prodaccepted'", changes.get(0).toString());
        assertEquals("Change the attribute 'coin:publish_in_edugain' for 'spEntityId' from old value 'false' to new value 'true'", changes.get(1).toString());
    }
}
