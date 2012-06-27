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
 * Implementation for OAuth v2 access keys from api.surfconext
 */
public class OAuthTokenDaoAPIv2Impl implements OAuthTokenDao {

  public static final String SOURCE = "api.surfconext.oauth10a";


  private final JdbcTemplate apiJdbcTemplate;


  public OAuthTokenDaoAPIv2Impl(JdbcTemplate apiJdbcTemplate) {
    this.apiJdbcTemplate = apiJdbcTemplate;
  }


  @Override
  public List<OAuthTokenInfo> getOAuthTokens(final String userId, final String spEntityId) {
    Object[] args = {userId, spEntityId};
    List<OAuthTokenInfo> tokenInfoList;
    try {
      tokenInfoList = this.apiJdbcTemplate.query(
          "SELECT token_id, client_id FROM oauth_access_token WHERE user_name = ? AND client_entity_id = ?",
          args, new RowMapper<OAuthTokenInfo>() {
        @Override
        public OAuthTokenInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
          final String token_id = rs.getString("token_id");
          final String client_id = rs.getString("client_id");
          OAuthTokenInfo info = new OAuthTokenInfo(token_id, SOURCE);
          info.setConsumerKey(client_id);
          info.setUserId(userId);
          return info;
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
      this.apiJdbcTemplate.update("DELETE FROM oauth_access_token WHERE user_name = ? AND client_id = ?", args);
    }
  }
}
