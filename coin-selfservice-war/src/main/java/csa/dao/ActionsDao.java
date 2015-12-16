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

package csa.dao;

import java.util.Date;
import java.util.List;

import csa.model.Action;

/**
 * Dao for action objects
 */
public interface ActionsDao {

  /**
   * Get a list of all actions by identity provider
   * @param identityProvider the identity provider.
   * @return list of Action (or an empty list in case none found)
   */
  List<Action> findActionsByIdP(String identityProvider);
  
  /**
   * Get a list of all actions within the given date range, this
   * is used for statistics reporting.
   * @param from start date
   * @param to end date
   * @return a list of actions within this date range of an empty list
   */
  List<Action> findActionsByDateRange(Date from, Date to);

  /**
   * persist the given action
   * @param action the Action object
   */
  Long saveAction(Action action);

  /**
   * Find a unique Action by its id
   *
   * @param id the id
   * @return Action or null if not found.
   */
  Action findAction(long id);

  /**
   * Find the Jira Keys for an identity provider
   *
   * @param identityProvider provider the id of the institution.
   * @return a list of jira keys
   */
  List<String> getKeys(String identityProvider);

  /**
   * Close an action.
   *
   * @param jiraKey the key in jira
   */
  void close(String jiraKey);
}
