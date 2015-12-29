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
package selfservice.control.shopadmin;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.After;
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

import selfservice.control.shopadmin.BaseController;
import selfservice.domain.CoinUser;
import selfservice.domain.IdentityProvider;

@RunWith(MockitoJUnitRunner.class)
public class BaseControllerTest {

  @InjectMocks
  private BaseController baseController = new BaseController() {};

  @Mock
  private SecurityContext securityContext;

  @Mock
  private Authentication authentication;

  private CoinUser coinUser = new CoinUser();

  @Before
  public void setUp() throws Exception {
    SecurityContextHolder.setContext(securityContext);

    coinUser = new CoinUser();

    when(securityContext.getAuthentication()).thenReturn(authentication);
    when(authentication.getPrincipal()).thenReturn(coinUser);
  }

  @After
  public void cleanUp() {
    SecurityContextHolder.clearContext();
  }

  @Test
  public void testMyIdentityProviders() throws Exception {
    coinUser.addInstitutionIdp(new IdentityProvider("idpId_1", "institute_1", "name_1"));
    coinUser.addInstitutionIdp(new IdentityProvider("idpId_2", "institute_2", "name_2"));

    List<IdentityProvider> identityProviders = baseController.getMyInstitutionIdps();

    assertEquals(2, identityProviders.size());
  }

  @Test
  public void testSelectedIdP() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();

    IdentityProvider idp1 = new IdentityProvider("idpId_1", "institute_1", "name_1");
    IdentityProvider idp2 = new IdentityProvider("idpId_2", "institute_2", "name_2");
    coinUser.addInstitutionIdp(idp1);
    coinUser.addInstitutionIdp(idp2);
    coinUser.setIdp(idp2);

    IdentityProvider identityProvider = baseController.getSelectedIdp(request);

    assertEquals(idp2, identityProvider);
  }

}
