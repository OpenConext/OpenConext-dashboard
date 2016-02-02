package selfservice.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import com.google.common.collect.ImmutableMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.CrmArticle;
import selfservice.domain.Facet;
import selfservice.domain.FacetValue;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.CrmService;
import selfservice.service.ServicesService;

public class ServicesServiceImpl implements ServicesService {

  private static final Logger LOG = LoggerFactory.getLogger(ServicesServiceImpl.class);

  private final CompoundServiceProviderService compoundSPService;
  private final CrmService lmngService;

  private final String staticBaseUrl;
  private final String lmngDeepLinkBaseUrl;

  private final String[] guids;

  public ServicesServiceImpl(CompoundServiceProviderService compoundSPService, CrmService lmngService, String staticBaseUrl, String lmngDeepLinkBaseUrl, String[] guids) {
    this.compoundSPService = compoundSPService;
    this.lmngService = lmngService;
    this.staticBaseUrl = staticBaseUrl;
    this.lmngDeepLinkBaseUrl = lmngDeepLinkBaseUrl;
    this.guids = guids;
  }

  @Override
  public Map<String, List<Service>> findAll(long callDelay) {
    List<CompoundServiceProvider> allCSPs = compoundSPService.getAllCSPs(callDelay);
    List<Service> servicesEn = buildApiServices(allCSPs, "en");
    List<Service> servicesNl = buildApiServices(allCSPs, "nl");
    List<Service> crmOnlyServices = getCrmOnlyServices();
    servicesEn.addAll(crmOnlyServices);
    servicesNl.addAll(crmOnlyServices);

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

  /**
   * Build a Service object based on the given CSP
   */
  public Service buildApiService(CompoundServiceProvider csp, String language) {
    boolean isEn = language.equalsIgnoreCase("en");

    Service service = new Service();
    plainProperties(csp, service);
    screenshots(csp, service);
    languageSpecificProperties(csp, isEn, service);
    addArticle(csp.getArticle(), service);
    service.setLicense(csp.getLicense());
    categories(csp, service, language);

    return service;
  }

  private List<Service> getCrmOnlyServices() {
    List<Service> result = new ArrayList<>();
    for (String guid : guids) {
      Article currentArticle = lmngService.getService(guid);
      if (currentArticle == null) {
        LOG.info("A GUID has been configured that cannot be found in CRM: {}", guid);
      } else {
        Service currentPS = new Service(0L, currentArticle.getServiceDescriptionNl(), currentArticle.getDetailLogo(), null, true, lmngDeepLinkBaseUrl + guid, null);
        addArticle(currentArticle, currentPS);
        result.add(currentPS);
      }
    }
    return result;
  }

  private void plainProperties(CompoundServiceProvider csp, Service service) {
    // Plain properties
    service.setSpEntityId(csp.getSp().getId());
    service.setAppUrl(csp.getAppUrl());
    service.setId(csp.getId());
    service.setEulaUrl(csp.getEulaUrl());
    service.setCrmUrl(csp.isArticleAvailable() ? (lmngDeepLinkBaseUrl + csp.getLmngId()) : null);
    service.setDetailLogoUrl(absoluteUrl(csp.getDetailLogo()));
    service.setLogoUrl(absoluteUrl(csp.getAppStoreLogo()));
    service.setSupportMail(csp.getSupportMail());
    service.setWebsiteUrl(csp.getServiceUrl());
    service.setArp(csp.getSp().getArp());
    service.setAvailableForEndUser(csp.isAvailableForEndUser());
    service.setIdpVisibleOnly(csp.getSp().isIdpVisibleOnly());
    service.setInstitutionId(csp.getSp().getInstitutionId());
    service.setPublishedInEdugain(csp.getSp().isPublishedInEdugain());
    service.setLicenseStatus(csp.getLicenseStatus());
    service.setNormenkaderUrl(csp.getNormenkaderUrl());
    service.setNormenkaderPresent(csp.isNormenkaderPresent());
    service.setExampleSingleTenant(csp.isExampleSingleTenant());
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
    } else {
      service.setDescription(csp.getServiceDescriptionNl());
      service.setEnduserDescription(csp.getEnduserDescriptionNl());
      service.setName(csp.getTitleNl());
      service.setSupportUrl(csp.getSupportUrlNl());
      service.setInstitutionDescription(csp.getInstitutionDescriptionNl());
      service.setServiceUrl(csp.getSupportUrlNl());
      service.setWikiUrl(csp.getWikiUrlNl());
      service.setSpName(csp.getSp().getName(Provider.Language.NL));
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

      category.addCategoryValue(new CategoryValue(facetValue.getLocaleValue(locale)));
    }

    service.setCategories(categories);
  }

  private Optional<Category> findCategory(List<Category> categories, Facet facet) {
    return categories.stream().filter(category -> category.getName().equalsIgnoreCase(facet.getName())).findFirst();
  }

  private void addArticle(Article article, Service service) {
    if (article != null) {
      CrmArticle crmArticle = new CrmArticle();
      crmArticle.setGuid(article.getLmngIdentifier());
      if (article.getAndroidPlayStoreMedium() != null) {
        crmArticle.setAndroidPlayStoreUrl(article.getAndroidPlayStoreMedium().getUrl());
      }
      if (article.getAppleAppStoreMedium() != null) {
        crmArticle.setAppleAppStoreUrl(article.getAppleAppStoreMedium().getUrl());
      }
      service.setHasCrmLink(true);
      service.setCrmArticle(crmArticle);
    }
  }

  /**
   * Returns an absolute URL for the given url
   */
  private String absoluteUrl(final String relativeUrl) {
    if (relativeUrl != null && relativeUrl.startsWith("/")) {
      return this.staticBaseUrl + relativeUrl;
    }
    return relativeUrl;
  }
}
