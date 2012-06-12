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

import java.util.HashMap;
import java.util.Map;

/**
 * Labels for an attribute, like
 * <pre>
 * "urn:mace:dir:attribute-def:uid":{
 * "Name":{
 * "nl":"UID",
 * "en":"UID"
 * },
 * "Description":{
 * "nl":"jouw unieke gebruikersnaam binnen jouw instelling",
 * "en":"your unique username within your organization"
 * }</pre>
 */
public class PersonAttributeLabel {
  private String key;
  private Map<String, String> names = new HashMap<String, String>();
  private Map<String, String> descriptions = new HashMap<String, String>();

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public Map<String, String> getNames() {
    return names;
  }

  public void setNames(Map<String, String> names) {
    this.names = names;
  }

  public void addName(String language, String value) {
    this.names.put(language, value);
  }

  public Map<String, String> getDescriptions() {
    return descriptions;
  }

  public void setDescriptions(Map<String, String> descriptions) {
    this.descriptions = descriptions;
  }

  public void addDescription(String language, String value) {
    this.descriptions.put(language, value);
  }
}
