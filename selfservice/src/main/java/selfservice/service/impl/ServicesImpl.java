package selfservice.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Service;
import selfservice.domain.ServiceProvider;
import selfservice.domain.ContactPerson;
import selfservice.domain.ContactPersonType;
import selfservice.manage.EntityType;
import selfservice.manage.Manage;
import selfservice.service.Services;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static selfservice.domain.Provider.Language.EN;
import static selfservice.domain.Provider.Language.NL;

public class ServicesImpl implements Services {

    @Autowired
    private Manage manage;

    @Override
    public List<Service> getServicesForIdp(String idpEntityId, Locale locale) {
        IdentityProvider identityProvider = manage.getIdentityProvider(idpEntityId).orElseThrow(() -> new
            IllegalArgumentException(String.format("IDP %s does not exists", idpEntityId)));

        List<ServiceProvider> allServiceProviders = manage.getAllServiceProviders();
        List<Service> services = allServiceProviders.stream().map(sp -> {
            Service service = this.buildApiService(sp, locale.getLanguage());
            boolean connectedToIdentityProvider = identityProvider.isAllowedAll() || identityProvider
                .getAllowedEntityIds().contains(sp.getId());
            boolean allowedBySp = sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idpEntityId);
            service.setConnected(connectedToIdentityProvider && allowedBySp);
            return service;
        }).filter(service -> !service.isIdpVisibleOnly() || service.isConnected() ||
            (service.getInstitutionId() != null &&service.getInstitutionId().equals(identityProvider.getInstitutionId())))
            .collect(toList());
        return services;
    }

    @Override
    public Optional<Service> getServiceByEntityId(String idpEntityId, String spEntityId, EntityType entityType,
                                                  Locale locale) {
        Optional<ServiceProvider> serviceProvider = manage.getServiceProvider(spEntityId, entityType);
        return enrichService(idpEntityId, locale, serviceProvider);
    }

    @Override
    public Optional<Service> getServiceById(String idpEntityId, Long spId, EntityType entityType, Locale locale)
        throws IOException {
        Optional<ServiceProvider> serviceProvider = manage.getServiceProviderById(spId, entityType);
        return enrichService(idpEntityId, locale, serviceProvider);
    }

    private Optional<Service> enrichService(String idpEntityId, Locale locale, Optional<ServiceProvider>
        serviceProvider) {
        IdentityProvider identityProvider = manage.getIdentityProvider(idpEntityId).orElseThrow(() -> new
            IllegalArgumentException(String.format("IDP %s does not exists", idpEntityId)));
        return serviceProvider.map(sp -> {
            boolean connectedToIdentityProvider = identityProvider.isAllowedAll() || identityProvider
                .getAllowedEntityIds().contains(sp.getId());
            boolean allowedBySp = sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idpEntityId);
            Service service = this.buildApiService(sp, locale.getLanguage());
            service.setConnected(connectedToIdentityProvider && allowedBySp);
            return service;
        });
    }

    @Override
    public List<Service> getInstitutionalServicesForIdp(String institutionId, Locale locale) {
        List<ServiceProvider> institutionalServicesForIdp = manage.getInstitutionalServicesForIdp(institutionId);
        return this.buildApiServices(institutionalServicesForIdp, locale.getLanguage());
    }

    private List<Service> buildApiServices(List<ServiceProvider> services, String language) {
        return services.stream().map(service -> buildApiService(service, language)).collect(Collectors.toList());
    }

    private Service buildApiService(ServiceProvider serviceProvider, String language) {
        boolean isEn = language.equalsIgnoreCase("en");

        Service service = new Service();
        plainProperties(serviceProvider, service);
        languageSpecificProperties(serviceProvider, isEn, service);
        categories(serviceProvider, service, language);
        contactPersons(serviceProvider, service);
        return service;
    }

    private void plainProperties(ServiceProvider sp, Service service) {
        // Plain properties
        service.setSpEntityId(sp.getId());
        service.setAppUrl(sp.getApplicationUrl());
        service.setId(sp.getEid());
        service.setEulaUrl(sp.getEulaURL());
        service.setDetailLogoUrl(sp.getLogoUrl());
        service.setLogoUrl(sp.getLogoUrl());
        service.setSupportMail(mailOfContactPerson(sp.getContactPerson(ContactPersonType.help)));
        Map<String, String> homeUrls = sp.getHomeUrls();
        service.setWebsiteUrl(CollectionUtils.isEmpty(homeUrls) ? null : homeUrls.values().iterator().next());
        service.setArp(sp.getArp());
        service.setIdpVisibleOnly(sp.isIdpVisibleOnly());
        service.setPolicyEnforcementDecisionRequired(sp.isPolicyEnforcementDecisionRequired());
        service.setInstitutionId(sp.getInstitutionId());
        service.setPublishedInEdugain(sp.isPublishedInEdugain());
        service.setLicenseStatus(sp.getLicenseStatus());
        service.setExampleSingleTenant(sp.isExampleSingleTenant());
        service.setInterfedSource(sp.getInterfedSource());
        service.setRegistrationInfoUrl(sp.getRegistrationInfo());
        service.setEntityCategories1(sp.getEntityCategories1());
        service.setEntityCategories2(sp.getEntityCategories2());
        service.setPublishInEdugainDate(sp.getPublishInEdugainDate());
        service.setStrongAuthentication(sp.isStrongAuthenticationSupported());
        service.setNames(sp.getNames());
        service.setDescriptions(sp.getDescriptions());
        service.setNoConsentRequired(sp.isNoConsentRequired());
        service.setPrivacyInfo(sp.getPrivacyInfo());
        service.setMotivations(sp.getArpMotivations());
        service.setNormenkaderPresent(sp.getPrivacyInfo().isGdprIsInWiki());
        service.setAansluitovereenkomstRefused(sp.isAansluitovereenkomstRefused());
        service.setGuestEnabled(sp.isAllowedAll() ||
            (sp.getAllowedEntityIds() != null && sp.getAllowedEntityIds().contains(Manage.guestIdp)));
    }

    private String mailOfContactPerson(ContactPerson contactPerson) {
        return contactPerson == null ? null : contactPerson.getEmailAddress();
    }

    private void languageSpecificProperties(ServiceProvider sp, boolean en, Service service) {
        if (en) {
            service.setDescription(sp.getDescription(EN));
            service.setEnduserDescription(sp.getDescription(EN));
            service.setName(sp.getName(EN));

            service.setSupportUrl(sp.getUrl(EN));
            service.setInstitutionDescription(sp.getDescription(EN));
            service.setServiceUrl(sp.getUrl(EN));
            service.setWikiUrl(sp.getWikiUrlEn());
            service.setSpName(sp.getName(EN));
            service.setRegistrationPolicyUrl(sp.getRegistrationPolicyUrlEn());
            service.setPrivacyStatementUrl(sp.getPrivacyStatementUrlEn());
        } else {
            service.setDescription(sp.getDescription(NL));
            service.setEnduserDescription(sp.getDescription(NL));
            service.setName(sp.getName(NL));

            service.setSupportUrl(sp.getUrl(NL));
            service.setInstitutionDescription(sp.getDescription(NL));
            service.setServiceUrl(sp.getUrl(NL));
            service.setWikiUrl(sp.getWikiUrlNl());
            service.setSpName(sp.getName(NL));
            service.setRegistrationPolicyUrl(sp.getRegistrationPolicyUrlNl());
            service.setPrivacyStatementUrl(sp.getPrivacyStatementUrlNl());
        }
    }

    private void categories(ServiceProvider sp, Service service, String locale) {
        // Categories - the category values need to be either in nl or en (as the facet and facet_values are based on
        // the language setting)
        List<String> typeOfServices = locale.equals("en") ? sp.getTypeOfServicesEn() : sp.getTypeOfServicesNl();
        Category category = new Category(locale.equals("en") ? "Type of Service" : "Type Service", typeOfServices
            .stream().map
            (CategoryValue::new).collect(toList()));
        service.setCategories(Collections.singletonList(category));
    }

    private void contactPersons(ServiceProvider sp, Service service) {
        List<ContactPerson> contactPersons = sp.getContactPersons();
        if (!CollectionUtils.isEmpty(contactPersons)) {
            service.setContactPersons(contactPersons.stream()
                .filter(contactPerson -> contactPerson.isSirtfiSecurityContact()).collect(toList()));
        }
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

}
