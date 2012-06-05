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

package nl.surfnet.coin.selfservice.dao;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.Action;

/**
 * Dao for action objects
 */
public interface ActionsDao {

  /**
   * Get a list of all actions by institution id
   * @param institutionId the id of the institution.
   * @return list of Action (or an empty list in case none found)
   */
  List<Action> findActionsByInstitute(String institutionId);

  /**
   * persist the given action
   * @param action the Action object
   */
  void saveAction(Action action);

  /**
   * Find a unique Action by its id
   * @param id the id
   * @return Action or null if not found.
   */
  Action findAction(int id);
}
