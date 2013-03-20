/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.sab;

import java.io.IOException;

/**
 * Interface for SAB, the SURFnet Authorisation Beheer interface
 */
public interface Sab {

  /**
   * Returns whether the given user has the given role for the given organisation
   * @param userId
   * @param role
   * @param organisation
   * @return boolean
   */
  boolean hasRoleForOrganisation(String userId, String role, String organisation);

  /**
   * Get the Role/organisation info for the given userId
   * @param userId the userId to query for
   * @return SabRoleHolder
   */
  SabRoleHolder getRoles(String userId) throws IOException;

}
