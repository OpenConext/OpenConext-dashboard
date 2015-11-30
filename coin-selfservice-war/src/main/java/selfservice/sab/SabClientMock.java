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

package selfservice.sab;

import static java.util.Arrays.asList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.collect.Collections2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

  private static final Logger LOG = LoggerFactory.getLogger(SabClientMock.class);
  private static final SabRole ROLE_BEHEERDER = new SabRole("CONBEH", "SURFconextbeheerder");
  private static final SabRole ROLE_VERANTWOORDELIJKE = new SabRole("CONVER", "SURFconextverantwoordelijke");
  private final List<SabPerson> sabPersons = asList(
    new SabPerson("Hans", "Janssen", "hjanssen", asList(ROLE_BEHEERDER)),
    new SabPerson("Frans", "Franssen", "ffransen", asList(ROLE_BEHEERDER, ROLE_VERANTWOORDELIJKE))
  );

  /**
   * Mapping of userIds to roles
   */
  private Map<String, SabRoleHolder> rolesMapping = new ConcurrentHashMap<>();


  public SabClientMock() {
    rolesMapping.put("user1", new SabRoleHolder("SURFNET", asList("Foo", "Bar")));
    rolesMapping.put("user2", new SabRoleHolder("SURFNET", asList("Foo", "Baz")));
    rolesMapping.put("noroles", new SabRoleHolder("SURFNET", Collections.<String>emptyList()));
  }

  @Override
  public Optional<SabRoleHolder> getRoles(String userId) {
    SabRoleHolder sabRoleHolder = rolesMapping.get(userId);
    LOG.debug("Returning SAB role holder: {}", sabRoleHolder);
    return Optional.ofNullable(sabRoleHolder);
  }

  @Override
  public Collection<SabPerson> getPersonsInRoleForOrganization(String organisationAbbreviation, String role) {
    List<SabPerson> result = new ArrayList<>();

    for (SabPerson sabPerson : sabPersons) {
      Collection<String> roleNames = Collections2.transform(sabPerson.getRoles(), r -> r.roleName);

      if (roleNames.contains(role)) {
        result.add(sabPerson);
      }
    }
    return result;
  }
}
