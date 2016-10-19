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
package selfservice.domain;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.google.common.base.MoreObjects;

@JsonInclude(NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Service implements Comparable<Service>, Serializable {

  private static final long serialVersionUID = 0L;

  private long id;

  private String name;
  private String description;
  private String logoUrl;
  private String websiteUrl;
  private String appUrl;
  private String serviceUrl;
  private String crmUrl;
  private String detailLogoUrl;
  private String supportUrl;
  private String eulaUrl;
  private String wikiUrl;
  private String supportMail;
  private String enduserDescription;
  private String institutionDescription;
  private String institutionId;
  private String spEntityId;
  private String spName;
  private String normenkaderUrl;
  private String interfedSource;
  private String privacyStatementUrl;
  private String registrationInfoUrl;
  private String registrationPolicyUrl;

  private List<String> screenshotUrls = new ArrayList<>();
  private List<Category> categories = new ArrayList<>();

  private boolean connected;
  private boolean hasCrmLink;
  private boolean idpVisibleOnly;
  private boolean publishedInEdugain;
  private boolean normenkaderPresent;
  private boolean exampleSingleTenant;
  private boolean policyEnforcementDecisionRequired;

  private CrmArticle crmArticle;

  private License license;
  private LicenseStatus licenseStatus;

  private ARP arp;

  public Service() {
  }

  public Service(long id, String name, String logoUrl, String websiteUrl, boolean hasCrmLink, String crmUrl, String spEntityId) {
    this.id = id;
    this.name = name;
    this.logoUrl = logoUrl;
    this.websiteUrl = websiteUrl;
    this.hasCrmLink = hasCrmLink;
    this.crmUrl = crmUrl;
    this.spEntityId = spEntityId;
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

  public String getCrmUrl() {
    return crmUrl;
  }

  public void setCrmUrl(String crmUrl) {
    this.crmUrl = crmUrl;
  }

  public boolean isConnected() {
    return connected;
  }

  public void setConnected(boolean connected) {
    this.connected = connected;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public License getLicense() {
    return license;
  }

  public void setLicense(License license) {
    this.license = license;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }

  public String getAppUrl() {
    return appUrl;
  }

  public void setAppUrl(String appUrl) {
    this.appUrl = appUrl;
  }

  public String getSpEntityId() {
    return spEntityId;
  }

  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getServiceUrl() {
    return serviceUrl;
  }

  public void setServiceUrl(String serviceUrl) {
    this.serviceUrl = serviceUrl;
  }

  public String getDetailLogoUrl() {
    return detailLogoUrl;
  }

  public void setDetailLogoUrl(String detailLogoUrl) {
    this.detailLogoUrl = detailLogoUrl;
  }

  public CrmArticle getCrmArticle() {
    return crmArticle;
  }

  public void setCrmArticle(CrmArticle crmArticle) {
    this.crmArticle = crmArticle;
  }

  public String getSupportUrl() {
    return supportUrl;
  }

  public void setSupportUrl(String supportUrl) {
    this.supportUrl = supportUrl;
  }

  public String getEulaUrl() {
    return eulaUrl;
  }

  public void setEulaUrl(String eulaUrl) {
    this.eulaUrl = eulaUrl;
  }

  public List<String> getScreenshotUrls() {
    return screenshotUrls;
  }

  public void setScreenshotUrls(List<String> screenshotUrls) {
    this.screenshotUrls = screenshotUrls;
  }

  public String getSupportMail() {
    return supportMail;
  }

  public void setSupportMail(String supportMail) {
    this.supportMail = supportMail;
  }

  public String getEnduserDescription() {
    return enduserDescription;
  }

  public void setEnduserDescription(String enduserDescription) {
    this.enduserDescription = enduserDescription;
  }

  public String getInstitutionDescription() {
    return institutionDescription;
  }

  public void setInstitutionDescription(String institutionDescription) {
    this.institutionDescription = institutionDescription;
  }

  public ARP getArp() {
    return arp;
  }

  public void setArp(ARP arp) {
    this.arp = arp;
  }

  public boolean isIdpVisibleOnly() {
    return idpVisibleOnly;
  }

  public void setIdpVisibleOnly(boolean idpVisibleOnly) {
    this.idpVisibleOnly = idpVisibleOnly;
  }

  public boolean isPolicyEnforcementDecisionRequired() {
    return policyEnforcementDecisionRequired;
  }

  public void setPolicyEnforcementDecisionRequired(boolean policyEnforcementDecisionRequired) {
    this.policyEnforcementDecisionRequired = policyEnforcementDecisionRequired;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  public String getSpName() {
    return spName;
  }

  public void setSpName(String spName) {
    this.spName = spName;
  }

  public String getWikiUrl() {
    return wikiUrl;
  }

  public void setWikiUrl(String wikiUrl) {
    this.wikiUrl = wikiUrl;
  }

  public boolean isPublishedInEdugain() {
    return publishedInEdugain;
  }

  public void setPublishedInEdugain(boolean publishedInEdugain) {
    this.publishedInEdugain = publishedInEdugain;
  }

  public LicenseStatus getLicenseStatus() {
    return licenseStatus;
  }

  public void setLicenseStatus(LicenseStatus licenseStatus) {
    this.licenseStatus = licenseStatus;
  }

  public boolean isNormenkaderPresent() {
    return normenkaderPresent;
  }

  public void setNormenkaderPresent(boolean normenkaderPresent) {
    this.normenkaderPresent = normenkaderPresent;
  }

  public String getNormenkaderUrl() {
    return normenkaderUrl;
  }

  public void setNormenkaderUrl(String normenkaderUrl) {
    this.normenkaderUrl = normenkaderUrl;
  }

  public boolean isExampleSingleTenant() {
    return exampleSingleTenant;
  }

  public void setExampleSingleTenant(boolean exampleSingleTenant) {
    this.exampleSingleTenant = exampleSingleTenant;
  }

  public String getInterfedSource() {
    return interfedSource;
  }

  public void setInterfedSource(String interfedSource) {
    this.interfedSource = interfedSource;
  }
  
  public String getPrivacyStatementUrl() {
    return privacyStatementUrl;
  }

  public void setPrivacyStatementUrl(String privacyStatementUrl) {
    this.privacyStatementUrl = privacyStatementUrl;
  }

  public String getRegistrationInfoUrl() {
    return registrationInfoUrl;
  }

  public void setRegistrationInfoUrl(String registrationInfo) {
    this.registrationInfoUrl = registrationInfo;
  }
  
  public String getRegistrationPolicyUrl() {
    return registrationPolicyUrl;
  }

  public void setRegistrationPolicyUrl(String registrationPolicyUrl) {
    this.registrationPolicyUrl = registrationPolicyUrl;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Service service = (Service) o;

    if (id != service.id) return false;
    if (name != null ? !name.equals(service.name) : service.name != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = (int) (id ^ (id >>> 32));
    result = 31 * result + (name != null ? name.hashCode() : 0);
    return result;
  }

  @Override
  public int compareTo(Service other) {
    if (other == null) {
      return 1;
    }
    String otherName = other.getName();
    if (this.name == null && otherName == null) {
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

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
        .add("id", id)
        .add("name", name).toString();
  }

}
