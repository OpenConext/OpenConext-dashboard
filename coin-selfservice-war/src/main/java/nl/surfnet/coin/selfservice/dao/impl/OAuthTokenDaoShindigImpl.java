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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import nl.surfnet.coin.selfservice.dao.OAuthTokenDao;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;

/**
 * Gets OAuthTokens from the Shindig (os.surfconext.nl) database
 */
public class OAuthTokenDaoShindigImpl implements OAuthTokenDao {

  public static final String SOURCE = "os.surfconext";

  private final JdbcTemplate shindigJdbcTemplate;

  public OAuthTokenDaoShindigImpl(JdbcTemplate shindigJdbcTemplate) {
    this.shindigJdbcTemplate = shindigJdbcTemplate;
  }

  @Override
  public List<OAuthTokenInfo> getOAuthTokens(final String userId, final String spEntityId) {
    List<OAuthTokenInfo> tokenInfoList;
    Object[] args = {userId};
    try {
      tokenInfoList = this.shindigJdbcTemplate.query("SELECT token, consumer_key FROM oauth_entry WHERE user_id = ?",
          args, new RowMapper<OAuthTokenInfo>() {
        @Override
        public OAuthTokenInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
          final String token = rs.getString("token");
          final String consumerKey = rs.getString("consumer_key");
          OAuthTokenInfo oAuthTokenInfo = new OAuthTokenInfo(token, SOURCE);
          oAuthTokenInfo.setId(token);
          oAuthTokenInfo.setUserId(userId);
          oAuthTokenInfo.setConsumerKey(consumerKey);
          return oAuthTokenInfo;
        }
      });
    } catch (EmptyResultDataAccessException e) {
      tokenInfoList = new ArrayList<OAuthTokenInfo>();
    }

    return tokenInfoList;
  }

  @Override
  public void revokeOAuthToken(OAuthTokenInfo oAuthTokenInfo) {
    if (SOURCE.equals(oAuthTokenInfo.getSource())) {
      Object[] args = {oAuthTokenInfo.getUserId(), oAuthTokenInfo.getConsumerKey()};
      this.shindigJdbcTemplate.update("DELETE FROM oauth_entry WHERE user_id = ? AND consumer_key = ?", args);
    }
  }

}
