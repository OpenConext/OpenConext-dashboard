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
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import nl.surfnet.coin.selfservice.domain.CoinAuthority;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
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
    MockHttpServletRequest request = new MockHttpServletRequest();

    IdentityProvider idp1 = new IdentityProvider();
    idp1.setId("idpId_1");
    IdentityProvider idp2 = new IdentityProvider();
    idp2.setId("idpId_2");
    when(coinUser.getInstitutionIdps()).thenReturn(Arrays.asList(idp1, idp2));

    final IdentityProvider identityProvider = baseController.getRequestedIdp("idpId_2", request);
    assertEquals(idp2, identityProvider);
  }

  @Test
  public void testSelectedIdP_alreadySet() throws Exception {
    IdentityProvider idp2 = new IdentityProvider();
    idp2.setId("idpId_2");
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute("selectedidp", idp2);

    final IdentityProvider identityProvider = baseController.getRequestedIdp(null, request);
    assertEquals(idp2, identityProvider);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCurrentRole() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    GrantedAuthority user = new CoinAuthority(ROLE_USER);
    GrantedAuthority admin = new CoinAuthority(ROLE_DISTRIBUTION_CHANNEL_ADMIN);
    final List grantedAuthorities = Arrays.asList(user, admin);
    when(coinUser.getAuthorities()).thenReturn(grantedAuthorities);

    final String currentRole = baseController.getCurrentRole(ROLE_DISTRIBUTION_CHANNEL_ADMIN.name(), request);
    assertEquals(ROLE_DISTRIBUTION_CHANNEL_ADMIN.name(), currentRole);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCurrentRole_default() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    GrantedAuthority user = new CoinAuthority(ROLE_USER);
    final List grantedAuthorities = Arrays.asList(user);
    when(coinUser.getAuthorities()).thenReturn(grantedAuthorities);

    final String currentRole = baseController.getCurrentRole(null, request);
    assertEquals("ROLE_USER", currentRole);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCurrentRole_alreadySet() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.getSession().setAttribute("currentrole", ROLE_DISTRIBUTION_CHANNEL_ADMIN.name());

    final String currentRole = baseController.getCurrentRole(null, request);
    assertEquals(ROLE_DISTRIBUTION_CHANNEL_ADMIN.name(), currentRole);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testCurrentRole_notValid() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest();
    GrantedAuthority user = new CoinAuthority(ROLE_USER);
    final List grantedAuthorities = Arrays.asList(user);
    when(coinUser.getAuthorities()).thenReturn(grantedAuthorities);

    final String currentRole = baseController.getCurrentRole(ROLE_DISTRIBUTION_CHANNEL_ADMIN.name(), request);
    assertEquals(null, currentRole);
  }

  protected Authentication getAuthentication() {
    return new TestingAuthenticationToken(coinUser, "");
  }

  private class TestController extends BaseController {
    // nothing special
  }
}
