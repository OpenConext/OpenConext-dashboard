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
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@link OAuthTokenDaoAPIv2Impl}
 */
public class OAuthTokenDaoAPIv2ImplTest extends AbstractInMemoryDatabaseTest {

  private static OAuthTokenDaoAPIv2Impl oAuthTokenDao;
  private static final String TEST_USER = "urn:collab:person:example.edu:john.doe";
  private static final String TEST_SP = "https://mujina-sp.example.com";
  private static final String TEST_CONSUMER_KEY = "https://mujina-sp.example.com/";
  private static final String TEST_TOKEN = "c0ffeec0-ffee-c0ff-eec0-ffeec0ffeec0";

  @Before
  public void setUp() {
    oAuthTokenDao = new OAuthTokenDaoAPIv2Impl(super.getJdbcTemplate());
  }

  @Test
  public void testGetOAuthTokens() throws Exception {
    final List<OAuthTokenInfo> oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    boolean tokenForTestSp = false;
    assertFalse(oAuthTokens.isEmpty());
    for (OAuthTokenInfo info : oAuthTokens) {
      if (TEST_CONSUMER_KEY.equals(info.getConsumerKey())) {
        tokenForTestSp = true;
      }
    }
    assertTrue(tokenForTestSp);
  }

  @Test
  public void testRevokeOAuthToken() throws Exception {
    OAuthTokenInfo oAuthTokenInfo = new OAuthTokenInfo(TEST_TOKEN, OAuthTokenDaoAPIv2Impl.SOURCE);
    oAuthTokenInfo.setUserId(TEST_USER);
    oAuthTokenInfo.setConsumerKey(TEST_CONSUMER_KEY);

    List<OAuthTokenInfo> oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(1, oAuthTokens.size());
    oAuthTokenDao.revokeOAuthToken(oAuthTokenInfo);
    oAuthTokens = oAuthTokenDao.getOAuthTokens(TEST_USER, TEST_SP);
    assertEquals(0, oAuthTokens.size());
  }

  @Override
  public String getMockDataContentFilename() {
    return "db/migration/hsqldb/V0.0.0__initial.sql";
  }

  @Override
  public String getMockDataCleanUpFilename() {
    return "coin-selfservice-db-cleanup.sql";
  }
}
