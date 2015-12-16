package selfservice.domain;

import java.util.List;

public class OfferedService {

  private Service service;
  private List<InstitutionIdentityProvider> identityProviders;

  public OfferedService() { // only here for json marshalling (sigh...)
  }

  public OfferedService(Service service) {
    this.service = service;
  }

  public Service getService() {
    return service;
  }

  public List<InstitutionIdentityProvider> getIdentityProviders() {
    return identityProviders;
  }

  @Override
  public String toString() {
    return "OfferedService{" +
      "service=" + service +
      ", identityProviders=" + identityProviders +
      '}';
  }
}
