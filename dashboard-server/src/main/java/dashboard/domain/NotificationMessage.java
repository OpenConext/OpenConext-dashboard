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
package dashboard.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Message that is shown as a notification to a logged in user.
 */
@SuppressWarnings("serial")
public class NotificationMessage implements Serializable {

    private List<String> messageKeys = new ArrayList<>();
    private List<Service> arguments = new ArrayList<>();

    public NotificationMessage() {
        super();
    }

    public List<String> getMessageKeys() {
        return messageKeys;
    }

    public void addMessageKey(String messageKey) {
        this.messageKeys.add(messageKey);
    }

    public List<Service> getArguments() {
        return arguments;
    }

    public void addArguments(List<Service> arguments) {
        this.arguments.addAll(arguments);
    }
}
