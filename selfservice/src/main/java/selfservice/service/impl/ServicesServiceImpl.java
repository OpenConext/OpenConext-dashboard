package selfservice.service.impl;

import com.google.common.collect.ImmutableMap;
import org.springframework.util.CollectionUtils;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Service;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.ContactPerson;
import selfservice.domain.csa.ContactPersonType;
import selfservice.manage.Manage;
import selfservice.service.ServicesService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static selfservice.domain.Provider.Language.EN;
import static selfservice.domain.Provider.Language.NL;

public class ServicesServiceImpl implements ServicesService {

  private final Manage manage;

  public ServicesServiceImpl(Manage manage) {
    this.manage = manage;
  }

  @Override
  public Map<String, List<Service>> findAll() {
    List<ServiceProvider> allServiceProviders = manage.getAllServiceProviders();

    List<Service> servicesEn = buildApiServices(allServiceProviders, "en");
    List<Service> servicesNl = buildApiServices(allServiceProviders, "nl");

    return ImmutableMap.of("en", servicesEn, "nl", servicesNl);
  }

  private List<Service> buildApiServices(List<ServiceProvider> services, String language) {
    return services.stream().map(service -> buildApiService(service, language)).collect(Collectors.toList());
  }

  protected Service buildApiService(ServiceProvider serviceProvider, String language) {
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
    service.setNormenkaderPresent(sp.isGdprIsInWiki());
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
    // Categories - the category values need to be either in nl or en (as the facet and facet_values are based on the language setting)
    List<String> typeOfServices = locale.equals("en") ? sp.getTypeOfServicesEn() : sp.getTypeOfServicesNl();
    Category category = new Category(locale.equals("en") ? "Type of Service" : "Type Service", typeOfServices.stream().map
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

}
