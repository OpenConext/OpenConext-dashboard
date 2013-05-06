package nl.surfnet.coin.selfservice.api.model;

import java.util.ArrayList;
import java.util.List;

public class LicenseInformationResponse {
  private final String idpEntityId;
  private List<LicenseInformation> licenses;
  
  public LicenseInformationResponse(final String idpEntityId) {
    this.idpEntityId = idpEntityId;
  }
  
  public void addLicense(final LicenseInformation license) {
    if (null == licenses) {
      licenses = new ArrayList<LicenseInformation>();
    }
    licenses.add(license);
  }

  public List<LicenseInformation> getLicenses() {
    return licenses;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }  
}
