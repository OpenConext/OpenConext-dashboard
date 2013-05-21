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

package nl.surfnet.coin.selfservice.api.model;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize(include=JsonSerialize.Inclusion.NON_NULL)
public class Service implements Comparable<Service> {

  private String name;
  @JsonProperty("logo_url_service")
  private String logoUrl;
  @JsonProperty("website_service")
  private String websiteUrl;
  @JsonProperty("is_surfmarket_connected")
  private boolean hasCrmLink;
  @JsonProperty("surfmarket_url")
  private String crmLink;

  public Service(String name, String logoUrl, String websiteUrl, boolean hasCrmLink, String crmLink) {
    this.name = name;
    this.logoUrl = logoUrl;
    this.websiteUrl = websiteUrl;
    this.hasCrmLink = hasCrmLink;
    this.crmLink = crmLink;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }

  public void setWebsiteUrl(String websiteUrl) {
    this.websiteUrl = websiteUrl;
  }

  public boolean isHasCrmLink() {
    return hasCrmLink;
  }

  public void setHasCrmLink(boolean hasCrmLink) {
    this.hasCrmLink = hasCrmLink;
  }

  public String getCrmLink() {
    return crmLink;
  }

  public void setCrmLink(String crmLink) {
    this.crmLink = crmLink;
  }

  @Override
  public int compareTo(Service other) {
    if (other == null) {
      return 1;
    }
    String otherName =  other.getName();
    if (this.name == null && otherName == null ) {
      return -1;
    }
    if (this.name == null) {
      return -1;
    }
    if (otherName == null) {
      return 1;
    }
    return this.name.compareTo(otherName);
  }
}
