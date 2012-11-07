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

package nl.surfnet.coin.selfservice.util;

import java.util.List;
import java.util.Map;

/**
 * Simple pojo containing the main attributes of a person. These main attributes
 * will be displayed to the normal user by default.
 * 
 */
public class PersonMainAttributes {
  
  public static final String USER_ATTRIBUTE_DISPLAY_NAME = "urn:mace:dir:attribute-def:displayName";
  public static final String USER_ATTRIBUTE_MAIL = "urn:mace:dir:attribute-def:mail";
  public static final String USER_ATTRIBUTE_SCHAC_HOME_ORGANISATIONS = "urn:mace:terena.org:attribute-def:schacHomeOrganization";

  String mail;
  String displayName;
  String schacHomeOrganization ;
  
  /**
   * Constructor that initializes the object with fields from the given attributeMap
   * @param attributeMap the map with attributes
   */
  public PersonMainAttributes(Map<String, List<String>> attributeMap) {
    mail = attributeMap.get(USER_ATTRIBUTE_MAIL) == null ? null : attributeMap.get(USER_ATTRIBUTE_MAIL).get(0);
    displayName = attributeMap.get(USER_ATTRIBUTE_DISPLAY_NAME) == null ? null : attributeMap.get(USER_ATTRIBUTE_DISPLAY_NAME).get(0);
    schacHomeOrganization = attributeMap.get(USER_ATTRIBUTE_SCHAC_HOME_ORGANISATIONS) == null ? null : attributeMap.get(USER_ATTRIBUTE_SCHAC_HOME_ORGANISATIONS).get(0);
    mail = mail + "XXXX";
  }

  public String getMail() {
    return mail;
  }

  public void setMail(String mail) {
    this.mail = mail;
  }

  public String getDisplayName() {
    return displayName;
  }

  public void setDisplayName(String displayName) {
    this.displayName = displayName;
  }

  public String getSchacHomeOrganization() {
    return schacHomeOrganization;
  }

  public void setSchacHomeOrganization(String schacHomeOrganization) {
    this.schacHomeOrganization = schacHomeOrganization;
  }
}
