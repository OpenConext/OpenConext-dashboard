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

  private final String applicationUrl;
  private final String institutionId;
  private final String eulaURL;
  private final String interfedSource;
  private final String privacyStatementUrlEn;
  private final String privacyStatementUrlNl;
  private final String registrationInfo;
  private final String registrationPolicyUrlEn;
  private final String registrationPolicyUrlNl;
  
  private final boolean idpVisibleOnly;
  private final boolean publishedInEdugain;
  private final boolean policyEnforcementDecisionRequired;
  private boolean exampleSingleTenant;

  private final ARP arp;

  private final Map<String, String> urls = new HashMap<>();

  @SuppressWarnings("unchecked")
  public ServiceProvider(Map<String, Object> metaData) {
    super(metaData);
    this.applicationUrl = (String) metaData.get("coin:application_url");
    this.institutionId = (String) metaData.get("coin:institution_id");
    this.eulaURL = (String) metaData.get("coin:eula");
    this.interfedSource = (String) metaData.getOrDefault("coin:interfed_source", "SURFconext");
    this.privacyStatementUrlEn = (String) metaData.get("mdui:PrivacyStatementURL:en");
    this.privacyStatementUrlNl = (String) metaData.get("mdui:PrivacyStatementURL:nl");
    this.registrationInfo = (String) metaData.get("mdrpi:RegistrationInfo");
    this.registrationPolicyUrlEn = (String) metaData.get("mdrpi:RegistrationPolicy:en");
    this.registrationPolicyUrlNl = (String) metaData.get("mdrpi:RegistrationPolicy:nl");
    
    this.idpVisibleOnly = booleanValue(metaData.get("coin:ss:idp_visible_only"));
    this.publishedInEdugain = booleanValue(metaData.get("coin:publish_in_edugain"));
    this.policyEnforcementDecisionRequired = booleanValue(metaData.get("coin:policy_enforcement_decision_required"));
    this.arp = metaData.containsKey("attributes") ? ARP.fromAttributes((List<String>) metaData.get("attributes")) : ARP.fromRestResponse(new HashMap<>());

    addUrl("en", (String) metaData.get("url:en"));
    addUrl("nl", (String) metaData.get("url:nl"));
  }

  public boolean isIdpVisibleOnly() {
    return idpVisibleOnly;
  }

  public boolean isPolicyEnforcementDecisionRequired() {
    return policyEnforcementDecisionRequired;
  }

  public String getEulaURL() {
    return eulaURL;
  }
  
  public String getInterfedSource() {
    return interfedSource;
  }

  public String getPrivacyStatementUrlEn() {
    return privacyStatementUrlEn;
  }

  public String getPrivacyStatementUrlNl() {
    return privacyStatementUrlNl;
  }

  public String getRegistrationInfo() {
    return registrationInfo;
  }

  public String getRegistrationPolicyUrlEn() {
    return registrationPolicyUrlEn;
  }

  public String getRegistrationPolicyUrlNl() {
    return registrationPolicyUrlNl;
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
      ", policyEnforcementDecisionRequired=" + policyEnforcementDecisionRequired +
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
