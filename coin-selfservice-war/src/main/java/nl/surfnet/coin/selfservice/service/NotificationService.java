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

import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.NotificationMessage;

public interface NotificationService {

  /**
   * Get a list of possible notifications for the given services. Notifications
   * will be created for services that have a license but no linked service or
   * vice-versa
   * 
   * @param services
   * 
   * @param list
   *          of services for the current IDP
   * @return list of possible notifications
   */
  List<NotificationMessage> getNotifications(List<CompoundServiceProvider> services);
}
