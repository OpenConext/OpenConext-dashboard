package nl.surfnet.coin.selfservice.service;


import nl.surfnet.coin.selfservice.domain.*;

import java.util.List;
import java.util.Optional;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId);

  List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId);

  List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders();

  List<InstitutionIdentityProvider> serviceUsedBy(long serviceId);

  List<Action> getJiraActions(String idpEntityId);

  Taxonomy getTaxonomy();

  Service getServiceForIdp(String idpEntityId, long serviceId);

  Action createAction(Action action);

  Optional<LicenseContactPerson> licenseContactPerson(String idpEntityId);

}
