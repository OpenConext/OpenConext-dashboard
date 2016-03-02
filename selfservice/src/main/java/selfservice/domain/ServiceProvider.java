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

import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("serial")
public class ServiceProvider extends Provider implements Serializable, Cloneable {

  private String applicationUrl;
  private String institutionId;
  private String eulaURL;

  private boolean idpVisibleOnly;
  private boolean publishedInEdugain;
  private boolean exampleSingleTenant;

  private ARP arp;

  private Map<String, String> urls = new HashMap<>();

  public ServiceProvider(String id) {
    setId(id);
  }

  public ServiceProvider(Map<String, Object> metaData) {
    super(metaData);
    this.applicationUrl = (String) metaData.get("coin:application_url");
    this.institutionId = (String) metaData.get("coin:institution_id");
    this.eulaURL = (String) metaData.get("coin:eula");

    this.idpVisibleOnly = booleanValue(metaData.get("coin:ss:idp_visible_only"));
    this.publishedInEdugain = booleanValue(metaData.get("coin:publish_in_edugain"));
    this.arp = metaData.containsKey("attributes") ? ARP.fromAttributes((List<String>) metaData.get("attributes")) : ARP.fromRestResponse(new HashMap<>());

    addUrl("en", (String) metaData.get("url:en"));
    addUrl("nl", (String) metaData.get("url:en"));
  }

  public boolean isIdpVisibleOnly() {
    return idpVisibleOnly;
  }

  public String getEulaURL() {
    return eulaURL;
  }

  public Map<String, String> getUrls() {
    return urls;
  }

  public String getUrl() {
    return CollectionUtils.isEmpty(this.urls) ? null : urls.values().iterator().next();
  }

  private void addUrl(String lang, String url) {
    if (StringUtils.hasText(url)) {
      this.urls.put(lang, url);
    }
  }

  public ARP getArp() {
    return arp;
  }

  public boolean isPublishedInEdugain() {
    return publishedInEdugain;
  }

  public boolean isExampleSingleTenant() {
    return exampleSingleTenant;
  }

  public void setExampleSingleTenant(boolean exampleSingleTenant) {
    this.exampleSingleTenant = exampleSingleTenant;
  }

  public String getApplicationUrl() {
    return applicationUrl;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  @Override
  public String toString() {
    return "ServiceProvider{" +
      "id='" + getId() + '\'' +
      ", applicationUrl='" + applicationUrl + '\'' +
      ", institutionId='" + institutionId + '\'' +
      ", eulaURL='" + eulaURL + '\'' +
      ", idpVisibleOnly=" + idpVisibleOnly +
      ", publishedInEdugain=" + publishedInEdugain +
      ", exampleSingleTenant=" + exampleSingleTenant +
      ", arp=" + arp +
      ", urls=" + urls +
      '}';
  }

  public ServiceProvider clone()  {
    try {
      return (ServiceProvider) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new RuntimeException(e);
    }
  }

}
