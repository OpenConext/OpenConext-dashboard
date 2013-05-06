package nl.surfnet.coin.selfservice.api.model;

import nl.surfnet.coin.selfservice.domain.License;

public class LicenseInformation {
  private final String spEntityId;
  private final LicenseStatus status;
  private final License license;
  
  public LicenseInformation(final String spEntityId, final LicenseStatus status, final License license) {
    this.spEntityId = spEntityId;
    this.status = status;
    this.license = license;
  }

  public String getSpEntityId() {
    return spEntityId;
  }

  public LicenseStatus getStatus() {
    return status;
  }

  public License getLicense() {
    return license;
  }
}

enum LicenseStatus {
  AVAILABLE, UNAVAILABLE, UNKNOWN;
}
