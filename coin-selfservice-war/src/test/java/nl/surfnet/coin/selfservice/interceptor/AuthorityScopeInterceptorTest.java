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

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import nl.surfnet.coin.csa.model.CrmArticle;
import nl.surfnet.coin.csa.model.License;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_APPLY_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_QUESTION_ALLOWED;
import static nl.surfnet.coin.selfservice.control.BaseController.TOKEN_CHECK;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_LICENSE_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_IDP_SURFCONEXT_ADMIN;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.ROLE_USER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * AuthorityScopeInterceptorTest.java
 * 
 */
public class AuthorityScopeInterceptorTest {

  private AuthorityScopeInterceptor interceptor = new AuthorityScopeInterceptor();

  /**
   * Test method for
   * {@link nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void test_regular_user_may_not_see_technical_mail_address() throws Exception {
    ModelAndView modelAndView = new ModelAndView();

    CoinUser user = coinUser(ROLE_USER);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    Service sp = buildService();
    sp.setConnected(true);
    modelAndView.addObject(BaseController.SERVICE, sp);
    String technicalSupportMail = sp.getSupportMail();
    // we have not intercepted yet, so everything is accessible
    assertNotNull(technicalSupportMail);

    interceptor.postHandle(new MockHttpServletRequest(), null, null, modelAndView);

    technicalSupportMail = sp.getSupportMail();
    // intercepted...
    assertNull(technicalSupportMail);

    // also not allowed
    Map<String, Object> model = modelAndView.getModel();
    assertFalse((Boolean) model.get(SERVICE_QUESTION_ALLOWED));
    assertFalse((Boolean) model.get(SERVICE_APPLY_ALLOWED));

  }

  @Test(expected = AccessDeniedException.class)
  public void user_cannot_view_unlinked_services() throws Exception {
    ModelAndView mav = new ModelAndView();

    CoinUser user = coinUser(ROLE_USER);

    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    Service sp = buildService();
    sp.setConnected(false);
    mav.addObject(BaseController.SERVICE, sp);

    interceptor.postHandle(null, null, null, mav);

    fail("an AccessDeniedException should be thrown by now");
  }

  /**
   * Test method for
   * {@link nl.surfnet.coin.selfservice.interceptor.AuthorityScopeInterceptor#postHandle(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse, java.lang.Object, org.springframework.web.servlet.ModelAndView)}
   * .
   * 
   * @throws Exception
   */
  @Test
  public void test_power_user_may_see_technical_mail_address() throws Exception {
    ModelAndView modelAndView = new ModelAndView();

    CoinUser user = coinUser(ROLE_IDP_SURFCONEXT_ADMIN);
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
  @SuppressWarnings("unchecked")
  public void idp_license_admin_may_only_see_licensed_services() throws Exception {
    ModelAndView modelAndView = new ModelAndView();

    CoinUser user = coinUser(ROLE_IDP_LICENSE_ADMIN);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    Service sp = buildService();
    modelAndView.addObject(BaseController.SERVICES, Arrays.asList(sp));

    interceptor.postHandle(new MockHttpServletRequest(), null, null, modelAndView);
    Collection<Service> sps = (Collection<Service>) modelAndView.getModelMap().get(
        BaseController.SERVICES);
    assertEquals(0, sps.size());

    Service sp1 = buildService();
    sp1.setLicense(new License());
    sp1.setCrmArticle(new CrmArticle());
    sp1.setHasCrmLink(true);
    modelAndView.addObject(BaseController.SERVICES, Arrays.asList(sp, sp1));

    interceptor.postHandle(new MockHttpServletRequest(), null, null, modelAndView);
    sps = (Collection<Service>) modelAndView.getModelMap().get(BaseController.SERVICES);
    assertEquals(1, sps.size());

    Service sp2 = buildService();
    sp2.setConnected(true);
    modelAndView.addObject(BaseController.SERVICES, Arrays.asList(sp, sp1, sp2));

    interceptor.postHandle(new MockHttpServletRequest(), null, null, modelAndView);
    sps = (Collection<Service>) modelAndView.getModelMap().get(BaseController.SERVICES);
    assertEquals(2, sps.size());
  }

  @Test
  public void token_session_does_not_equal_request_param_token() throws Exception {
    ModelAndView modelAndView = new ModelAndView();
    CoinUser user = coinUser(ROLE_IDP_LICENSE_ADMIN);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));

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
    Service service = new Service(1L, "", "", "", false, null);
    service.setSupportMail("somesupportmail");
    return service;
  }

}
