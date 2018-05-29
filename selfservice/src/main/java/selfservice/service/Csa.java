package selfservice.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import selfservice.domain.Service;
import selfservice.domain.Taxonomy;

public interface Csa {

  List<Service> getServicesForIdp(String idpEntityId) throws IOException;

  Optional<Service> getServiceForIdp(String idpEntityId, long serviceId) throws IOException;

}
