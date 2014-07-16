/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package nl.surfnet.coin.selfservice.interceptor;

import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_APPLY_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.TOKEN_CHECK;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_DASHBOARD_ADMIN;
import static org.junit.Assert.*;

import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

/**
 * AuthorityScopeInterceptorTest.java
 * 
 */
public class AuthorityScopeInterceptorTest {

  @InjectMocks
  private AuthorityScopeInterceptor interceptor;

  @Mock
  private Csa csa;

  @Before
  public void setUp() throws Exception {
    interceptor = new AuthorityScopeInterceptor();
    MockitoAnnotations.initMocks(this);
  }



  @Test
  public void test_power_user_may_see_technical_mail_address() throws Exception {
    ModelAndView modelAndView = new ModelAndView();

    CoinUser user = coinUser(ROLE_DASHBOARD_ADMIN);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    Service sp = buildService();
    modelAndView.addObject(BaseController.SERVICE, sp);

    interceptor.postHandle(new MockHttpServletRequest(), null, null, modelAndView);

    String technicalSupportMail = sp.getSupportMail();
    assertNotNull(technicalSupportMail);

    // also allowed
    Map<String, Object> model = modelAndView.getModel();
    assertTrue((Boolean) model.get(SERVICE_APPLY_ALLOWED));

  }

  @Test
  public void token_session_does_not_equal_request_param_token() throws Exception {
    ModelAndView modelAndView = buildSecurityContext(ROLE_DASHBOARD_ADMIN);

    MockHttpServletRequest request = new MockHttpServletRequest();
    interceptor.postHandle(request, null, null, modelAndView);

    // first check if the token is generated and stored in session and modelMap
    String token = (String) modelAndView.getModelMap().get(TOKEN_CHECK);
    assertNotNull(token);

    String sessionToken = (String) request.getSession(false).getAttribute(TOKEN_CHECK);
    assertNotNull(token);
    assertEquals(token, sessionToken);

    // now check if the prehandle checks the token if the method is a POST
    request = new MockHttpServletRequest();
    request.setMethod(RequestMethod.POST.name());
    try {
      interceptor.preHandle(request, null, null);
      fail("Expected security exception");
    } catch (Exception e) {
    }

    // now check if the prehandle checks the token if the method is a POST
    request = new MockHttpServletRequest();
    request.addParameter(TOKEN_CHECK, sessionToken);
    request.getSession().setAttribute(TOKEN_CHECK, sessionToken);
    request.setMethod(RequestMethod.POST.name());

    assertTrue(interceptor.preHandle(request, null, null));

  }

  private CoinUser coinUser(Authority... authorities) {
    CoinUser coinUser = new CoinUser();
    for (Authority authority : authorities) {
      coinUser.addAuthority(new CoinAuthority(authority));
    }
    return coinUser;
  }

  private Service buildService() {
    Service service = new Service(1L, "", "", "", false, null, "http://mock-idp");
    service.setSupportMail("somesupportmail");
    service.setAvailableForEndUser(true);
    return service;
  }

  private ModelAndView buildSecurityContext(Authority... roles) {
    CoinUser user = coinUser(roles);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    return new ModelAndView();
  }


}
