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
