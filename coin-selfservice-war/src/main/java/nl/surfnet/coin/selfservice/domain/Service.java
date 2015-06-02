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

package nl.surfnet.coin.selfservice.domain;

import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
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

  private List<String> screenshotUrls = new ArrayList<>();

  private String supportMail;

  private String enduserDescription;

  private String institutionDescription;

  private String institutionId;

  private Date lastLoginDate;

  /**
   * Whether this service is connected to the IdP in the service registry
   */
  private boolean connected;

  /**
   * Whether this service is connected to an item in the CRM
   */
  private boolean hasCrmLink;

  /**
   * The article in the CRM this service is linked to. If set, hasCrmLink is true;
   */
  private CrmArticle crmArticle;

  /**
   * The license from the CRM. If set, hasCrmLink is true
   */
  private License license;

  private List<Category> categories = new ArrayList<>();

  private String spEntityId;

  private String spName;

  private ARP arp;

  private boolean availableForEndUser;

  private boolean idpVisibleOnly;

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

  public void restoreCategoryReferences() {
    if (categories == null) {
      return;
    }
    for (Category category : categories) {
      List<CategoryValue> values = category.getValues();
      for (CategoryValue value : values) {
        value.setCategory(category);
      }
    }
  }

  public Date getLastLoginDate() {
    return lastLoginDate;
  }

  public void setLastLoginDate(Date lastLoginDate) {
    this.lastLoginDate = lastLoginDate;
  }

  public boolean isAvailableForEndUser() {
    return availableForEndUser;
  }

  public void setAvailableForEndUser(boolean availableForEndUser) {
    this.availableForEndUser = availableForEndUser;
  }

  public boolean isIdpVisibleOnly() {
    return idpVisibleOnly;
  }

  public void setIdpVisibleOnly(boolean idpVisibleOnly) {
    this.idpVisibleOnly = idpVisibleOnly;
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

  @Override
  public String toString() {
    return "Service{" +
      "id=" + id +
      ", name='" + name + '\'' +
      '}';
  }
}
