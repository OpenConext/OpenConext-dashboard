package selfservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.manage.Manage;
import selfservice.service.Csa;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

public class CsaImpl implements Csa {

    private final String defaultLocale = "en";
    @Autowired
    private ServicesService servicesService;
    @Autowired
    private Manage manage;

    @Override
    public List<Service> getServicesForIdp(String idpEntityId) throws IOException {
        IdentityProvider identityProvider = manage.getIdentityProvider(idpEntityId)
            .orElseThrow(() -> new IllegalArgumentException(String.format("No IdentityProvider known in Manage with name:" +
                " '%s'", idpEntityId)));
        boolean allowedAll = identityProvider.isAllowedAll();
        Set<String> allowedEntityIds = identityProvider.getAllowedEntityIds();

        List<String> connectedServiceProviderIdentifiers = manage.getAllServiceProviders(idpEntityId).stream()
            .filter(sp -> sp.isLinked())
            .map(Provider::getId).collect(toList());

        return servicesService.findAll(getLocale()).stream().filter(service -> {
            boolean isConnected = connectedServiceProviderIdentifiers.contains(service.getSpEntityId());
            boolean showForInstitution = showServiceForInstitution(identityProvider, service);
            return showForInstitution || isConnected;
        }).map(service -> {
            service.setConnected(connectedServiceProviderIdentifiers.contains(service.getSpEntityId()));
            return service;
        }).collect(toList());
    }

    /*
     * If a Service is idpOnly then we do want to show it as the institutionId matches that of the Idp, meaning that
     * an admin from Groningen can see the services offered by Groningen also when they are marked idpOnly - which is
      * often the
     * case for services offered by universities
     */
    private boolean showServiceForInstitution(IdentityProvider identityProvider, Service service) {
        return !service.isIdpVisibleOnly() || (service.getInstitutionId() != null && service.getInstitutionId()
            .equalsIgnoreCase(identityProvider.getInstitutionId()));
    }

    @Override
    public Optional<Service> getServiceForIdp(String idpEntityId, long serviceId) throws IOException {
        return getServicesForIdp(idpEntityId).stream()
            .filter(service -> service.getId() == serviceId)
            .findFirst();
    }

    private String getLocale() {
        Locale locale = null;
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra != null) {
            HttpServletRequest request = sra.getRequest();
            if (request != null) {
                locale = RequestContextUtils.getLocale(request);
            }
        }
        return locale != null ? locale.getLanguage() : defaultLocale;
    }

}
