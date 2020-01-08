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
package dashboard.service;

import dashboard.domain.Action;
import dashboard.domain.Change;
import dashboard.domain.JiraFilter;
import dashboard.domain.JiraResponse;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ActionsService {

    JiraResponse searchTasks(String idp, JiraFilter jiraFilter);

    Action create(Action action, List<Change> changes);

    Action automaticallyConnect(Action action);

    void rejectInviteRequest(String jiraKey, String comment);

    void approveInviteRequest(String jiraKey, String comment);
}
