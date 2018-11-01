package dashboard.domain;

public enum LicenseStatus {

  HAS_LICENSE_SURFMARKET("Yes, with SURFmarket"),
  HAS_LICENSE_SP("Yes, with service provider"),
  NOT_NEEDED("Not needed"),
  UNKNOWN("Unknown");

  private String name;

  LicenseStatus(final String name) {
    this.name = name;
  }

  public static LicenseStatus fromManage(String licenseStatus) {
    if (licenseStatus == null) {
      return UNKNOWN;
    }
    switch (licenseStatus) {
      case "license_required_by_service_provider":
        return HAS_LICENSE_SP;
      case "license_not_required":
        return NOT_NEEDED;
      case "license_available_through_surfmarket":
        return HAS_LICENSE_SURFMARKET;
      default:
        return UNKNOWN;
    }
  }

  public String getName() {
    return name;
  }

  @Override
  public String toString() {
    return this.name();
  }
}
