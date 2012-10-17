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
import static nl.surfnet.coin.selfservice.control.BaseController.SERVICE_QUESTION_ALLOWED;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import nl.surfnet.coin.selfservice.control.BaseController;
import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.ContactPerson;
import nl.surfnet.coin.selfservice.domain.ContactPersonType;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.spring.security.opensaml.SAMLAuthenticationToken;

import org.junit.Test;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

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

    CoinUser user = coinUser(Authority.ROLE_USER);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    CompoundServiceProvider sp = buildCompoundSeriveProvider();
    modelAndView.addObject(BaseController.COMPOUND_SP, sp);

    String technicalSupportMail = sp.getTechnicalSupportMail();
    // we have not intercepted yet, so everything is accessible
    assertNotNull(technicalSupportMail);

    interceptor.postHandle(null, null, null, modelAndView);

    technicalSupportMail = sp.getTechnicalSupportMail();
    // intercepted...
    assertNull(technicalSupportMail);

    // also not allowed
    Map<String, Object> model = modelAndView.getModel();
    assertFalse((Boolean) model.get(SERVICE_QUESTION_ALLOWED));
    assertFalse((Boolean) model.get(SERVICE_APPLY_ALLOWED));

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

    CoinUser user = coinUser(Authority.ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    SecurityContextHolder.getContext().setAuthentication(new SAMLAuthenticationToken(user, "", user.getAuthorities()));
    CompoundServiceProvider sp = buildCompoundSeriveProvider();
    modelAndView.addObject(BaseController.COMPOUND_SP, sp);

    interceptor.postHandle(null, null, null, modelAndView);

    String technicalSupportMail = sp.getTechnicalSupportMail();
    assertNotNull(technicalSupportMail);

    // also allowed
    Map<String, Object> model = modelAndView.getModel();
    assertTrue((Boolean) model.get(SERVICE_QUESTION_ALLOWED));
    assertTrue((Boolean) model.get(SERVICE_APPLY_ALLOWED));

  }

  private CoinUser coinUser(Authority... authorities) {
    CoinUser coinUser = new CoinUser();
    for (Authority authority : authorities) {
      coinUser.addAuthority(new CoinAuthority(authority));
    }
    return coinUser;
  }

  private CompoundServiceProvider buildCompoundSeriveProvider() {
    ServiceProvider serviceProvider = new ServiceProvider(null);
    serviceProvider.addContactPerson(new ContactPerson(ContactPersonType.technical, "we.dont.want.regular.user.to.see.this@wgaf"));

    CompoundServiceProvider sp = CompoundServiceProvider.builder(serviceProvider, new License());
    return sp;
  }

}
