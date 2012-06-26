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

package nl.surfnet.coin.selfservice.dao.impl;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

import nl.surfnet.coin.db.AbstractInMemoryDatabaseTest;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for {@link OAuthTokenDaoShindigImpl}
 */
public class OAuthTokenDaoShindigImplTest extends AbstractInMemoryDatabaseTest {

  private static OAuthTokenDaoShindigImpl oAuthTokenDao;
  private static final String TEST_USER = "urn:collab:person:example.com:test-user";
  private static final String TEST_SP = "http://mujina-sp.example.com";
  private static final String TEST_CONSUMER_KEY = "http://mujina-sp";
  private static final String TEST_TOKEN = "cafebabe-babe-cafe-babe-cafebabecafe";

  @Before
  public void setUp() throws Exception {
    oAuthTokenDao = new OAuthTokenDaoShindigImpl(super.getJdbcTemplate());
  }

  @Test
  public void testGetOAuthTokens() throws Exception {
    final List<OAuthTokenInfo> oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    boolean tokenForTestSp = false;
    for (OAuthTokenInfo info : oAuthTokens) {
      if (TEST_CONSUMER_KEY.equals(info.getConsumerKey())) {
        tokenForTestSp = true;
      }
    }
    assertTrue(tokenForTestSp);
  }

  @Test
  public void testRevokeOAuthToken() throws Exception {
    OAuthTokenInfo oAuthTokenInfo = new OAuthTokenInfo(TEST_TOKEN, OAuthTokenDaoShindigImpl.SOURCE);
    oAuthTokenInfo.setUserId(TEST_USER);
    oAuthTokenInfo.setConsumerKey(TEST_CONSUMER_KEY);

    List<OAuthTokenInfo> oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(2, oAuthTokens.size());
    oAuthTokenDao.revokeOAuthToken(oAuthTokenInfo);
    oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(1, oAuthTokens.size());
  }

  @Test
  public void testRevokeOAuthToken_wrongSource() throws Exception {
    OAuthTokenInfo oAuthTokenInfo = new OAuthTokenInfo(TEST_TOKEN, "dummysource");
    oAuthTokenInfo.setUserId(TEST_USER);
    oAuthTokenInfo.setConsumerKey(TEST_CONSUMER_KEY);

    List<OAuthTokenInfo> oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(2, oAuthTokens.size());
    oAuthTokenDao.revokeOAuthToken(oAuthTokenInfo);
    oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(2, oAuthTokens.size());
  }

  @Override
  public String getMockDataContentFilename() {
    return "test-data-shindig-insert.sql";
  }

  @Override
  public String getMockDataCleanUpFilename() {
    return "test-data-shindig-cleanup.sql";
  }
}
