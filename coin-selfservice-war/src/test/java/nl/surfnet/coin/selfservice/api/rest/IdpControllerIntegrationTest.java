package nl.surfnet.coin.selfservice.api.rest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.filter.SpringSecurityUtil;
import nl.surfnet.coin.selfservice.interceptor.EnsureAccessToIdp;
import nl.surfnet.coin.selfservice.util.CookieThenAcceptHeaderLocaleResolver;
import nl.surfnet.sab.Sab;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;
import org.springframework.web.util.NestedServletException;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.String.format;
import static nl.surfnet.coin.selfservice.api.rest.Constants.HTTP_X_IDP_ENTITY_ID;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.coinUser;
import static nl.surfnet.coin.selfservice.api.rest.RestDataFixture.idp;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;

public class IdpControllerIntegrationTest {
  private static final String FOO_IDP_ENTITY_ID = "foo";
  private static final String BAR_IDP_ENTITY_ID = "bar";
  private MockMvc mockMvc;

  @InjectMocks
  private IdpController controller;

  private CoinUser coinUser;
  private List<InstitutionIdentityProvider> idps;

  @Mock
  private Csa csa;

  @Mock
  private Sab sab;

  @Before
  public void setup() {
    controller = new IdpController();
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    MockitoAnnotations.initMocks(this);

    EnsureAccessToIdp ensureAccessToIdp = new EnsureAccessToIdp();
    ensureAccessToIdp.setCsa(csa);
    this.mockMvc = standaloneSetup(controller)
      .setMessageConverters(new GsonHttpMessageConverter())
      .addInterceptors(ensureAccessToIdp)
      .setHandlerExceptionResolvers(createExceptionResolver())
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
  public void returnsServerErrorOnFailure() throws Exception {
    when(
      sab.getPersonsInRoleForOrganization(anyString(), anyString())).thenThrow(new RuntimeException("Foo"));

    this.mockMvc.perform(
      get(format("/idp/current/roles"))
        .contentType(MediaType.APPLICATION_JSON)
        .header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID)
    )
      .andExpect(status().isInternalServerError());
  }

  private ExceptionHandlerExceptionResolver createExceptionResolver() {
    ExceptionHandlerExceptionResolver exceptionResolver = new ExceptionHandlerExceptionResolver() {
      protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
        Method method = new ExceptionHandlerMethodResolver(ExceptionLogger.class).resolveMethod(exception);
        return new ServletInvocableHandlerMethod(new ExceptionLogger(), method);
      }
    };
    exceptionResolver.afterPropertiesSet();
    return exceptionResolver;
  }
}
