package selfservice.service;

import selfservice.domain.Service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface Services {

    List<Service> getServicesForIdp(String idpEntityId) throws IOException;

    Optional<Service> getServiceForIdp(String idpEntityId, long serviceId) throws IOException;

    List<Service> getInstitutionalServicesForIdp(String institutionId) throws IOException;

}
