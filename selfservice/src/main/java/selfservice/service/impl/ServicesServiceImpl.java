package selfservice.service.impl;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Facet;
import selfservice.domain.FacetValue;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.domain.csa.ContactPerson;
import selfservice.manage.Manage;
import selfservice.service.ServicesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

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
    screenshots(serviceProvider, service);
    languageSpecificProperties(serviceProvider, isEn, service);
    categories(serviceProvider, service, language);
    contactPersons(serviceProvider, service);
    return service;
  }

  private void plainProperties(ServiceProvider sp, Service service) {
    // Plain properties
    service.setSpEntityId(sp.getId());
    service.setAppUrl(sp.getApplicationUrl());
    service.setId(sp.getId());
    service.setEulaUrl(csp.getEulaUrl());
    service.setDetailLogoUrl(absoluteUrl(csp.getDetailLogo()));
    service.setLogoUrl(absoluteUrl(csp.getAppStoreLogo()));
    service.setSupportMail(normalizeEmail(csp.getSupportMail()));
    service.setWebsiteUrl(csp.getServiceUrl());
    service.setArp(csp.getSp().getArp());
    service.setIdpVisibleOnly(csp.getSp().isIdpVisibleOnly());
    service.setPolicyEnforcementDecisionRequired(csp.getSp().isPolicyEnforcementDecisionRequired());
    service.setInstitutionId(csp.getSp().getInstitutionId());
    service.setPublishedInEdugain(csp.getSp().isPublishedInEdugain());
    service.setLicenseStatus(csp.getLicenseStatus());
    service.setNormenkaderUrl(csp.getNormenkaderUrl());
    service.setNormenkaderPresent(csp.isNormenkaderPresent());
    service.setExampleSingleTenant(csp.isExampleSingleTenant());
    service.setInterfedSource(csp.getInterfedSource());
    service.setRegistrationInfoUrl(csp.getRegistrationInfo());
    service.setEntityCategories1(csp.getEntityCategories1());
    service.setEntityCategories2(csp.getEntityCategories2());
    service.setPublishInEdugainDate(csp.getPublishInEdugainDate());
    service.setStrongAuthentication(csp.isStrongAuthentication());
    service.setNames(csp.getSp().getNames());
    service.setDescriptions(csp.getSp().getDescriptions());
    service.setNoConsentRequired(csp.getSp().isNoConsentRequired());
  }

  private String normalizeEmail(String email) {
    if (email != null && email.startsWith("mailto:")) {
      return email.substring(7);
    }

    return email;
  }

  private void screenshots(CompoundServiceProvider csp, Service service) {
    if (csp.getScreenShotsImages() != null) {
      List<String> screenshots = csp.getScreenShotsImages().stream().map(screenshot -> absoluteUrl(screenshot.getFileUrl())).collect(toList());
      service.setScreenshotUrls(screenshots);
    }
  }

  private void languageSpecificProperties(CompoundServiceProvider csp, boolean en, Service service) {
    if (en) {
      service.setDescription(csp.getServiceDescriptionEn());
      service.setEnduserDescription(csp.getEnduserDescriptionEn());
      service.setName(csp.getTitleEn());
      service.setSupportUrl(csp.getSupportUrlEn());
      service.setInstitutionDescription(csp.getInstitutionDescriptionEn());
      service.setServiceUrl(csp.getSupportUrlEn());
      service.setWikiUrl(csp.getWikiUrlEn());
      service.setSpName(csp.getSp().getName(Provider.Language.EN));
      service.setRegistrationPolicyUrl(csp.getRegistrationPolicyUrlEn());
      service.setPrivacyStatementUrl(csp.getPrivacyStatementUrlEn());
    } else {
      service.setDescription(csp.getServiceDescriptionNl());
      service.setEnduserDescription(csp.getEnduserDescriptionNl());
      service.setName(csp.getTitleNl());
      service.setSupportUrl(csp.getSupportUrlNl());
      service.setInstitutionDescription(csp.getInstitutionDescriptionNl());
      service.setServiceUrl(csp.getSupportUrlNl());
      service.setWikiUrl(csp.getWikiUrlNl());
      service.setSpName(csp.getSp().getName(Provider.Language.NL));
      service.setPrivacyStatementUrl(csp.getPrivacyStatementUrlNl());
      service.setRegistrationPolicyUrl(csp.getRegistrationPolicyUrlNl());
    }
  }

  private void categories(CompoundServiceProvider csp, Service service, String locale) {
    // Categories - the category values need to be either in nl or en (as the facet and facet_values are based on the language setting)
    List<Category> categories = new ArrayList<>();

    for (FacetValue facetValue : csp.getFacetValues()) {
      Facet facet = facetValue.getFacet();

      Category category = findCategory(categories, facet).orElseGet(() -> {
        Category cat = new Category(facet.getLocaleName(locale));
        categories.add(cat);
        return cat;
      });

      category.addCategoryValue(new CategoryValue(facetValue.getLocaleValue(locale), category));
    }

    service.setCategories(categories);
  }

  private void contactPersons(CompoundServiceProvider csp, Service service) {
    List<ContactPerson> contactPersons = csp.getServiceProvider().getContactPersons();
    if (!CollectionUtils.isEmpty(contactPersons)) {
      service.setContactPersons(contactPersons.stream()
        .filter(contactPerson -> contactPerson.isSirtfiSecurityContact()).collect(toList()));
    }
  }

}
