package nl.surfnet.coin.selfservice.service;


import java.util.List;

import nl.surfnet.coin.selfservice.domain.Action;
import nl.surfnet.coin.selfservice.domain.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.LicenseContactPerson;
import nl.surfnet.coin.selfservice.domain.Service;
import nl.surfnet.coin.selfservice.domain.Taxonomy;

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
