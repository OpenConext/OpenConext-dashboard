package nl.surfnet.coin.selfservice.api.model;

import java.util.ArrayList;
import java.util.List;

import org.codehaus.jackson.map.annotate.JsonSerialize;

@JsonSerialize
public class LicenseInformationResponse {
  private String idpEntityId;
  private List<LicenseInformation> licenses;
  
  public LicenseInformationResponse() {
    super();
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

  public void setIdpEntityId(String idpEntityId) {
    this.idpEntityId = idpEntityId;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }  
}
