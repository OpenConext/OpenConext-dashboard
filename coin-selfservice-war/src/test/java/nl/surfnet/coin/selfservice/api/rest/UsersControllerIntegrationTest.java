package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.filter.SpringSecurityUtil;
import nl.surfnet.coin.selfservice.interceptor.EnsureCurrentIdpSet;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.util.NestedServletException;

import static java.lang.String.format;
import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.coinUser;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class UsersControllerIntegrationTest {
  private static final String FOO_IDP_ENTITY_ID = "foo";
  private static final String BAR_IDP_ENTITY_ID = "bar";
  private MockMvc mockMvc;

  private UsersController controller;
  private CoinUser coinUser;

  @Before
  public void setup() {
    controller = new UsersController();
    this.mockMvc = standaloneSetup(controller)
      .setMessageConverters(new MappingJacksonHttpMessageConverter())
      .addInterceptors(new EnsureCurrentIdpSet())
      .build();
    coinUser = coinUser("user", FOO_IDP_ENTITY_ID, BAR_IDP_ENTITY_ID);
    SpringSecurityUtil.setAuthentication(coinUser);
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
  public void thatIdpCanBeSwitched() throws Exception {
    this.mockMvc.perform(
      get(format("/users/me/switch-to-idp/%s", BAR_IDP_ENTITY_ID)).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
    )
      .andExpect(status().isOk())
      .andExpect(header().string(HTTP_X_IDP_ENTITY_ID, BAR_IDP_ENTITY_ID))
    ;

  }

  @Test
  public void cannotSwitchToIdpWithoutAccessToIt() throws Exception {
    try {
      this.mockMvc.perform(
        get(format("/users/me/switch-to-idp/%s", "no access")).contentType(MediaType.APPLICATION_JSON).header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
      );
      fail("expected SecurityException");
    } catch (NestedServletException e) {
      assertEquals(SecurityException.class, e.getRootCause().getClass());
    }

  }
}
