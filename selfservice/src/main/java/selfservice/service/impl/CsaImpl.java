package selfservice.service.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import selfservice.cache.CrmCache;
import selfservice.cache.ServicesCache;
import selfservice.dao.FacetDao;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.CrmArticle;
import selfservice.domain.IdentityProvider;
import selfservice.domain.Provider;
import selfservice.domain.Service;
import selfservice.domain.Taxonomy;
import selfservice.domain.csa.Article;
import selfservice.service.Csa;
import selfservice.serviceregistry.ServiceRegistry;

public class CsaImpl implements Csa {

  @Autowired
  private FacetDao facetDao;

  @Autowired
  private ServicesCache servicesCache;

  @Autowired
  private ServiceRegistry serviceRegistry;

  @Autowired
  private CrmCache crmCache;

  private final String defaultLocale = "en";

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    IdentityProvider identityProvider = serviceRegistry.getIdentityProvider(idpEntityId)
        .orElseThrow(() -> new IllegalArgumentException(String.format("No IdentityProvider known in SR with name: '%s'", idpEntityId)));

    List<String> connectedServiceProviderIdentifiers = serviceRegistry.getAllServiceProviders(idpEntityId).stream()
        .filter(sp -> sp.isLinked())
        .map(Provider::getId).collect(toList());

    return servicesCache.getAllServices(getLocale()).stream().filter(service -> {
      boolean isConnected = connectedServiceProviderIdentifiers.contains(service.getSpEntityId());
      boolean showForInstitution = showServiceForInstitution(identityProvider, service);
      return showForInstitution || isConnected;
    }).map(service -> {
        service.setConnected(connectedServiceProviderIdentifiers.contains(service.getSpEntityId()));

        crmCache.getLicense(service, identityProvider.getInstitutionId()).ifPresent(license -> service.setLicense(license));
        crmCache.getArticle(service).map(this::getArticle).ifPresent(crmArticle -> {
          service.setHasCrmLink(true);
          service.setCrmArticle(crmArticle);
        });

        return service;
    }).collect(toList());
  }

  /*
   * If a Service is idpOnly then we do want to show it as the institutionId matches that of the Idp, meaning that
   * an admin from Groningen can see the services offered by Groningen also when they are marked idpOnly - which is often the
   * case for services offered by universities
   */
  private boolean showServiceForInstitution(IdentityProvider identityProvider, Service service) {
    return !service.isIdpVisibleOnly() || (service.getInstitutionId() != null && service.getInstitutionId().equalsIgnoreCase(identityProvider.getInstitutionId()));
  }

  @Override
  public Taxonomy getTaxonomy() {
    List<Category> categories = StreamSupport.stream(facetDao.findAll().spliterator(), false).map(facet -> {
      Category category = new Category(facet.getName());

      List<CategoryValue> values = facet.getFacetValues().stream().map(fv ->
        new CategoryValue(fv.getValue(), category)
      ).collect(toList());

      category.setValues(values);

      return category;
    }).collect(toList());

    return new Taxonomy(categories);
  }

  @Override
  public Optional<Service> getServiceForIdp(String idpEntityId, long serviceId) {
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

  private CrmArticle getArticle(Article article) {
    CrmArticle crmArticle = new CrmArticle();
    crmArticle.setGuid(article.getLmngIdentifier());
    if (article.getAndroidPlayStoreMedium() != null) {
      crmArticle.setAndroidPlayStoreUrl(article.getAndroidPlayStoreMedium().getUrl());
    }
    if (article.getAppleAppStoreMedium() != null) {
      crmArticle.setAppleAppStoreUrl(article.getAppleAppStoreMedium().getUrl());
    }

    return crmArticle;
  }

}
