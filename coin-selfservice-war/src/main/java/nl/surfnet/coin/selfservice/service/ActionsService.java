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

import nl.surfnet.coin.selfservice.domain.Action;
import nl.surfnet.coin.selfservice.domain.JiraTask;

/**
 * Service for Actions
 */
public interface ActionsService {

  /**
   * Get a list of all actions of a certain institution
   *
   *
   * @param institutionId the institutionid
   * @return list of actions
   */
  List<Action> getActions(String institutionId);

  /**
   * Register the creation of a JIRA issue.
   * @param issueKey the created jira issue key
   * @param task details of the issue
   */
  void registerJiraIssueCreation(String issueKey, JiraTask task);
}
