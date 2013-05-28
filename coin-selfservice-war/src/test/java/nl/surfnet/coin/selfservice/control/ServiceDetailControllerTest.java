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

package nl.surfnet.coin.selfservice.control;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link nl.surfnet.coin.selfservice.control.ServiceDetailController}
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
  private OAuthTokenService oAuthTokenService;
  
  @Mock
  private ConsentDao consentDao;

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;

  @Mock
  private CompoundSPService compoundSPService;
  
  private HttpServletRequest request;

  @Mock
  private Csa csa;

  @Before
  public void setUp() throws Exception {
    controller = new ServiceDetailController();
    MockitoAnnotations.initMocks(this);
    
    request = new MockHttpServletRequest();
    request.setAttribute("showOauthTokens", Boolean.TRUE);
    request.setAttribute("ebLinkActive", Boolean.TRUE);
    when(coinUser.getUid()).thenReturn("urn:collab:person:example.edu:john.doe");
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());

  }

  @Test
  public void testSpDetail() throws Exception {

    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    Service service = new Service(1L, "", "", "", false, null);
    when(csa.getServiceForIdp("mockIdP", 1L)).thenReturn(service);
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);

    OAuthTokenInfo info = new OAuthTokenInfo("cafebabe-cafe-babe-cafe-babe-cafebabe", "mockDao");
    info.setUserId(coinUser.getUid());
    List<OAuthTokenInfo> infos = Arrays.asList(info);
    when(oAuthTokenService.getOAuthTokenInfoList(eq(coinUser.getUid()), (ServiceProvider) any())).thenReturn(infos);

    final ModelAndView modelAndView = controller.serviceDetail(1, null, "false", idp, request);
    assertEquals("app-detail", modelAndView.getViewName());
    assertEquals(service, modelAndView.getModelMap().get("service"));
    assertTrue(modelAndView.getModelMap().containsKey("revoked"));
    assertNull(modelAndView.getModelMap().get("revoked"));
  }

  @Ignore("revoking not yet implemented in CSA")
  @Test
  public void revokeAccessTokens() {
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");

    ServiceProvider sp = new ServiceProvider("mockSp");
    sp.setLinked(true);

    when(providerService.getServiceProvider("mockSp", "mockIdP")).thenReturn(sp);

    final RedirectView view = controller.revokeKeys(1, "mockSp", idp);
    verify(oAuthTokenService).revokeOAuthTokens(coinUser.getUid(), sp);
    assertEquals("app-detail.shtml?compoundSpId=1&revoked=true", view.getUrl());
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }
  
  @Test
  public void testWithoutOathTokens() {
    //disable oathTokens
    request.setAttribute("showOauthTokens", Boolean.FALSE);
    
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);

    Service service = new Service(1L, "", "", "", false, null);
    when(csa.getServiceForIdp("mockIdP", 1L)).thenReturn(service);

    when(oAuthTokenService.getOAuthTokenInfoList(eq(coinUser.getUid()), (ServiceProvider) any())).thenThrow(new IllegalStateException("Illegal Call to API Database"));

    final ModelAndView modelAndView = controller.serviceDetail(1, "revoked", "false", idp, request);
    assertEquals("app-detail", modelAndView.getViewName());
    assertEquals(service, modelAndView.getModelMap().get("service"));
    assertNull(modelAndView.getModelMap().get("oAuthTokens"));
    assertNull(modelAndView.getModelMap().get("revoked"));
  }
  
  @Test
  public void testWithoutConsent() {
    request.setAttribute("ebLinkActive", Boolean.FALSE);
    
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    Service service = new Service(1L, "", "", "", false, null);
    when(csa.getServiceForIdp("mockIdP", 1L)).thenReturn(service);

    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenThrow(new IllegalStateException("Illegal call to consent database"));

    OAuthTokenInfo info = new OAuthTokenInfo("cafebabe-cafe-babe-cafe-babe-cafebabe", "mockDao");
    info.setUserId(coinUser.getUid());
    List<OAuthTokenInfo> infos = Arrays.asList(info);
    when(oAuthTokenService.getOAuthTokenInfoList(eq(coinUser.getUid()), (ServiceProvider) any())).thenReturn(infos);

    final ModelAndView modelAndView = controller.serviceDetail(1, "revoked", "false", idp, request);
    assertEquals("app-detail", modelAndView.getViewName());
    assertEquals(service, modelAndView.getModelMap().get("service"));

    // FIXME: no integration with oauth token service yet in CSA
//    assertNotNull(modelAndView.getModelMap().get("oAuthTokens"));
    assertNull(modelAndView.getModelMap().get("mayHaveGivenConsent"));
  }
}
