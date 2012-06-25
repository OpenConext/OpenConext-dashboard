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

package nl.surfnet.coin.selfservice.control.user;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ServiceDetailController}
 */
public class ServiceDetailControllerTest {

  @InjectMocks
  private ServiceDetailController controller;

  @Mock
  private CoinUser coinUser;

  @Mock
  private LocaleResolver localeResolver;

  @Mock
  private ServiceProviderService providerService;

  @Mock
  private ConsentDao consentDao;

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;

  @Before
  public void setUp() throws Exception {
    controller = new ServiceDetailController();
    MockitoAnnotations.initMocks(this);
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void testSpDetail() throws Exception {
    ServiceProvider sp = new ServiceProvider("mockSP");
    sp.setLinked(true);
    when(providerService.getServiceProvider("mockSP", "mockIdP")).thenReturn(sp);
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    final ModelAndView modelAndView = controller.serviceDetail("mockSP", idp);
    assertEquals("user/service-detail", modelAndView.getViewName());
    assertEquals(sp, modelAndView.getModelMap().get("sp"));
  }

  @Test
  public void testSpDetail_notLinked() throws Exception {
    ServiceProvider sp = new ServiceProvider("mockSP");
    sp.setLinked(false);
    when(providerService.getServiceProvider("mockSP", "mockIdP")).thenReturn(sp);
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    final ModelAndView modelAndView = controller.serviceDetail("mockSP", idp);
    assertEquals("user/service-detail", modelAndView.getViewName());
    assertFalse(modelAndView.getModelMap().containsKey("sp"));
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }
}
