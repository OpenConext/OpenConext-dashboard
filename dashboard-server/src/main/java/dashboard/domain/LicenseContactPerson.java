package dashboard.domain;

import org.springframework.util.StringUtils;

public class LicenseContactPerson {

  private String name;
  private String email;
  private String phone;
  private String idpEntityId;

  public LicenseContactPerson() {
  }

  public LicenseContactPerson(String name, String email, String phone, String idpEntityId) {
    this.name = name;
    this.email = email;
    this.phone = phone;
    this.idpEntityId = idpEntityId;
  }

  public boolean isReachable() {
    return StringUtils.hasText(name) || StringUtils.hasText(email) || StringUtils.hasText(phone);
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getPhone() {
    return phone;
  }

  public String getIdpEntityId() {
    return idpEntityId;
  }
}
