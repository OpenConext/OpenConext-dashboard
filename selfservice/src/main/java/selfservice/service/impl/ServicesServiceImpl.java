package selfservice.service.impl;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.Facet;
import selfservice.domain.FacetValue;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.ServicesService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;

public class ServicesServiceImpl implements ServicesService {

  private static final Logger LOG = LoggerFactory.getLogger(ServicesServiceImpl.class);

  private final CompoundServiceProviderService compoundSPService;

  private final String staticBaseUrl;

  public ServicesServiceImpl(CompoundServiceProviderService compoundSPService,  String staticBaseUrl) {
    this.compoundSPService = compoundSPService;
    this.staticBaseUrl = staticBaseUrl;
  }

  @Override
  public Map<String, List<Service>> findAll() {
    List<CompoundServiceProvider> allCSPs = compoundSPService.getAllCSPs();

    List<Service> servicesEn = buildApiServices(allCSPs, "en");
    List<Service> servicesNl = buildApiServices(allCSPs, "nl");

    return ImmutableMap.of("en", servicesEn, "nl", servicesNl);
  }

  /**
   * Convert the list of found services to a list of services that can be
   * displayed in the API (either public or private)
   *
   * @param services list of services to convert (compound service providers)
   * @param language language to use in the result
   * @return a list of api services
   */
  private List<Service> buildApiServices(List<CompoundServiceProvider> services, String language) {
    return services.stream().map(csp -> buildApiService(csp, language)).collect(Collectors.toList());
  }

  protected Service buildApiService(CompoundServiceProvider csp, String language) {
    checkNotNull(csp);
    checkNotNull(csp.getSp());

    boolean isEn = language.equalsIgnoreCase("en");

    Service service = new Service();
    plainProperties(csp, service);
    screenshots(csp, service);
    languageSpecificProperties(csp, isEn, service);
    categories(csp, service, language);

    return service;
  }


  private void plainProperties(CompoundServiceProvider csp, Service service) {
    // Plain properties
    service.setSpEntityId(csp.getSp().getId());
    service.setAppUrl(csp.getAppUrl());
    service.setId(csp.getId());
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

  private Optional<Category> findCategory(List<Category> categories, Facet facet) {
    return categories.stream().filter(category -> category.getName().equalsIgnoreCase(facet.getName())).findFirst();
  }

  private String absoluteUrl(final String relativeUrl) {
    if (relativeUrl != null && relativeUrl.startsWith("/")) {
      return this.staticBaseUrl + relativeUrl;
    }
    return relativeUrl;
  }
}
