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

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import nl.surfnet.coin.selfservice.domain.PersonAttributeLabel;
import nl.surfnet.coin.selfservice.service.PersonAttributeLabelService;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * Parses a json file into {@link nl.surfnet.coin.selfservice.domain.PersonAttributeLabel}
 */
public class PersonAttributeLabelServiceJsonImpl implements PersonAttributeLabelService {

  private static final String NAME = "Name";
  private static final String DESCRIPTION = "Description";
  private static final String[] LANGUAGES = {"en", "nl"};

  private Map<String, PersonAttributeLabel> labelMap;

  private String attributeJsonFile;

  public PersonAttributeLabelServiceJsonImpl(String attributeJsonFile) {
    this.attributeJsonFile = attributeJsonFile;
    try {
      populate();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private void populate() throws IOException {
    Resource jsonResource;
    if (attributeJsonFile.startsWith("classpath:")) {
      jsonResource = new ClassPathResource(attributeJsonFile.substring("classpath:".length()));
    } else {
      jsonResource = new FileSystemResource(attributeJsonFile);
    }
    labelMap = parseStreamToAttributeLabelMap(IOUtils.toString(jsonResource.getInputStream()));
  }


  public Map<String, PersonAttributeLabel> getAttributeLabelMap() {
    return labelMap;
  }

  /**
   * @param src {@link InputStream} of the Json data
   * @return {@link Map}
   */
  Map<String, PersonAttributeLabel> parseStreamToAttributeLabelMap(String src) throws IOException {
    Map<String, PersonAttributeLabel> m = new HashMap<String, PersonAttributeLabel>();
    ObjectMapper mapper = new ObjectMapper();

    JsonNode rootNode = mapper.readValue(src, JsonNode.class);
    final Iterator<Map.Entry<String, JsonNode>> fields = rootNode.getFields();

    while (fields.hasNext()) {
      final Map.Entry<String, JsonNode> labelField = fields.next();
      PersonAttributeLabel a = makeAttributeLabel(labelField);
      m.put(a.getKey(), a);
    }

    return m;
  }

  /**
   * Converts a Map.Entry into an PersonAttributeLabel
   *
   * @param labelField {@link Map.Entry} with the attribute key as key and sub {@link JsonNode}'s as values
   * @return {@link nl.surfnet.coin.selfservice.domain.PersonAttributeLabel}
   */
  private PersonAttributeLabel makeAttributeLabel(Map.Entry<String, JsonNode> labelField) {
    PersonAttributeLabel a = new PersonAttributeLabel();
    a.setKey(labelField.getKey());

    final JsonNode nameAndDesc = labelField.getValue();

    if (nameAndDesc != null) {
      final JsonNode nameNode = nameAndDesc.get(NAME);
      if (nameNode != null) {
        a.setNames(getTranslatedLabels(nameNode));
      }
      final JsonNode descNode = nameAndDesc.get(DESCRIPTION);
      if (descNode != null) {
        a.setDescriptions(getTranslatedLabels(descNode));
      }
    }
    return a;
  }

  /**
   * Name and Description have subnodes for "en", "nl" etc. These are returned as Map with the
   * <pre>{
   * "nl":"Voornaam",
   * "en":"Name"
   * }</pre>
   *
   * @param node {@link JsonNode}
   * @return Map with key-value for language and label
   */
  private Map<String, String> getTranslatedLabels(JsonNode node) {
    Map<String, String> m = new HashMap<String, String>();
    for (String lang : LANGUAGES) {
      if (node.has(lang)) {
        m.put(lang, node.get(lang).getTextValue());
      }
    }
    return m;
  }

  public String getAttributeJsonFile() {
    return attributeJsonFile;
  }

}
