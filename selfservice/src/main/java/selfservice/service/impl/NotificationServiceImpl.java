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
package selfservice.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import selfservice.domain.CoinAuthority.Authority;
import selfservice.domain.NotificationMessage;
import selfservice.domain.Service;
import selfservice.service.Csa;
import selfservice.service.NotificationService;
import selfservice.util.SpringSecurity;

@Component
public class NotificationServiceImpl implements NotificationService {

  protected static final String FCP_NOTIFICATIONS = "notifications.messages.fcp";

  @Autowired
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
      } else if (service.getLicense() == null && service.isHasCrmLink() && service.isConnected()) {
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
