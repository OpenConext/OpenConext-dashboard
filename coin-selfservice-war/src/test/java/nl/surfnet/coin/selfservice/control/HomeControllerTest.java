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

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;
import nl.surfnet.coin.selfservice.service.impl.CompoundSPService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;

/**
 * Test for {@link HomeController}
 */
public class HomeControllerTest {

  @InjectMocks
  private HomeController controller;

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;

  @Mock
  private ServiceProviderService sps;

  @Mock
  private NotificationService notificationService;

  @Mock
  private CompoundSPService compoundSPService;

  @Before
  public void setUp() throws Exception {
    controller = new HomeController();
    MockitoAnnotations.initMocks(this);
    when(labelService.getAttributeLabelMap()).thenReturn(new HashMap<String, PersonAttributeLabel>());
  }

  @Test
  public void testStart() throws Exception {

    final ModelAndView mav = controller.home(new IdentityProvider(), "card", new MockHttpServletRequest());
    assertEquals("app-overview", mav.getViewName());

    final ModelMap modelMap = mav.getModelMap();
    assertTrue(modelMap.containsKey("personAttributeLabels"));
  }
}
