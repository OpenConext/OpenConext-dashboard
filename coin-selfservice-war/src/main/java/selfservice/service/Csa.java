package selfservice.service;

import java.util.List;

import selfservice.domain.Action;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.LicenseContactPerson;
import selfservice.domain.Service;
import selfservice.domain.Taxonomy;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId);

  List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId);

  List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders();

  List<InstitutionIdentityProvider> serviceUsedBy(String spEntityId);

  List<Action> getJiraActions(String idpEntityId);

  Taxonomy getTaxonomy();

  Service getServiceForIdp(String idpEntityId, long serviceId);

  Action createAction(Action action);

  List<LicenseContactPerson> licenseContactPersons(String idpEntityId);

}
