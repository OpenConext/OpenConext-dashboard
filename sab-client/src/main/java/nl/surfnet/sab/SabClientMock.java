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
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Mock implementation of SAB client that uses a predefined mapping of userIds to SabRoleHolders
 */
public class SabClientMock implements Sab {

  /**
   * Mapping of userIds to roles
   */
  private Map<String, SabRoleHolder> rolesMapping = new ConcurrentHashMap<String, SabRoleHolder>();


  public SabClientMock() {
    rolesMapping.put("urn:collab:person:example.com:user1", new SabRoleHolder("SURFNET", Arrays.asList("Foo", "Bar")));
    rolesMapping.put("urn:collab:person:example.com:user2", new SabRoleHolder("SURFNET", Arrays.asList("Foo", "Baz")));
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
    return rolesMapping.get(userId);
  }

  public void setTransport(SabTransport transport) {
    // added this method to be able to use it as a full replacement for the regular SabClient.
  }
}
