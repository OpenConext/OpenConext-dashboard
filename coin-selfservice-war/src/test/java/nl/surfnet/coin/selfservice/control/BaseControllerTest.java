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

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.surfnet.coin.selfservice.domain.CoinUser;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;

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
  public void testMyIdentityProviders() throws Exception {
    IdentityProvider idp1 = new IdentityProvider();
    idp1.setId("idpId_1");
    IdentityProvider idp2 = new IdentityProvider();
    idp1.setId("idpId_2");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp1, idp2));

    final List<IdentityProvider> identityProviders = baseController.getMyInstitutionIdps();
    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testSelectedIdP() throws Exception {
    IdentityProvider idp1 = new IdentityProvider();
    idp1.setId("idpId_1");
    IdentityProvider idp2 = new IdentityProvider();
    idp2.setId("idpId_2");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp1, idp2));

    final IdentityProvider identityProvider = baseController.getRequestedIdp("idpId_2");
    assertEquals(idp2, identityProvider);
  }

  private Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

  private class TestController extends BaseController {
    // nothing special
  }
}
