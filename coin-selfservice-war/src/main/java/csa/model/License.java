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

package csa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;
import java.util.Date;

/**
 * License (for software, SaaS, or other) that belongs to an institute, group of
 * individual. A license is typically retrieved from the LMNG.
 */
public class License implements Serializable {

  private static final long serialVersionUID = 0L;

  public enum LicenseStatus {

    HAS_LICENSE_SURFMARKET,
    HAS_LICENSE_SP,
    NO_LICENSE,
    NOT_NEEDED,
    UNKNOWN

  }

  private Date startDate;
  private Date endDate;
  private String licenseNumber;
  private String institutionName;
  private boolean groupLicense;
  
  /**
   * Default constructor
   */
  public License() {
  }

  public License(Date startDate, Date endDate, String licenseNumber, String institutionName) {
    super();
    this.startDate = startDate;
    this.endDate = endDate;
    this.licenseNumber = licenseNumber;
    this.institutionName = institutionName;
  }

  public Date getStartDate() {
    return startDate;
  }

  public void setStartDate(Date startDate) {
    this.startDate = startDate;
  }

  public Date getEndDate() {
    return endDate;
  }

  public void setEndDate(Date endDate) {
    this.endDate = endDate;
  }

  public String getLicenseNumber() {
    return licenseNumber;
  }

  public void setLicenseNumber(String licenseNumber) {
    this.licenseNumber = licenseNumber;
  }

  public String getInstitutionName() {
    return institutionName;
  }

  public void setInstitutionName(String institutionName) {
    this.institutionName = institutionName;
  }

  @Override
  public String toString() {
    return "License [startDate=" + startDate + ", endDate=" + endDate + ", licenseNumber=" + licenseNumber + ", institutionName="
        + institutionName + "]";
  }

  public boolean isGroupLicense() {
    return groupLicense;
  }

  public void setGroupLicense(boolean groupLicense) {
    this.groupLicense = groupLicense;
  }

  @JsonIgnore
  public boolean isValid() {
    return endDate == null || endDate.getTime() > System.currentTimeMillis();
  }


}
