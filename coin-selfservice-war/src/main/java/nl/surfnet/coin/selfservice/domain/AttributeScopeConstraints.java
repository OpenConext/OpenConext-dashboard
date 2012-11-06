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
package nl.surfnet.coin.selfservice.domain;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority;
import static nl.surfnet.coin.selfservice.domain.CoinAuthority.Authority.*;
import nl.surfnet.coin.selfservice.domain.Field.Key;
import static nl.surfnet.coin.selfservice.domain.Field.Key.*;
import static java.util.Arrays.asList;

/**
 * AttributeScopeConstraints.java
 * 
 * What may not be seen by the various roles?
 * 
 * https://wiki.surfnetlabs.nl/display/services/App-omschrijving
 * 
 */
@SuppressWarnings("serial")
public class AttributeScopeConstraints implements Serializable {

  private HashSet<Key> keys;

  // private constructor as we want to enforce builder
  private AttributeScopeConstraints() {
  }

  private static Map<Key, Collection<Authority>> constraints = new HashMap<Field.Key, Collection<Authority>>();

  static {
    constraints.put(INSTITUTION_DESCRIPTION_EN, asList(ROLE_USER));
    constraints.put(INSTITUTION_DESCRIPTION_NL, asList(ROLE_USER));
    constraints.put(TECHNICAL_SUPPORTMAIL, asList(ROLE_USER));
    constraints.put(ENDUSER_DESCRIPTION_EN, asList(ROLE_IDP_LICENSE_ADMIN, ROLE_IDP_SURFCONEXT_ADMIN));
    constraints.put(ENDUSER_DESCRIPTION_NL, asList(ROLE_IDP_LICENSE_ADMIN, ROLE_IDP_SURFCONEXT_ADMIN));

  }

  public static AttributeScopeConstraints builder(Collection<Authority> authorities) {
    AttributeScopeConstraints result = new AttributeScopeConstraints();
    for (Entry<Key, Collection<Authority>> entry : constraints.entrySet()) {
      if (entry.getValue().containsAll(authorities)) {
        result.addAttributeScopeConstraint(entry.getKey());
      }
    }
    return result;
  }

  private void addAttributeScopeConstraint(Key key) {
    if (this.keys == null) {
      this.keys = new HashSet<Key>();
    }
    this.keys.add(key);
  }

  public boolean isAllowed(Key key) {
    return this.keys != null ? !this.keys.contains(key) : true;
  }

}
