package csa.shibboleth;

/**
 * Represents the data about the user that is provided to us by Shibboleth
 */
public class ShibbolethPrincipal {

  private final String uid;
  private final String displayName;
  private final String email;
  private final String idpId;

  public String getIdpId() {
    return idpId;
  }

  public ShibbolethPrincipal(String uid, String displayName, String email, String idpId) {
    this.uid = uid;
    this.displayName = displayName;
    this.email = email;
    this.idpId = idpId;
  }

  public String getUid() {
    return uid;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getEmail() {
    return email;
  }

  @Override
  public String toString() {
    return "ShibbolethPrincipal{" +
      "uid='" + uid + '\'' +
      ", displayName='" + displayName + '\'' +
      ", email='" + email + '\'' +
      ", idpId='" + idpId + '\'' +
      '}';
  }
}
