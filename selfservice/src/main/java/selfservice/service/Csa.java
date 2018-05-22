package selfservice.service;

import java.util.List;
import java.util.Optional;

import selfservice.domain.Service;
import selfservice.domain.Taxonomy;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId);

  Optional<Service> getServiceForIdp(String idpEntityId, long serviceId);

}
