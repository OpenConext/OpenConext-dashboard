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
import java.util.HashSet;
import java.util.Set;

import nl.surfnet.coin.selfservice.domain.Field.Key;

/**
 * AttributeScopeConstraints.java
 * 
 * What may not be seen by the various roles?
 * 
 * https://wiki.surfnetlabs.nl/display/services/App-omschrijving
 * 
 */
@SuppressWarnings("serial")
public class AttributeScopeConstraints implements Serializable{

  private Set<Key> keys;

  public void addAttributeScopeConstraint(Key... constraints) {
    if (this.keys == null) {
      this.keys = new HashSet<Field.Key>();
    }
    for (Key key : constraints) {
      this.keys.add(key);
    }
  }

  public boolean isAllowed(Key key) {
    return this.keys != null ? !this.keys.contains(key) : true;
  }

}
