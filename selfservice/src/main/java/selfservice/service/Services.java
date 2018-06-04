package selfservice.service;

import selfservice.domain.Service;
import selfservice.manage.EntityType;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public interface Services {

    List<Service> getServicesForIdp(String idpEntityId, Locale locale) throws IOException;

    Optional<Service> getServiceByEntityId(String idpEntityId, String spEntityId, EntityType entityType,
                                           Locale locale) throws IOException;

    List<Service> getInstitutionalServicesForIdp(String institutionId, Locale locale) throws IOException;

    List<Service> getGuestEnabledServiceProviders(Locale locale) throws IOException;

}
