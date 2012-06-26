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

package nl.surfnet.coin.selfservice.service;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

/**
 * Service to handle {@link OAuthTokenInfo}
 */
public interface OAuthTokenService {

  /**
   * Gets a List of {@link OAuthTokenInfo} for a user
   *
   * @param userId        unique identifier of a user
   * @param serviceProvider {@link ServiceProvider} which can be linked to the OAuth token
   * @return List of OAuthTokenInfo, can be empty
   */
  List<OAuthTokenInfo> getOAuthTokenInfoList(String userId, ServiceProvider serviceProvider);

  /**
   * Revokes an existing OAuthToken
   *
   * @param userId          unique identifier of a user
   * @param serviceProvider ServiceProvider of which the tokens should be revoked
   */
  void revokeOAuthTokens(String userId, ServiceProvider serviceProvider);
}
