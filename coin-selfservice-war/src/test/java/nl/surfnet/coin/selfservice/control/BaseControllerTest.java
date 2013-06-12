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

import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.when;

/**
 * Tests for {@link BaseController}
 */
public class BaseControllerTest {

  @InjectMocks
  private BaseController baseController;

  @Mock
  private CoinUser coinUser;

  @Before
  public void setUp() throws Exception {
    baseController = new TestController();
    MockitoAnnotations.initMocks(this);
    SecurityContextHolder.getContext().setAuthentication(getAuthentication());
  }

  @Test
  public void testMyInstitutionIdentityProviders() throws Exception {
    InstitutionIdentityProvider idp1 = new InstitutionIdentityProvider();
    idp1.setId("idpId_1");
    InstitutionIdentityProvider idp2 = new InstitutionIdentityProvider();
    idp1.setId("idpId_2");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp1, idp2));

    final List<InstitutionIdentityProvider> identityProviders = baseController.getMyInstitutionIdps();
    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testSelectedIdP() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();

    InstitutionIdentityProvider idp1 = new InstitutionIdentityProvider();
    idp1.setId("idpId_1");
    InstitutionIdentityProvider idp2 = new InstitutionIdentityProvider();
    idp2.setId("idpId_2");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp1, idp2));

    final InstitutionIdentityProvider identityProvider = baseController.switchIdp(request,"idpId_2");
    assertEquals(idp2, identityProvider);
  }

  @Test
  public void testSelectedIdP_alreadySet() throws Exception {
    InstitutionIdentityProvider idp2 = new InstitutionIdentityProvider();
    idp2.setId("idpId_2");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute(BaseController.SELECTED_IDP, idp2);

    final InstitutionIdentityProvider identityProvider = baseController.getSelectedIdp(request);
    assertEquals(idp2, identityProvider);
  }

  protected Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

  private class TestController extends BaseController {
    // nothing special
  }
}
