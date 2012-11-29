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

package nl.surfnet.coin.selfservice.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Container for notificationmessages with convenient methods.
 *
 */
public class NotificationMessages implements Serializable {

  private static final long serialVersionUID = 1L;

  private List<NotificationMessage> messages = new ArrayList<NotificationMessage>();

  
  public List<NotificationMessage> getMessages() {
    return messages;
  }

  public void addMessages(NotificationMessage message) {
    messages.add(message);
  }

  public void addAllMessages(List<NotificationMessage> messageList) {
    messages.addAll(messageList);
  }
  
  public int getNumberOfUnreadMessages() {
    int count = 0;
    for (NotificationMessage message : messages) {
      if (!message.isRead()) {
        count++;
      }
    }
    return count;
  }
  
  /**
   * @return true if there are unread messages
   */
  public boolean isUnreadMessages(){
    return getNumberOfUnreadMessages() > 0;
  }
  
  public void markAllMessagesAsRead() {
    for (NotificationMessage message : messages) {
      message.setRead(true);
    }
  }
}
