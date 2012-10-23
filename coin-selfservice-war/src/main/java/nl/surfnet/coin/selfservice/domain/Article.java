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

package nl.surfnet.coin.selfservice.domain;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Article information. An article is our LMNG implementation of a service.
 * 
 */
public class Article {

  private List<License> licenses;

  private String endUserDescriptionNl;
  private String institutionDescriptionNl;
  private String serviceDescriptionNl;
  private String supplierName;
  private String detailLogo;
  private String specialConditions;
  private String articleState;
  private String lmngIdentifier;

  /**
   * Default constructor
   */
  public Article() {
  }

  /**
   * Field initializing constructor
   * 
   * @param endUserDescriptionNl
   * @param institutionDescriptionNl
   * @param serviceDescriptionNl
   * @param supplierName
   * @param detailLogo
   * @param specialConditions
   * @param articleState
   * @param lmngIdentifier
   */
  public Article(String endUserDescriptionNl, String institutionDescriptionNl, String serviceDescriptionNl, String supplierName,
      String detailLogo, String specialConditions, String articleState, String lmngIdentifier) {
    super();
    this.endUserDescriptionNl = endUserDescriptionNl;
    this.institutionDescriptionNl = institutionDescriptionNl;
    this.serviceDescriptionNl = serviceDescriptionNl;
    this.supplierName = supplierName;
    this.detailLogo = detailLogo;
    this.specialConditions = specialConditions;
    this.articleState = articleState;
    this.lmngIdentifier = lmngIdentifier;
  }

  public List<License> getLicenses() {
    return licenses;
  }

  public void setLicenses(List<License> licenses) {
    this.licenses = licenses;
  }

  public String getEndUserDescriptionNl() {
    return endUserDescriptionNl;
  }

  public void setEndUserDescriptionNl(String endUserDescriptionNl) {
    this.endUserDescriptionNl = endUserDescriptionNl;
  }

  public String getInstitutionDescriptionNl() {
    return institutionDescriptionNl;
  }

  public void setInstitutionDescriptionNl(String institutionDescriptionNl) {
    this.institutionDescriptionNl = institutionDescriptionNl;
  }

  public String getServiceDescriptionNl() {
    return serviceDescriptionNl;
  }

  public void setServiceDescriptionNl(String serviceDescriptionNl) {
    this.serviceDescriptionNl = serviceDescriptionNl;
  }

  public String getSupplierName() {
    return supplierName;
  }

  public void setSupplierName(String supplierName) {
    this.supplierName = supplierName;
  }

  public String getDetailLogo() {
    return detailLogo;
  }

  public void setDetailLogo(String detailLogo) {
    this.detailLogo = detailLogo;
  }

  public String getSpecialConditions() {
    return specialConditions;
  }

  public void setSpecialConditions(String specialConditions) {
    this.specialConditions = specialConditions;
  }

  public String getArticleState() {
    return articleState;
  }

  public void setArticleState(String articleState) {
    this.articleState = articleState;
  }

  public String getLmngIdentifier() {
    return lmngIdentifier;
  }

  public void setLmngIdentifier(String lmngIdentifier) {
    this.lmngIdentifier = lmngIdentifier;
  }

  public void addLicense(License license) {
    if (licenses == null) {
      licenses = new ArrayList<License>();
    }
    licenses.add(license);
  }
  
  
  // BELOW convenience methods for licenses
  
  /**
   * convenience method.
   * Assuming we have 1 license. return the first (and only?) item from the licenses list
   * @return the first license
   */
  public License getLicence() {
    if (licenses != null && licenses.size() > 0) {
      return licenses.get(0);
    }
    return null;
  }
  
  /**
   * convenience method.
   * @return the startDate of the first license
   */
  public Date getStartDate() {
    if (getLicence() != null) {
      return getLicence().getStartDate();
    }
    return null;
  }
  
  /**
   * convenience method.
   * @return the endDate of the first license
   */
  public Date getEndDate() {
    if (getLicence() != null) {
      return getLicence().getEndDate();
    }
    return null;
  }
  
  /**
   * convenience method.
   * @return the license number of the first license
   */
  public String getLicenseNumber() {
    if (getLicence() != null) {
      return getLicence().getLicenseNumber();
    }
    return null;
  }
  
  /**
   * convenience method.
   * @return the institution name of the first license
   */
  public String getInstitutionName() {
    if (getLicence() != null) {
      return getLicence().getInstitutionName();
    }
    return null;
  }
  
  /**
   * convenience method.
   * @return the startDate of the first license
   */
  public void setStartDate(Date startDate) {
    if (getLicence() != null) {
      getLicence().setStartDate(startDate);
    }
  }
  
  /**
   * convenience method.
   * @return the endDate of the first license
   */
  public void setEndDate(Date endDate) {
    if (getLicence() != null) {
      getLicence().setEndDate(endDate);
    }
  }
  
  /**
   * convenience method.
   * @return the license number of the first license
   */
  public void setLicenseNumber(String licenseNumber) {
    if (getLicence() != null) {
      getLicence().setLicenseNumber(licenseNumber);
    }
  }
  
  /**
   * convenience method.
   * @return the institution name of the first license
   */
  public void setInstitutionName(String institutionName) {
    if (getLicence() != null) {
      getLicence().setInstitutionName(institutionName);
    }
  }
  
}