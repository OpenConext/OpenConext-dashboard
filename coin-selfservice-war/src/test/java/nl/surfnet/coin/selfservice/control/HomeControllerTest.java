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

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.surfnet.cruncher.Cruncher;

/**
 * Test for {@link HomeController}
 */
public class HomeControllerTest {

  @InjectMocks
  private HomeController controller;

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;


  @Mock
  private NotificationService notificationService;

  @Mock
  private Csa csa;

  @Mock
  private Cruncher cruncher;

  @Before
  public void setUp() throws Exception {
    controller = new HomeController();
    MockitoAnnotations.initMocks(this);
    when(labelService.getAttributeLabelMap()).thenReturn(new HashMap<String, PersonAttributeLabel>());
  }

  @Test
  public void testStart() throws Exception {

    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute(BaseController.SELECTED_IDP, new InstitutionIdentityProvider("id", "name", "inst"));
    final ModelAndView mav = controller.home(null, "card", request);
    assertEquals("app-overview", mav.getViewName());

    final ModelMap modelMap = mav.getModelMap();
    assertTrue(modelMap.containsKey("personAttributeLabels"));
  }
}
