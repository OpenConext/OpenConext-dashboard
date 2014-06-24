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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import nl.surfnet.coin.csa.model.OfferedService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import org.surfnet.cruncher.Cruncher;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Category;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.csa.model.Taxonomy;
import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.service.impl.PersonAttributeLabelServiceJsonImpl;
import nl.surfnet.sab.Sab;
import nl.surfnet.sab.SabPerson;

/**
 * Test for {@link HomeController}
 */
@RunWith(MockitoJUnitRunner.class)
public class HomeControllerTest {

  @InjectMocks
  private HomeController controller = new HomeController();

  @Mock
  private PersonAttributeLabelServiceJsonImpl labelService;

  @Mock
  private NotificationService notificationService;

  @Mock
  private Csa csa;

  @Mock
  private Cruncher cruncher;

  @Mock
  private Sab sabClient;

  private MockHttpServletRequest request;


  @Before
  public void setUp() throws Exception {
    when(labelService.getAttributeLabelMap()).thenReturn(new HashMap<String, PersonAttributeLabel>());
    Taxonomy taxonomy = new Taxonomy();
    taxonomy.setCategories(Collections.<Category>emptyList());
    when(csa.getTaxonomy()).thenReturn(taxonomy);
    request = new MockHttpServletRequest();
    request.getSession().setAttribute(BaseController.SELECTED_IDP, new InstitutionIdentityProvider("id", "name", "inst"));
  }

  @Test
  public void testStart() throws Exception {
    ModelAndView mav = controller.home(null, "card", request);
    assertEquals("app-overview", mav.getViewName());

    ModelMap modelMap = mav.getModelMap();
    assertTrue(modelMap.containsKey("personAttributeLabels"));
  }

  @Test
  public void testIdp() throws Exception {
    Collection<SabPerson> maintainers = Collections.emptyList();
    when(sabClient.getPersonsInRoleForOrganization("name", "SURFconextbeheerder")).thenReturn(maintainers);
    Collection<SabPerson>  responsibles = Collections.emptyList();
    when(sabClient.getPersonsInRoleForOrganization("name", "SURFconextverantwoordelijke")).thenReturn(responsibles);

    ModelAndView modelAndView = controller.idp(request);

    assertEquals("idp", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().containsKey("roleAssignments"));
  }

  @Test
  public void testAddsOfferedServicesToModel() throws Exception {
    ArrayList<OfferedService> expected = new ArrayList<OfferedService>();
    when(csa.findOfferedServicesFor("id")).thenReturn(expected);

    ModelAndView modelAndView = controller.idp(request);
    assertEquals("idp", modelAndView.getViewName());
    assertTrue(modelAndView.getModel().containsKey("offeredServicePresenter"));
  }
}