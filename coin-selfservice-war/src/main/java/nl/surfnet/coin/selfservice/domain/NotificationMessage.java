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
import java.util.List;
import java.util.Collections;
import java.util.Comparator;


/**
 * Message that is shown as a notification to a logged in user.
 *
 */
public class NotificationMessage implements Serializable {

  private static final long serialVersionUID = 1L;

  private String messageKey;
  private List<CompoundServiceProvider> arguments;

  public String getMessageKey() {
    return messageKey;
  }
  public void setMessageKey(String messageKey) {
    this.messageKey = messageKey;
  }  
  public List<CompoundServiceProvider> getArguments() {
    return arguments;
  }
  public void setArguments(List<CompoundServiceProvider> arguments) {
    Collections.sort(arguments, new Comparator<CompoundServiceProvider>() {
      public int compare(CompoundServiceProvider o1, CompoundServiceProvider o2) {
        return o1.getSp().getName().toUpperCase().compareTo(o2.getSp().getName().toUpperCase());
      }
    });
    this.arguments = arguments;
  }
}
