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

package nl.surfnet.coin.selfservice.control.idpadmin;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.service.ActionsService;
import nl.surfnet.coin.selfservice.service.JiraService;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.PersonAttributeLabelService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

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

public class SpDetailControllerTest {


  @InjectMocks
  private SpDetailController spDetailController;

  @Mock
  private ServiceProviderService sps;

  @Mock
  private CoinUser coinUser;

  @Before
  public void before() {
    spDetailController = new SpDetailController();
    MockitoAnnotations.initMocks(this);
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

  @Test
  public void details() {
    final ModelAndView mav = spDetailController.spDetail("foobar", getIdp());
    assertTrue(mav.hasView());
    assertThat(mav.getViewName(), is("idpadmin/sp-detail"));
  }

  private static IdentityProvider getIdp() {
    return new IdentityProvider("idp", null, "idp");
  }
}
