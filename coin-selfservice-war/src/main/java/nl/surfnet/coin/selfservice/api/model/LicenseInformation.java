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

import nl.surfnet.coin.selfservice.domain.License;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class LicenseInformation {
  private String spEntityId;
  private LicenseStatus status;
  private License license;
  
  public String getSpEntityId() {
    return spEntityId;
  }
  
  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }
  
  public LicenseStatus getStatus() {
    return status;
  }
  
  public void setStatus(LicenseStatus status) {
    this.status = status;
  }
  
  public License getLicense() {
    return license;
  }
  
  public void setLicense(License license) {
    this.license = license;
  }
}
