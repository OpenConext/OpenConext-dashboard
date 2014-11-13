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

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.Service;
import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.service.NotificationService;
import nl.surfnet.coin.selfservice.util.SpringSecurity;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * Default implementation of notification service
 */
@Component
public class NotificationServiceImpl implements NotificationService {

  protected static final String FCP_NOTIFICATIONS = "notifications.messages.fcp";

  @Resource
  private Csa csa;

  @Override
  public NotificationMessage getNotifications(String idpId) {
    NotificationMessage notificationMessage = new NotificationMessage();

    boolean isFcp = getAuthorities().contains(Authority.ROLE_DASHBOARD_ADMIN) || getAuthorities().contains(Authority.ROLE_DASHBOARD_VIEWER);

    if (!isFcp) {
      return notificationMessage;
    } else {
      notificationMessage.addMessageKey(FCP_NOTIFICATIONS);
    }

    List<Service> services = csa.getServicesForIdp(idpId);
    List<Service> notLinkedCSPs = new ArrayList<>();
    List<Service> noLicenseCSPs = new ArrayList<>();

    for (Service service : services) {
      if (service.getLicense() != null && !service.isConnected()) {
        notLinkedCSPs.add(service);
      } else if (service.getLicense() == null && service.isHasCrmLink()
        && service.isConnected()) {
        noLicenseCSPs.add(service);
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
    return notificationMessage;
  }

  protected List<Authority> getAuthorities() {
    return SpringSecurity.getCurrentUser().getAuthorityEnums();
  }

}
