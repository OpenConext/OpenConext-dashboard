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

package nl.surfnet.coin.selfservice.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.selfservice.dao.OAuthTokenDao;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test for {@link OAuthTokenServiceImpl}
 */
public class OAuthTokenServiceImplTest {

  private static final String USER_UUID = "urn:collab:person:example.edu:john.doe";

  private OAuthTokenServiceImpl oAuthTokenService;

  private List<OAuthTokenDao> oAuthTokenDaos = new ArrayList<OAuthTokenDao>();

  private OAuthTokenDao mockOAuthTokenDao;

  @Before
  public void setUp() {
    oAuthTokenService = new OAuthTokenServiceImpl();
    mockOAuthTokenDao = mock(OAuthTokenDao.class);
    oAuthTokenDaos.add(mockOAuthTokenDao);
    oAuthTokenService.setoAuthTokenDaos(oAuthTokenDaos);
  }

  @Test
  public void testGetOAuthTokens() throws Exception {
    OAuthTokenInfo sp1Token = new OAuthTokenInfo("cafebabe-cafe-babe", "mockito");
    sp1Token.setUserId(USER_UUID);
    sp1Token.setConsumerKey("http://mocksp.example.edu/myapp");
    OAuthTokenInfo sp2Token = new OAuthTokenInfo("deadbeef-dead-beef", "mockito");
    sp2Token.setUserId(USER_UUID);
    sp2Token.setConsumerKey("http://mocksp2.example.org/myapp");
    List<OAuthTokenInfo> tokens = Arrays.asList(sp1Token, sp2Token);
    when(mockOAuthTokenDao.getOAuthTokens(USER_UUID, "mocksp")).thenReturn(tokens);

    ServiceProvider sp = new ServiceProvider("mocksp");
    sp.setGadgetBaseUrl("http://mocksp.example.edu/.*");

    final List<OAuthTokenInfo> oAuthTokens = oAuthTokenService.getOAuthTokenInfoList(USER_UUID, sp);
    assertEquals(1, oAuthTokens.size());
    assertEquals(sp1Token, oAuthTokens.get(0));
  }

  @Test
  public void testRevokeOAuthToken() throws Exception {
    OAuthTokenInfo sp1Token = new OAuthTokenInfo("cafebabe-cafe-babe", "mockito");
    sp1Token.setUserId(USER_UUID);
    sp1Token.setConsumerKey("http://mocksp.example.edu/myapp");
    OAuthTokenInfo sp2Token = new OAuthTokenInfo("deadbeef-dead-beef", "mockito");
    sp2Token.setUserId(USER_UUID);
    sp2Token.setConsumerKey("http://mocksp2.example.org/myapp");
    List<OAuthTokenInfo> tokens = Arrays.asList(sp1Token, sp2Token);
    when(mockOAuthTokenDao.getOAuthTokens(USER_UUID, "mocksp")).thenReturn(tokens);
    ServiceProvider sp = new ServiceProvider("mocksp");
    sp.setGadgetBaseUrl("http://mocksp.example.edu/.*");

    oAuthTokenService.revokeOAuthTokens(USER_UUID, sp);
    verify(mockOAuthTokenDao).revokeOAuthToken(sp1Token);
  }

}
