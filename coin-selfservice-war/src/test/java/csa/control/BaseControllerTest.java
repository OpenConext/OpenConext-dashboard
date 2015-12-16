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

package csa.control;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import csa.domain.CoinUser;
import csa.domain.IdentityProvider;

@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest {

  @InjectMocks
  private BaseController baseController = new BaseController() {
  };

  private CoinUser coinUser;

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  @Before
  public void setUp() throws Exception {
    coinUser = new CoinUser();

    SecurityContextHolder.setContext(securityContext);
    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(coinUser);
  }

  @Test
  public void testMyIdentityProviders() throws Exception {
    IdentityProvider idp1 = new IdentityProvider();
    idp1.setId("idpId_1");
    IdentityProvider idp2 = new IdentityProvider();
    idp2.setId("idpId_2");
    coinUser.addInstitutionIdp(idp1);
    coinUser.addInstitutionIdp(idp2);

    final List<IdentityProvider> identityProviders = baseController.getMyInstitutionIdps();
    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testSelectedIdP() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();

    IdentityProvider idp1 = new IdentityProvider();
    idp1.setId("idpId_1");
    IdentityProvider idp2 = new IdentityProvider();
    idp2.setId("idpId_2");
    coinUser.addInstitutionIdp(idp1);
    coinUser.addInstitutionIdp(idp2);

    coinUser.setIdp(idp2);

    final IdentityProvider identityProvider = baseController.getSelectedIdp(request);
    assertEquals(idp2, identityProvider);
  }

}
