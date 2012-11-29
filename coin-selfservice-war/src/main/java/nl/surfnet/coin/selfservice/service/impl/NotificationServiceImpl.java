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

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.util.SpringSecurity;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

/**
 * Default implementation of notification service
 * 
 */
@Component
public class NotificationServiceImpl implements NotificationService {

  private static final String LCP_SERVICE_NOT_LINKED_KEY = "jsp.notifications.lcp.service.not.linked";
  private static final String FCP_SERVICE_NOT_LINKED_KEY = "jsp.notifications.fcp.service.not.linked";
  private static final String LCP_LICENCE_NOT_AVAILABLE_KEY = "jsp.notifications.lcp.license.not.available";
  private static final String FCP_LICENCE_NOT_AVAILABLE_KEY = "jsp.notifications.fcp.license.not.available";

  @Resource
  private CompoundSPService compoundSPService;

  @Override
  public List<NotificationMessage> getNotifications(IdentityProvider selectedidp) {
    List<NotificationMessage> result = new ArrayList<NotificationMessage>();

    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);

    for (CompoundServiceProvider compoundServiceProvider : services) {
      for (Authority authority : getAuthorities()) {
        String messageKey = null;
        if (compoundServiceProvider.isLicenseAvailable() && !compoundServiceProvider.getSp().isLinked()) {
          // Get messagekey based on Authority (for LCP also filter on
          // availableArticles)
          if (Authority.ROLE_IDP_LICENSE_ADMIN.equals(authority) && compoundServiceProvider.isArticleAvailable()) {
            messageKey = LCP_SERVICE_NOT_LINKED_KEY;
          } 
          if (Authority.ROLE_IDP_SURFCONEXT_ADMIN.equals(authority)) {
            messageKey = FCP_SERVICE_NOT_LINKED_KEY;
          }
        } else if (!compoundServiceProvider.isLicenseAvailable() && compoundServiceProvider.getSp().isLinked()) {
          // Get messagekey based on Authority (for LCP also filter on
          // availableArticles)
          if (Authority.ROLE_IDP_LICENSE_ADMIN.equals(authority) && compoundServiceProvider.isArticleAvailable()) {
            messageKey = LCP_LICENCE_NOT_AVAILABLE_KEY;
          } 
          if (Authority.ROLE_IDP_SURFCONEXT_ADMIN.equals(authority)) {
            messageKey = FCP_LICENCE_NOT_AVAILABLE_KEY;
          }
        }
        if (messageKey != null) {
          NotificationMessage notificationMessage = new NotificationMessage();
          notificationMessage.setMessageKey(messageKey);
          String arguments = compoundServiceProvider.getServiceDescriptionNl();
          if (StringUtils.isEmpty(arguments)) {
            arguments = compoundServiceProvider.getServiceProviderEntityId();
          }
          notificationMessage.setArguments(arguments);
          result.add(notificationMessage);
        }
      }
    }

    return result;
  }

  protected List<Authority> getAuthorities() {
    return SpringSecurity.getCurrentUser().getAuthorityEnums();
  }
}
