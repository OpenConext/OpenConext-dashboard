package dashboard.service.impl;

import dashboard.domain.*;
import dashboard.manage.EntityType;
import dashboard.manage.Manage;
import dashboard.service.Services;
import dashboard.util.SpringSecurity;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.stream.Collectors;

import static dashboard.domain.Provider.Language.*;
import static java.util.stream.Collectors.toList;

public class ServicesImpl implements Services {

    private Manage manage;
    private List<String> guestIdps;
    private Set<String> allowedGuestEntityIds = new HashSet<>();
    private boolean allowedAllForGuestIdp = false;
    private boolean manageFetched = false;

    public ServicesImpl(Manage manage, List<String> guestIdps) {
        this.manage = manage;
        this.guestIdps = guestIdps;
    }

    @Override
    public List<Service> getServicesForIdp(String idpEntityId, boolean includeAll, Locale locale) {
        IdentityProvider identityProvider;
        CoinUser currentUser = SpringSecurity.getCurrentUser();
        if (currentUser.isGuest()) {
            identityProvider = new IdentityProvider(Collections.singletonMap("eid", 1));
        } else {
            identityProvider = manage.getIdentityProvider(idpEntityId, false).orElseThrow(() -> new
                    IllegalArgumentException(String.format("IDP %s does not exists", idpEntityId)));
        }

        List<ServiceProvider> allServiceProviders = manage.getAllServiceProviders();
        Set<String> invitationRequestEntities = currentUser.getInvitationRequestEntities();
        List<Service> services = allServiceProviders.stream()
                .filter(sp -> !sp.isResourceServer() && !sp.isClientCredentials())
                .map(sp -> {
                    Service service = this.buildApiService(sp, locale.getLanguage());
                    markServiceAsConnected(idpEntityId, identityProvider, sp, service);
                    service.setDashboardConnectOption(sp.getDashboardConnectOption());
                    return service;
                })
                .filter(service -> !service.isIdpVisibleOnly() || service.isConnected() || includeAll ||
                        (service.getInstitutionId() != null && service.getInstitutionId().equals(identityProvider.getInstitutionId())) ||
                        (invitationRequestEntities != null && invitationRequestEntities.contains(service.getSpEntityId())))
                .collect(toList());
        return services;
    }

    @Override
    public List<Service> getServicesByEntityIds(List<String> entityIds, Locale locale) {
        return buildApiServices(manage.getByEntityIdin(entityIds), locale.getLanguage());
    }

    @Override
    public Optional<Service> getServiceByEntityId(String idpEntityId, String spEntityId, EntityType entityType,
                                                  Locale locale) {
        Optional<ServiceProvider> serviceProvider = manage.getServiceProvider(spEntityId, entityType, false);
        return enrichService(idpEntityId, locale, serviceProvider);
    }

    @Override
    public Optional<Service> getServiceById(String idpEntityId, Long spId, EntityType entityType, Locale locale) {
        Optional<ServiceProvider> serviceProvider = manage.getServiceProviderById(spId, entityType);
        return enrichService(idpEntityId, locale, serviceProvider);
    }

    private void markServiceAsConnected(String idpEntityId, IdentityProvider identityProvider, ServiceProvider sp, Service service) {
        boolean connectedToIdentityProvider = identityProvider.isAllowedAll() || identityProvider
                .getAllowedEntityIds().contains(sp.getId());
        boolean allowedBySp = sp.isAllowedAll() || sp.getAllowedEntityIds().contains(idpEntityId);
        boolean isSingleTenantTemplate = sp.getEntityType().equals(EntityType.single_tenant_template);
        service.setConnected(connectedToIdentityProvider && allowedBySp && !isSingleTenantTemplate);
    }

    private Optional<Service> enrichService(String idpEntityId, Locale locale, Optional<ServiceProvider>
            serviceProvider) {
        IdentityProvider identityProvider;
        if (SpringSecurity.getCurrentUser().isGuest()) {
            identityProvider = new IdentityProvider(Collections.singletonMap("eid", 1));
        } else {
            identityProvider = manage.getIdentityProvider(idpEntityId, false).orElseThrow(() -> new
                    IllegalArgumentException(String.format("IDP %s does not exists", idpEntityId)));
        }

        return serviceProvider.map(sp -> {
            Service service = this.buildApiService(sp, locale.getLanguage());
            markServiceAsConnected(idpEntityId, identityProvider, sp, service);
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

    private Service buildApiService(ServiceProvider serviceProvider, String locale) {

        Service service = new Service();
        plainProperties(serviceProvider, service);
        languageSpecificProperties(serviceProvider, locale, service);
        categories(serviceProvider, service, locale);
        contactPersons(serviceProvider, service);
        return service;
    }

    private void plainProperties(ServiceProvider sp, Service service) {
        // Plain properties
        service.setSpEntityId(sp.getId());
        service.setState(sp.getState());
        service.setAppUrl(sp.getApplicationUrl());
        service.setId(sp.getEid());
        service.setEulaUrl(sp.getEulaURL());
        service.setDetailLogoUrl(sp.getLogoUrl());
        service.setLogoUrl(sp.getLogoUrl());
        service.setSupportMail(mailOfContactPerson(sp.getContactPerson(ContactPersonType.support)));
        Map<String, String> homeUrls = sp.getHomeUrls();
        service.setWebsiteUrl(CollectionUtils.isEmpty(homeUrls) ? null : homeUrls.values().iterator().next());
        service.setArp(sp.getArp());
        service.setIdpVisibleOnly(sp.isIdpVisibleOnly());
        service.setPolicyEnforcementDecisionRequired(sp.isPolicyEnforcementDecisionRequired());
        service.setInstitutionId(sp.getInstitutionId());
        service.setPublishedInEdugain(sp.isPublishedInEdugain());
        service.setLicenseStatus(sp.getLicenseStatus());
        service.setEntityType(sp.getEntityType());
        service.setExampleSingleTenant(sp.getEntityType().equals(EntityType.single_tenant_template));
        service.setInterfedSource(sp.getInterfedSource());
        service.setRegistrationInfoUrl(sp.getRegistrationInfo());
        service.setEntityCategories1(sp.getEntityCategories1());
        service.setEntityCategories2(sp.getEntityCategories2());
        service.setEntityCategories3(sp.getEntityCategories3());
        service.setPublishInEdugainDate(sp.getPublishInEdugainDate());
        service.setStrongAuthentication(sp.isStrongAuthenticationEnabled());
        service.setMinimalLoaLevel(sp.getMinimalLoaLevel());
        service.setNames(sp.getNames());
        service.setDescriptions(sp.getDescriptions());
        service.setDisplayNames(sp.getDisplayNames());
        service.setNoConsentRequired(sp.isNoConsentRequired());
        service.setPrivacyInfo(sp.getPrivacyInfo());
        service.setMotivations(sp.getArpMotivations());
        service.setNormenkaderPresent(sp.getPrivacyInfo().isGdprIsInWiki());
        service.setAansluitovereenkomstRefused(sp.isAansluitovereenkomstRefused());
        service.setGuestEnabled(this.isGuestEnabled(sp));
        service.setManipulationNotes(sp.getManipulationNotes());
        service.setContractualBase(sp.getContractualBase());
        service.setDashboardConnectOption(sp.getDashboardConnectOption());
        service.setManipulation(sp.isManipulation());
        service.setNameIds(sp.getNameIds());
        service.setResourceServers(sp.getResourceServers());
        service.setResourceServer(sp.isResourceServer());
    }

    private void initialize() {
        this.guestIdps.stream().map(guestIdp -> manage.getIdentityProvider(guestIdp, false))
                .forEach(identityProviderOptional -> {
                    identityProviderOptional.ifPresent(identityProvider -> {
                        if (!this.allowedAllForGuestIdp) {
                            this.allowedAllForGuestIdp = identityProvider.isAllowedAll();
                        }
                        if (identityProvider.getAllowedEntityIds() != null) {
                            this.allowedGuestEntityIds.addAll(identityProvider.getAllowedEntityIds());
                        }
                    });

                });
        this.manageFetched = true;
    }

    private boolean isGuestEnabled(ServiceProvider sp) {
        try {
            if (!this.manageFetched) {
                initialize();
            }
        } catch (Exception e) {
            this.manageFetched = false;
            return false;
        }
        if (sp.isAllowedAll() && (this.allowedAllForGuestIdp || this.allowedGuestEntityIds.contains(sp.getId()))) {
            return true;
        }
        return sp.getAllowedEntityIds() != null && sp.getAllowedEntityIds().stream().anyMatch(entityId -> this.guestIdps.contains(entityId)) &&
                (this.allowedGuestEntityIds.contains(sp.getId()) || this.allowedAllForGuestIdp);
    }

    private String mailOfContactPerson(ContactPerson contactPerson) {
        return contactPerson == null ? null : contactPerson.getEmailAddress();
    }

    private void languageSpecificProperties(ServiceProvider sp, String locale, Service service) {
        Provider.Language lang = (locale == "en" ? EN : locale == "pt" ? PT : NL);
        service.setDescription(sp.getDescription(lang));
        service.setEnduserDescription(sp.getDescription(lang));
        service.setName(sp.getName(lang));

        service.setSupportUrl(sp.getUrl(lang));
        service.setInstitutionDescription(sp.getDescription(lang));
        service.setServiceUrl(sp.getUrl(lang));
        service.setWikiUrl(sp.getWikiUrl(lang));
        service.setSpName(sp.getName(lang));

        if (locale == "en") {
            service.setRegistrationPolicyUrl(sp.getRegistrationPolicyUrlEn());
            service.setPrivacyStatementUrl(sp.getPrivacyStatementUrlEn());
        } else if (locale == "pt") {
            service.setRegistrationPolicyUrl(sp.getRegistrationPolicyUrlPt());
            service.setPrivacyStatementUrl(sp.getPrivacyStatementUrlPt());
        } else {
            service.setRegistrationPolicyUrl(sp.getRegistrationPolicyUrlNl());
            service.setPrivacyStatementUrl(sp.getPrivacyStatementUrlNl());
        }
    }

    private void categories(ServiceProvider sp, Service service, String locale) {
        // Categories - the category values need to be either in nl or en (as the facet and facet_values are based on
        // the language setting)
        List<String> typeOfServices = locale.equals("en") ? sp.getTypeOfServicesEn() : locale.equals("pt") ? sp.getTypeOfServicesPt() : sp.getTypeOfServicesNl();
        if (CollectionUtils.isEmpty(typeOfServices)) {
            typeOfServices.add(locale.equals("en") ? "Other" : locale.equals("pt") ? "Outro" : "Overig");
        }
        Category category = new Category(locale.equals("en") ? "Type of Service" : locale.equals("pt") ? "Tipo de Serviço" : "Type Service", "type_of_service",
                typeOfServices.stream().map(CategoryValue::new).collect(toList()));
        service.setCategories(Collections.singletonList(category));
    }

    private void contactPersons(ServiceProvider sp, Service service) {
        service.setContactPersons(sp.getContactPersons());
    }

}
