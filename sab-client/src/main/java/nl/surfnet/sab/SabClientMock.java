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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

  private static final Logger LOG = LoggerFactory.getLogger(SabClientMock.class);

  /**
   * Mapping of userIds to roles
   */
  private Map<String, SabRoleHolder> rolesMapping = new ConcurrentHashMap<String, SabRoleHolder>();


  public SabClientMock() {
    rolesMapping.put("user1", new SabRoleHolder("SURFNET", asList("Foo", "Bar")));
    rolesMapping.put("user2", new SabRoleHolder("SURFNET", asList("Foo", "Baz")));
  }

  @Override
  public boolean hasRoleForOrganisation(String userId, String role, String organisation) {
    return
            rolesMapping.containsKey(userId) &&
                    rolesMapping.get(userId).getOrganisation().equals(organisation) &&
                    rolesMapping.get(userId).getRoles().contains(role);
  }

  @Override
  public SabRoleHolder getRoles(String userId) throws IOException {
    SabRoleHolder sabRoleHolder = rolesMapping.get(userId);
    LOG.debug("Returning SAB role holder: {}", sabRoleHolder);
    return sabRoleHolder;
  }

  @Override
  public SabPersonsInRole getPersonsInRoleForOrganization(String organisationAbbreviation, String role) {
    List<SabRole> sabRoles = asList(
            new SabRole("CONVER", "SURFconextverantwoordelijke"),
            new SabRole("CONBEH", "SURFconextbeheerder")
    );
    List<SabPerson> sabPersons = asList(
            new SabPerson("Hans", "Janssen", "hjanssen", sabRoles),
            new SabPerson("Frans", "Franssen", "ffransen", sabRoles)
    );
    return new SabPersonsInRole(sabPersons, role);
  }
}
