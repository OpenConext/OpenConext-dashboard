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
package dashboard.service.impl;

import dashboard.domain.Action;
import dashboard.domain.Change;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface JiraClient {

    String DISPATCH = "Dispatch";
    String ACCEPT = "Accept";
    String WAITING_FOR_CUSTOMER = "Waiting for customer";
    String BACK_IN_PROGRESS = "Back in progress";
    String SUPPORT_LEVEL = "Support level";
    String RESOLVE = "Resolve";

    String create(Action action) throws IllegalStateException;

    JiraResponse searchTasks(String idp, JiraFilter jiraFilter);

    Map<String, String> validTransitions(String key);

    void transition(String key, String transitionId, Optional<String> resolution, Optional<String> comment);

    void comment(String key, String comment);

    void updateOptionalMessage(String jiraKey, String optionalMessage);
}
