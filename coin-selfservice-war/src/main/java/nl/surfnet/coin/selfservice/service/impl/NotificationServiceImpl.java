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
  
  protected static final String FCP_NOTIFICATIONS = "jsp.notifications.fcp.text";
  protected static final String LCP_NOTIFICATIONS = "jsp.notifications.lcp.text";

  @Resource
  private CompoundSPService compoundSPService;

  @Override
  public NotificationMessage getNotifications(IdentityProvider selectedidp) {
    NotificationMessage notificationMessage = new NotificationMessage();

    boolean isLcp = getAuthorities().contains(Authority.ROLE_IDP_LICENSE_ADMIN);
    boolean isFcp = getAuthorities().contains(Authority.ROLE_IDP_SURFCONEXT_ADMIN);
    
    if (!isLcp && !isFcp) {
      return notificationMessage;
    }
    
    //might that we have two text's but this is very rare and acceptable
    if (isFcp) {
      notificationMessage.addMessageKey(FCP_NOTIFICATIONS);
    }
    if (isLcp) {
      notificationMessage.addMessageKey(LCP_NOTIFICATIONS);
    }

    List<CompoundServiceProvider> services = compoundSPService.getCSPsByIdp(selectedidp);
    List<CompoundServiceProvider> notLinkedCSPs = new ArrayList<CompoundServiceProvider>();
    List<CompoundServiceProvider> noLicenseCSPs = new ArrayList<CompoundServiceProvider>();

    for (CompoundServiceProvider compoundServiceProvider : services) {
      if (compoundServiceProvider.isLicenseAvailable() && !compoundServiceProvider.getSp().isLinked()) {
        // if statement inside if statement for readability
        if (isFcp || (isLcp && compoundServiceProvider.isArticleAvailable())) {
          notLinkedCSPs.add(compoundServiceProvider);
        }
      } else if (!compoundServiceProvider.isLicenseAvailable() && compoundServiceProvider.isArticleAvailable()
          && compoundServiceProvider.getSp().isLinked()) {
        // if statement inside if statement for readability
        if (isFcp || (isLcp && compoundServiceProvider.isArticleAvailable())) {
          noLicenseCSPs.add(compoundServiceProvider);
        }
      }
    }

    // Create message for license available but service not linked
    if (!notLinkedCSPs.isEmpty()) {
      notificationMessage.addArguments(notLinkedCSPs);
    }

    // Create message for service linked but license not available
    if (!noLicenseCSPs.isEmpty()) {
      notificationMessage.addArguments(noLicenseCSPs);
    }
   // notificationMessage.sort();
    return notificationMessage;
  }

  protected List<Authority> getAuthorities() {
    return SpringSecurity.getCurrentUser().getAuthorityEnums();
  }

}
