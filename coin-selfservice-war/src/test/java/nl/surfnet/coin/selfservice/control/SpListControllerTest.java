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
import java.util.Collections;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.ModelAndView;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

public class SpListControllerTest {

  @InjectMocks
  private SpListController spListController;

  @Mock
  private CoinUser coinUser;

  @Mock
  private ServiceProviderService serviceProviderService;

  @Before
  public void before() {
    spListController = new SpListController();
    MockitoAnnotations.initMocks(this);
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList("idpId"));
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void mySPsEmpty() {
    when(serviceProviderService.getLinkedServiceProviders(anyString())).thenReturn(Collections.<ServiceProvider>emptyList());
    final ModelAndView mav = spListController.listLinkedSps();
    assertThat(mav, notNullValue());
    assertTrue(mav.hasView());
  }

  @Test
  public void mySPs() {
    when(serviceProviderService.getLinkedServiceProviders(anyString())).thenReturn(
        Arrays.asList(new ServiceProvider("", "")));
    final ModelAndView mav = spListController.listLinkedSps();
    List<ServiceProvider> sps = (List<ServiceProvider>) mav.getModelMap().get("sps");
    assertThat(sps.get(0), notNullValue());
  }

  @Test
  public void allSPs() {
    spListController.listAllSps();
  }

  @Test
  public void multipleIdpsPerInstitute() {
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList("idp1", "idp2"));
    ServiceProvider sp1 = new ServiceProvider("sp1", "");
    ServiceProvider sp2 = new ServiceProvider("sp2", "");
    ServiceProvider sp3 = new ServiceProvider("sp3", "");

    when(serviceProviderService.getAllServiceProviders("idp1")).thenReturn(Arrays.asList(sp1, sp2));
    when(serviceProviderService.getAllServiceProviders("idp2")).thenReturn(Arrays.asList(sp2, sp3));

    final ModelAndView mav = spListController.listAllSps();

    List<ServiceProvider> sps = (List<ServiceProvider>) mav.getModelMap().get("sps");
    assertThat(sps.size(), is(3));
    assertThat(sps.contains(sp1), is(true));
    assertThat(sps.contains(sp2), is(true));
    assertThat(sps.contains(sp3), is(true));
  }

  @Test
  public void details() {
    final ModelAndView mav = spListController.spDetail("foobar");
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("sp-detail"));
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }
}
