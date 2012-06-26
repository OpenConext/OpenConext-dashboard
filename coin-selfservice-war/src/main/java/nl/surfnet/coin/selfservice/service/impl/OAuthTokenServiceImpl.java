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
import java.util.List;

import org.apache.commons.lang.StringUtils;

import nl.surfnet.coin.selfservice.dao.OAuthTokenDao;
import nl.surfnet.coin.selfservice.domain.OAuthTokenInfo;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.OAuthTokenService;

/**
 * Implementation of {@link OAuthTokenService}
 */
public class OAuthTokenServiceImpl implements OAuthTokenService {

  private List<OAuthTokenDao> oAuthTokenDaos;

  @Override
  public List<OAuthTokenInfo> getOAuthTokenInfoList(final String userId, ServiceProvider serviceProvider) {
    List<OAuthTokenInfo> oAuthTokenInfoList = new ArrayList<OAuthTokenInfo>();
    for (OAuthTokenDao dao : oAuthTokenDaos) {
      final List<OAuthTokenInfo> tokens = dao.getOAuthTokens(userId, serviceProvider.getId());
      for (OAuthTokenInfo tokenInfo : tokens) {
        final String gadgetBaseUrl = serviceProvider.getGadgetBaseUrl();
        if (StringUtils.isNotBlank(gadgetBaseUrl) && tokenInfo.getConsumerKey().matches(gadgetBaseUrl)) {
          oAuthTokenInfoList.add(tokenInfo);
        }
      }
    }
    return oAuthTokenInfoList;
  }

  @Override
  public void revokeOAuthTokens(String userId, ServiceProvider serviceProvider) {
    final List<OAuthTokenInfo> oAuthTokenInfoList = getOAuthTokenInfoList(userId, serviceProvider);
    for (OAuthTokenInfo info : oAuthTokenInfoList) {
      for (OAuthTokenDao dao : oAuthTokenDaos) {
        dao.revokeOAuthToken(info);
      }
    }
  }


  public void setoAuthTokenDaos(List<OAuthTokenDao> oAuthTokenDaos) {
    this.oAuthTokenDaos = oAuthTokenDaos;
  }
}
