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
package dashboard.sab;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

    private static final SabRole ROLE_BEHEERDER = new SabRole("CONBEH", "SURFconextbeheerder");
    private static final SabRole ROLE_VERANTWOORDELIJKE = new SabRole("CONVER", "SURFconextverantwoordelijke");

    private final List<SabPerson> sabPersons = ImmutableList.of(
            new SabPerson("Hans", "", "Janssen", "hjanssen", "hjanssen@surfnet.nl", asList(ROLE_BEHEERDER)),
            new SabPerson("Raoul", "var der", "Teeuwen", "rteeuwen", "raoul.teeuwen@surfnet.nl", asList(ROLE_VERANTWOORDELIJKE)),
            new SabPerson("Okke", "", "Harsta", "oharsta", "oharsta@zilverline.nl", asList(ROLE_VERANTWOORDELIJKE))
    );

    /**
     * Mapping of userIds to roles
     */
    private final Map<String, SabRoleHolder> rolesMapping = ImmutableMap.of(
            "admin", new SabRoleHolder("SURFNET", Arrays.asList(ROLE_VERANTWOORDELIJKE.roleName)),
            "viewer", new SabRoleHolder("SURFNET", Arrays.asList(ROLE_BEHEERDER.roleName)),
            "noroles", new SabRoleHolder("SURFNET", Collections.emptyList())
    );

    @Override
    public List<SabPerson> getPersonsInRoleForOrganization(String organisationGuid, String role) {
        return sabPersons.stream()
                .filter(person -> person.getRoles().stream().anyMatch(r -> r.roleName.equals(role)))
                .toList();
    }

    @Override
    public List<SabPerson> getSabEmailsForOrganization(String entityId, String role) {
        return this.getPersonsInRoleForOrganization(null, role);
    }
}
