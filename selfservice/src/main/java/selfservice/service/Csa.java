package selfservice.service;

import java.util.List;

import selfservice.domain.Action;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.Service;
import selfservice.domain.Taxonomy;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId);

  List<InstitutionIdentityProvider> serviceUsedBy(String spEntityId);

  Taxonomy getTaxonomy();

  Service getServiceForIdp(String idpEntityId, long serviceId);

  Action createAction(Action action);

}
