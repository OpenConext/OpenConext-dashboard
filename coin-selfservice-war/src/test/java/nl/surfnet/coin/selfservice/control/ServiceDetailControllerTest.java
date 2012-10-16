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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.ConsentDao;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

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
import org.springframework.web.servlet.view.RedirectView;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
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
  private LicensingService licensingService;
  
  @Mock
  private ConsentDao consentDao;

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;

  @Before
  public void setUp() throws Exception {
    controller = new ServiceDetailController();
    MockitoAnnotations.initMocks(this);
    when(coinUser.getUid()).thenReturn("urn:collab:person:example.edu:john.doe");
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void testSpDetail() throws Exception {
    ServiceProvider sp = new ServiceProvider("mockSP");
    sp.setLinked(true);
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    when(providerService.getServiceProvider("mockSP", "mockIdP")).thenReturn(sp);
    when(licensingService.getLicensesForIdentityProvider(idp)).thenReturn(new ArrayList<License>());
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);

    OAuthTokenInfo info = new OAuthTokenInfo("cafebabe-cafe-babe-cafe-babe-cafebabe", "mockDao");
    info.setUserId(coinUser.getUid());
    List<OAuthTokenInfo> infos = Arrays.asList(info);
    when(oAuthTokenService.getOAuthTokenInfoList(coinUser.getUid(), sp)).thenReturn(infos);

    final ModelAndView modelAndView = controller.serviceDetail("mockSP", null, idp);
    assertEquals("app-detail", modelAndView.getViewName());
    assertEquals(sp, modelAndView.getModelMap().get("sp"));
    assertTrue(modelAndView.getModelMap().containsKey("revoked"));
    assertNull(modelAndView.getModelMap().get("revoked"));
  }

  @Test
  public void testSpDetail_notLinked() throws Exception {
    ServiceProvider sp = new ServiceProvider("mockSP");
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");
    sp.setLinked(false);
    when(providerService.getServiceProvider("mockSP", "mockIdP")).thenReturn(sp);
    when(oAuthTokenService.getOAuthTokenInfoList(coinUser.getUid(), sp)).thenReturn(Collections.<OAuthTokenInfo>emptyList());
    when(consentDao.mayHaveGivenConsent(coinUser.getUid(), "mockSp")).thenReturn(null);
    when(licensingService.getLicensesForIdentityProvider(idp)).thenReturn(new ArrayList<License>());
    final ModelAndView modelAndView = controller.serviceDetail("mockSP", null, idp);
    assertEquals("app-detail", modelAndView.getViewName());
    assertFalse(modelAndView.getModelMap().containsKey("sp"));
  }

  @Test
  public void revokeAccessTokens() {
    IdentityProvider idp = new IdentityProvider();
    idp.setId("mockIdP");

    ServiceProvider sp = new ServiceProvider("mockSp");
    sp.setLinked(true);

    when(providerService.getServiceProvider("mockSp", "mockIdP")).thenReturn(sp);

    final RedirectView view = controller.revokeKeys("mockSp", idp);
    verify(oAuthTokenService).revokeOAuthTokens(coinUser.getUid(), sp);
    assertEquals("app-detail.shtml?revoked=true&spEntityId=mockSp", view.getUrl());
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }
}
