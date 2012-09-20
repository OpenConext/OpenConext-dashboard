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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.opensaml.saml2.core.Assertion;
import org.opensaml.saml2.core.Attribute;
import org.opensaml.saml2.core.AttributeStatement;
import org.opensaml.xml.XMLObject;

/**
 * Util class for person Attributes
 */
public final class PersonAttributeUtil {

  private static final List<String> ATTRIBUTE_FILTER = attributeFilter();

  private PersonAttributeUtil() {

  }

  /**
   * Takes the SAML assertion and returns the user attributes as a Map
   *
   * @param assertion SAML {@link Assertion}
   * @return {@link Map} with the attribute name as key and a List of String values as value
   */
  public static Map<String, List<String>> getAttributesAsMap(Assertion assertion) {
    Map<String, List<String>> m = new HashMap<String, List<String>>();
    final List<AttributeStatement> attributeStatements = assertion.getAttributeStatements();
    for (AttributeStatement attributeStatement : attributeStatements) {
      final List<Attribute> attributes = attributeStatement.getAttributes();
      for (Attribute attribute : attributes) {
        if (ATTRIBUTE_FILTER.contains(attribute.getName())) {
          continue;
        }
        final List<XMLObject> attributeValues = attribute.getAttributeValues();
        if (attributeValues.isEmpty()) {
          continue;
        }
        List<String> values = new ArrayList<String>(attributeValues.size());
        for (XMLObject value : attributeValues) {
          values.add(value.getDOM().getFirstChild().getNodeValue());
        }
        m.put(attribute.getName(), values);
      }
    }
    return m;
  }

  /**
   * List of attributes that can be filtered
   * Copied from https://raw.github.com/OpenConext/OpenConext-engineblock/master/application/modules/Default/Controller/LoggedIn.php
   */
  private static List<String> attributeFilter() {
    return Arrays.asList(
        "urn:oid:2.5.4.42",
        "urn:oid:2.5.4.3",
        "urn:oid:2.5.4.4",
        "urn:oid:2.16.840.1.113730.3.1.241",
        "urn:oid:0.9.2342.19200300.100.1.1",
        "urn:oid:0.9.2342.19200300.100.1.3",
        "urn:oid:1.3.6.1.4.1.1466.115.121.1.15",
        "urn:oid:1.3.6.1.4.1.5923.1.1.1.6",
        "coin:",
        "urn:nl.surfconext.licenseInfo",
        "urn:mace:dir:attribute-def:isMemberOf",
        "urn:oid:1.3.6.1.4.1.1076.20.40.40.1",
        "urn:oid:1.3.6.1.4.1.5923.1.1.1.10",
        "urn:mace:dir:attribute-def:eduPersonTargetedID");
  }

}
