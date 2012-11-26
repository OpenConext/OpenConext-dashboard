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

import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;
import nl.surfnet.coin.selfservice.service.NotificationService;

import org.springframework.stereotype.Component;

/**
 * Default implementation of notification service
 *
 */
@Component
public class NotificationServiceImpl implements NotificationService {

  @Resource
  private CompoundSPService compoundSPService;

  @Override
  public List<NotificationMessage> getNotifications(List<CompoundServiceProvider> services) {
    List<NotificationMessage> result = new ArrayList<NotificationMessage>();
    
    //TODO get message from messageresource
    for (CompoundServiceProvider compoundServiceProvider : services) {
      if (compoundServiceProvider.isLicenseAvailable() && !compoundServiceProvider.getSp().isLinked()) {
        NotificationMessage message = new NotificationMessage();
        message
            .setMessage("Voor "
                + compoundServiceProvider.getSp().getId()
                + " is wel een licentie aanwezig, maar er is nog geen SURFconext koppeling actief. Vraag de federatiecontactpersoon van uw instelling om deze koppeling te activeren");
        result.add(message);
      } else if (!compoundServiceProvider.isLicenseAvailable() && compoundServiceProvider.getSp().isLinked()) {
        NotificationMessage message = new NotificationMessage();
        message
            .setMessage("Voor "
                + compoundServiceProvider.getSp().getId()
                + "  is wel een SURFconext koppeling aanwezig, maar er is nog geen licentie afgesloten. Bekijk deze applicatie en sluit direct een overeenkomst af bij SURFmarket.nl (link naar de detailpagina)");
        result.add(message);
      }
    }
    
    return result;
  }
}
