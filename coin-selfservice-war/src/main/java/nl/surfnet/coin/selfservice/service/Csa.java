package nl.surfnet.coin.selfservice.service;


import nl.surfnet.coin.selfservice.domain.*;

import java.util.List;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId);

  List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId);

  List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders();

  List<Action> getJiraActions(String idpEntityId);

  Taxonomy getTaxonomy();

  Service getServiceForIdp(String idpEntityId, long serviceId);

  Action createAction(Action action);

}
