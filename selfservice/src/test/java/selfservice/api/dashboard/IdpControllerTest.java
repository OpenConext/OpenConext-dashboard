package selfservice.api.dashboard;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.standaloneSetup;
import static selfservice.api.dashboard.Constants.HTTP_X_IDP_ENTITY_ID;
import static selfservice.api.dashboard.RestDataFixture.coinUser;
import static selfservice.api.dashboard.RestDataFixture.idp;

import java.lang.reflect.Method;
import java.util.Optional;

import com.google.common.collect.ImmutableList;

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
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

import selfservice.domain.CoinUser;
import selfservice.filter.EnsureAccessToIdpFilter;
import selfservice.filter.SpringSecurityUtil;
import selfservice.sab.Sab;
import selfservice.service.IdentityProviderService;
import selfservice.util.CookieThenAcceptHeaderLocaleResolver;

@RunWith(MockitoJUnitRunner.class)
public class IdpControllerTest {

  private static final String FOO_IDP_ENTITY_ID = "foo";
  private static final String BAR_IDP_ENTITY_ID = "bar";

  @InjectMocks
  private IdpController controller;

  @Mock
  private IdentityProviderService idpServiceMock;

  @Mock
  private Sab sab;

  private MockMvc mockMvc;

  private CoinUser coinUser = coinUser("user", FOO_IDP_ENTITY_ID, BAR_IDP_ENTITY_ID);

  @Before
  public void setup() {
    controller.localeResolver = new CookieThenAcceptHeaderLocaleResolver();

    EnsureAccessToIdpFilter ensureAccessToIdp = new EnsureAccessToIdpFilter(idpServiceMock);

    mockMvc = standaloneSetup(controller)
      .setMessageConverters(new GsonHttpMessageConverter("", "", "", ""))
      .addFilter(ensureAccessToIdp, "/*")
      .setHandlerExceptionResolvers(createExceptionResolver())
      .build();

    SpringSecurityUtil.setAuthentication(coinUser);

    when(idpServiceMock.getAllIdentityProviders()).thenReturn(ImmutableList.of(idp(BAR_IDP_ENTITY_ID), idp(FOO_IDP_ENTITY_ID)));
    when(idpServiceMock.getIdentityProvider(FOO_IDP_ENTITY_ID)).thenReturn(Optional.of(idp(FOO_IDP_ENTITY_ID)));
  }

  @After
  public void after() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void returnsServerErrorOnFailure() throws Exception {
    when(sab.getPersonsInRoleForOrganization(anyString(), anyString())).thenThrow(new RuntimeException("Foo"));

    mockMvc.perform(get("/dashboard/api/idp/current/roles")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HTTP_X_IDP_ENTITY_ID, FOO_IDP_ENTITY_ID))
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
