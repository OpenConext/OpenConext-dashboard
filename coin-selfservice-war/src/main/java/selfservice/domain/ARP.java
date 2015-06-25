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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Attribute Release Policy
 */
public class ARP implements Serializable {

  private static final long serialVersionUID = 0L;
  private String name;
  private String description;
  private Map<String, List<Object>> attributes = new LinkedHashMap<String, List<Object>>();
  private boolean noArp;
  private boolean noAttrArp;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Map<String, List<Object>> getAttributes() {
    return attributes;
  }

  public void setAttributes(Map<String, List<Object>> attributes) {
    this.attributes = attributes;
  }

  public static ARP fromRestResponse(Map response) {
    ARP arp = new ARP();

    arp.setNoArp(false);
    arp.setNoAttrArp(false);

    if (response.isEmpty()) {
      arp.setNoArp(true);
      return arp;
    }

    arp.setName((String) response.get("name"));
    arp.setDescription((String) response.get("description"));
    final Object attr = response.get("attributes");
    if (attr instanceof Map) {
      arp.setAttributes((Map<String, List<Object>>) attr);
    } else {
      // If 'no attributes', Janus will return not a hash, but an empty array
      arp.setNoAttrArp(true);
    }
    return arp;
  }

  @Override
  public String toString() {
    final StringBuffer sb = new StringBuffer();
    sb.append("ARP");
    sb.append("{name='").append(name).append('\'');
    sb.append(", description='").append(description).append('\'');
    sb.append(", attributes=").append(attributes);
    sb.append('}');
    return sb.toString();
  }

  public boolean isNoArp() {
    return noArp;
  }

  public boolean isNoAttrArp() {
    return noAttrArp;
  }

  public void setNoArp(boolean noArp) {
    this.noArp = noArp;
  }

  public void setNoAttrArp(boolean noAttrArp) {
    this.noAttrArp = noAttrArp;
  }
}
