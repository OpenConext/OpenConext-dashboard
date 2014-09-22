package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import java.util.Arrays;
import java.util.List;

import static java.lang.String.format;
import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.coinUser;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.idp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UsersControllerIntegrationTest {
  private static final String FOO_IDP_ENTITY_ID = "foo";
  private static final String BAR_IDP_ENTITY_ID = "bar";
  private MockMvc mockMvc;

  @InjectMocks
  private UsersController controller;

  private CoinUser coinUser;
  private List<InstitutionIdentityProvider> idps;

  @Mock
  private Csa csa;

  @Before
  public void setup() {
    controller = new UsersController();
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    MockitoAnnotations.initMocks(this);

    EnsureAccessToIdp ensureAccessToIdp = new EnsureAccessToIdp();
    ensureAccessToIdp.setCsa(csa);
    this.mockMvc = standaloneSetup(controller)
      .setMessageConverters(new GsonHttpMessageConverter())
      .addInterceptors(ensureAccessToIdp)
      .build();
    coinUser = coinUser("user", FOO_IDP_ENTITY_ID, BAR_IDP_ENTITY_ID);
    SpringSecurityUtil.setAuthentication(coinUser);
    idps = Arrays.asList(idp(BAR_IDP_ENTITY_ID), idp(FOO_IDP_ENTITY_ID));
    when(csa.getAllInstitutionIdentityProviders()).thenReturn(idps);
  }

  @After
  public void after() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void returnsCurrentUser() throws Exception {
    this.mockMvc.perform(
      get(format("/users/me")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
    )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload.uid").value(coinUser.getUid()));
  }

  @Test
  public void returnsIdps() throws Exception {

    this.mockMvc.perform(
      get(format("/users/super/idps")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
    )
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.payload.idps").exists())
      .andExpect(jsonPath("$.payload.roles").exists());
  }

  @Test
  public void thatIdpCanBeSwitched() throws Exception {

    this.mockMvc.perform(
      get(format("/users/me/switch-to-idp?idpId=%s", BAR_IDP_ENTITY_ID)).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
    )
      .andExpect(status().isOk());

  }

  @Test
  public void cannotSwitchToIdpWithoutAccessToIt() throws Exception {
    try {
      this.mockMvc.perform(
        get(format("/users/me/switch-to-idp?idpId=%s", "no access")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
      );
      fail("expected SecurityException");
    } catch (NestedServletException e) {
      assertEquals(SecurityException.class, e.getRootCause().getClass());
    }

  }
}
