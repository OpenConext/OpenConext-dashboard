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

import java.util.ArrayList;
import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

/**
 * Test for {@link ServiceListController}
 */
public class ServiceListControllerTest {

  @InjectMocks
  private ServiceListController controller;

  @Mock
  private CoinUser coinUser;

  @Mock
  private ServiceProviderService serviceProviderService;

  private IdentityProvider idp;

  @Before
  public void before() {
    controller = new ServiceListController();
    MockitoAnnotations.initMocks(this);
    idp = new IdentityProvider();
    idp.setId("idpId");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp));
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void testLinkedServices() {
    ModelAndView mav = controller.listLinkedSps(idp);
    when(serviceProviderService.getLinkedServiceProviders(idp.getId())).thenReturn(new ArrayList<ServiceProvider>());
    assertEquals("user/service-overview", mav.getViewName());
    final ModelMap modelMap = mav.getModelMap();
    assertTrue(modelMap.containsKey("sps"));
    assertEquals("linked-services", modelMap.get("activeSection"));
  }


  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

}
