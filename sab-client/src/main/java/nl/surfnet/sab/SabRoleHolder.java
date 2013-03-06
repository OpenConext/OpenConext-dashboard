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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

public class SabRoleHolder implements Serializable {

  private static final long serialVersionUID = 1L;

  private final List<String> roles;
  private final String organization;

  public SabRoleHolder(String organization, List<String> roles) {
    this.organization = organization;
    this.roles = Collections.unmodifiableList(roles);
  }

  public String getOrganization() {
    return organization;
  }

  public List<String> getRoles() {
    return roles;
  }
}
