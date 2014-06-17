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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Arrays.asList;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableList;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

  private static final Logger LOG = LoggerFactory.getLogger(SabClientMock.class);
  private static final SabRole ROLE_BEHEERDER = new SabRole("CONBEH", "SURFconextbeheerder");
  private static final SabRole ROLE_VERANTWOORDELIJKE = new SabRole("CONVER", "SURFconextverantwoordelijke");
  private final List<SabPerson> sabPersons = asList(
    new SabPerson("Hans", "Janssen", "hjanssen", Arrays.asList(ROLE_BEHEERDER)),
    new SabPerson("Frans", "Franssen", "ffransen", Arrays.asList(ROLE_BEHEERDER, ROLE_VERANTWOORDELIJKE))
  );

  /**
   * Mapping of userIds to roles
   */
  private Map<String, SabRoleHolder> rolesMapping = new ConcurrentHashMap<>();


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
  public Collection<SabPerson> getPersonsInRoleForOrganization(String organisationAbbreviation, String role) {
    List<SabPerson> result = new ArrayList<>();

    for (SabPerson sabPerson: sabPersons) {
      Collection<String> roleNames = Collections2.transform(sabPerson.getRoles(), new Function<SabRole, String>() {
        @Override
        public String apply(SabRole input) {
          return input.roleName;
        }
      });

      if (roleNames.contains(role)) {
        result.add(sabPerson);
      }
    }
    return result;
  }
}
