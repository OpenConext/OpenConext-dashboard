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

package nl.surfnet.coin.selfservice.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import org.junit.Test;

import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.PersonAttributeLabelService;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;

/**
 * Test class for {@code PersonAttributeLabelServiceJsonImpl}
 */
public class PersonAttributeLabelServiceJsonImplTest {

  @Test
  public void testParseJsonToAttributeLabels() throws IOException {
    PersonAttributeLabelService serviceJsonImplPerson =
        new PersonAttributeLabelServiceJsonImpl("classpath:person_attributes.json");

    Map<String, PersonAttributeLabel> labels = serviceJsonImplPerson.getAttributeLabelMap();
    assertFalse(labels.isEmpty());
    final PersonAttributeLabel label = labels.get("urn:mace:dir:attribute-def:uid");
    assertNotNull(label);
    assertEquals("UID", label.getNames().get("en"));
    assertEquals("your unique username within your organization", label.getDescriptions().get("en"));
  }

  @Test
  public void testParseEmptyJsonToAttributeLabels() throws Exception {
    PersonAttributeLabelServiceJsonImpl serviceJsonImplPerson = new PersonAttributeLabelServiceJsonImpl("");
    InputStream stream = new ByteArrayInputStream(new byte[]{});
    Map<String, PersonAttributeLabel> labels = serviceJsonImplPerson.parseStreamToAttributeLabelMap(stream);
    assertTrue(labels.isEmpty());
  }
}
