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

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

  private static final SabRole ROLE_BEHEERDER = new SabRole("CONBEH", "SURFconextbeheerder");
  private static final SabRole ROLE_VERANTWOORDELIJKE = new SabRole("CONVER", "SURFconextverantwoordelijke");

  private final List<SabPerson> sabPersons = ImmutableList.of(
    new SabPerson("Hans", "Janssen", "hjanssen", asList(ROLE_BEHEERDER)),
    new SabPerson("Frans", "Franssen", "ffransen", asList(ROLE_BEHEERDER, ROLE_VERANTWOORDELIJKE))
  );

  @Override
  public Collection<SabPerson> getPersonsInRoleForOrganization(String organisationAbbreviation, String role) {
      return sabPersons.stream()
        .filter(person -> person.getRoles().stream().anyMatch(r -> r.roleName.equals(role)))
        .collect(Collectors.toList());
  }
}
