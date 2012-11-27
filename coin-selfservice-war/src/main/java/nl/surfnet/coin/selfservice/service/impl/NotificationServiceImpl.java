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
    
    Authority authority = getHighestAuthority();
    
    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);

    
    List<NotificationMessage> result = new ArrayList<NotificationMessage>();
    
    for (CompoundServiceProvider compoundServiceProvider : services) {
      String messageKey = null;
      if (compoundServiceProvider.isLicenseAvailable() && !compoundServiceProvider.getSp().isLinked()) {
        if (Authority.ROLE_IDP_LICENSE_ADMIN.equals(authority)){
          messageKey = LCP_SERVICE_NOT_LINKED_KEY;
        } else if (Authority.ROLE_IDP_SURFCONEXT_ADMIN.equals(authority)) {
          messageKey = FCP_SERVICE_NOT_LINKED_KEY;
        }
      } else if (!compoundServiceProvider.isLicenseAvailable() && compoundServiceProvider.getSp().isLinked()) {
        if (Authority.ROLE_IDP_LICENSE_ADMIN.equals(authority)){
          messageKey = LCP_LICENCE_NOT_AVAILABLE_KEY;
        } else if (Authority.ROLE_IDP_SURFCONEXT_ADMIN.equals(authority)) {
          messageKey = FCP_LICENCE_NOT_AVAILABLE_KEY;
        }
      }
      if (messageKey != null) {
        NotificationMessage notificationMessage = new NotificationMessage();
        notificationMessage.setMessageKey(messageKey);
        notificationMessage.setArguments(compoundServiceProvider.getSp().getId());
        result.add(notificationMessage);
      }
    }
    
    return result;
  }

  /**
   * In case a user has multiple roles, determine which one has priority in case of these notification messages.
   * @param authorities
   * @return
   */
  private Authority getHighestAuthority() {
    List<Authority> authorities = SpringSecurity.getCurrentUser().getAuthorityEnums();
    Authority result = null;
    for (Authority authority : authorities) {
      if (Authority.ROLE_IDP_SURFCONEXT_ADMIN.equals(authority)) {
        result = authority;
        break;
      } else if (Authority.ROLE_IDP_LICENSE_ADMIN.equals(authority)) {
        result = authority;
      }
    }
    return result;
  }

}
