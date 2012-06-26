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

package nl.surfnet.coin.selfservice.dao;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;

/**
 * DAO to retrieve OAuth tokens
 */
public interface OAuthTokenDao {

  /**
   * Gets a List of {@link OAuthTokenInfo} for a user
   *
   * @param userId     unique identifier of a user
   * @param spEntityId unique identifier of a ServiceProvider
   * @return List of OAuthTokenInfo, can be empty
   */
  List<OAuthTokenInfo> getOAuthTokens(String userId, String spEntityId);

  /**
   * Revokes an existing OAuthToken
   *
   * @param oAuthTokenInfo the {@link OAuthTokenInfo} to revoke
   */
  void revokeOAuthToken(OAuthTokenInfo oAuthTokenInfo);

}
