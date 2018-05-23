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
package selfservice.domain;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

@SuppressWarnings("serial")
public class IdentityProvider extends Provider implements Serializable {

  private String institutionId;
  private Map<String, String> keywords = new HashMap<>();

  public IdentityProvider() {
  }

  public IdentityProvider(String id, String institutionId, String name, Long eid) {
    setId(id);
    setEid(eid);
    this.institutionId = institutionId;
    if (StringUtils.isNotBlank(name)) {
      setName(name);
      addName("en", name);
      addName("nl", name);
    }
  }

  public IdentityProvider(Map<String, Object> metaData) {
    super(metaData);
    this.institutionId = (String) metaData.get("coin:institution_id");
    addKeywords("en", (String) metaData.get("keywords:en"));
    addKeywords("nl", (String) metaData.get("keywords:nl"));
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }
  
  public Map<String, String> getKeywords() {
    return keywords;
  }

  private void addKeywords(String language, String keywords) {
    if (keywords != null) {
      this.keywords.put(language, keywords);
    }
  }
  
  @Override
  public String toString() {
    return "IdentityProvider{" +
      "id='" + getId() + '\'' +
      ", institutionId='" + institutionId + '\'' +
      '}';
  }
}
