package selfservice.service.impl;

import static java.util.stream.Collectors.toList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import selfservice.api.cache.CrmCache;
import selfservice.api.cache.ProviderCache;
import selfservice.api.cache.ServicesCache;
import selfservice.dao.FacetDao;
import selfservice.domain.Action;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.CrmArticle;
import selfservice.domain.Facet;
import selfservice.domain.InstitutionIdentityProvider;
import selfservice.domain.License;
import selfservice.domain.LicenseContactPerson;
import selfservice.domain.Service;
import selfservice.domain.Taxonomy;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.IdentityProvider;
import selfservice.domain.csa.ServiceProvider;
import selfservice.service.ActionsService;
import selfservice.service.Csa;
import selfservice.service.EmailService;
import selfservice.service.IdentityProviderService;
import selfservice.service.ServiceProviderService;
import selfservice.util.LicenseContactPersonService;

public class CsaImpl implements Csa {

  @Autowired
  private EmailService emailService;

  @Value("${administration.email.enabled}")
  private boolean sendAdministrationEmail;

  @Value("${administration.jira.ticket.enabled}")
  private boolean createAdministrationJiraTicket;

  @Autowired
  private LicenseContactPersonService licenseContactPersonService;

  @Autowired
  private ServiceProviderService serviceProviderService;

  @Autowired
  private FacetDao facetDao;

  @Autowired
  private ActionsService actionsService;

  @Autowired
  private ServicesCache servicesCache;

  @Autowired
  private ProviderCache providerCache;

  @Autowired
  private CrmCache crmCache;

  @Autowired
  private IdentityProviderService identityProviderService;

  private String defaultLocale = "en";

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    return doGetServicesForIdP(getLocale(), idpEntityId, true);
  }

  private List<Service> doGetServicesForIdP(String language, String idpEntityId, boolean includeNotLinkedSPs) {
    IdentityProvider identityProvider = providerCache.getIdentityProvider(idpEntityId);
    if (identityProvider == null) {
      throw new IllegalArgumentException("No IdentityProvider known in SR with name:'" + idpEntityId + "'");
    }

    List<String> serviceProviderIdentifiers = providerCache.getServiceProviderIdentifiers(idpEntityId);

    List<Service> allServices = servicesCache.getAllServices(language);
    List<Service> result = new ArrayList<>();

    for (Service service : allServices) {
      boolean isConnected = serviceProviderIdentifiers.contains(service.getSpEntityId());
    /*
     * If a Service is idpOnly then we do want to show it as the institutionId matches that of the Idp, meaning that
     * an admin from Groningen can see the services offered by Groningen also when they are marked idpOnly - which is often the
     * case for services offered by universities
     */
      boolean showForInstitution = !service.isIdpVisibleOnly() || (service.getInstitutionId() != null && service.getInstitutionId().equalsIgnoreCase(identityProvider.getInstitutionId()));
      if ((includeNotLinkedSPs && showForInstitution) || (service.isAvailableForEndUser() && isConnected)) {

        // Weave with 'is connected' from sp/idp matrix cache
        service.setConnected(isConnected);

        // Weave with article and license from caches
        String institutionId = identityProvider.getInstitutionId();
        service.setLicense(crmCache.getLicense(service, institutionId));
        addArticle(crmCache.getArticle(service), service);

        if (service.getLicenseStatus() == License.LicenseStatus.HAS_LICENSE_SURFMARKET) {
          service.setLicenseStatus(service.getLicense() != null ? License.LicenseStatus.HAS_LICENSE_SURFMARKET : License.LicenseStatus.NO_LICENSE);
        }

        result.add(service);
      }
    }
    return result;
  }


  @Override
  public List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId) {
    List<InstitutionIdentityProvider> result = new ArrayList<>();

    identityProviderService.getIdentityProvider(identityProviderId).ifPresent(idp -> {
      String institutionId = idp.getInstitutionId();
      if (StringUtils.isBlank(institutionId)) {
        result.add(convertIdentityProviderToInstitutionIdentityProvider(idp)) ;
      } else {
        identityProviderService.getInstituteIdentityProviders(institutionId)
          .forEach(provider -> result.add(convertIdentityProviderToInstitutionIdentityProvider(provider)));
      }
    });

    return result;
  }

  @Override
  public List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders() {
    List<InstitutionIdentityProvider> result = new ArrayList<>();
    List<IdentityProvider> identityProviders = identityProviderService.getAllIdentityProviders();

    for (IdentityProvider identityProvider : identityProviders) {
      result.add(convertIdentityProviderToInstitutionIdentityProvider(identityProvider));
    }

    return result;
  }

  @Override
  public List<InstitutionIdentityProvider> serviceUsedBy(String spEntityId) {
    return identityProviderService.getLinkedIdentityProviders(spEntityId).stream()
        .map(this::convertIdentityProviderToInstitutionIdentityProvider)
        .collect(toList());
  }

  @Override
  public List<Action> getJiraActions(String idpEntityId) {
    return actionsService.getActions(idpEntityId);
  }

  @Override
  public Taxonomy getTaxonomy() {
    Iterable<Facet> facets = facetDao.findAll();
    List<Category> categories = StreamSupport.stream(facets.spliterator(), false).map(facet -> {
      Category category = new Category(facet.getName());
      List<CategoryValue> values = facet.getFacetValues().stream().map(fv -> new CategoryValue(fv.getValue())).collect(toList());
      category.setValues(values);
      return category;
    }).collect(toList());

    for (Category category : categories) {
      List<CategoryValue> values = category.getValues();
      for (CategoryValue value : values) {
        value.setCategory(category);
      }
    }
    return new Taxonomy(categories);

//    Taxonomy taxonomy = csaRestTemplate.getForEntity(serviceUrl + "/api/public/taxonomy.json?lang={lang}", Taxonomy.class, getLocale()).getBody();
  }

  @Override
  public Service getServiceForIdp(String idpEntityId, long serviceId) {
    return doGetServicesForIdP(getLocale(), idpEntityId, true).stream()
        .filter(service -> service.getId() == serviceId)
        .findFirst()
        .orElseThrow(() -> new RuntimeException("Non-existent service ID('" + serviceId + "')"));
  }

  @Override
  public Action createAction(Action action) {
    ServiceProvider serviceProvider = serviceProviderService.getServiceProvider(action.getSpId());
    IdentityProvider identityProvider = identityProviderService.getIdentityProvider(action.getIdpId()).orElseThrow(RuntimeException::new);

    action.setSpName(serviceProvider.getName());
    action.setIdpName(identityProvider.getName());

    String issueKey = null;
    if (createAdministrationJiraTicket) {
      actionsService.registerJiraIssueCreation(action);
    }
    action = actionsService.registerAction(action);
    if (sendAdministrationEmail) {
      sendAdministrationEmail(serviceProvider, identityProvider, issueKey, action);
    }
    return action;
  }

  @Override
  public List<LicenseContactPerson> licenseContactPersons(String idpEntityId) {
    return licenseContactPersonService.licenseContactPersons(idpEntityId);
  }

  private List<Service> restoreCategoryReferences(List<Service> services) {
    services.forEach(Service::restoreCategoryReferences);
    return services;
  }

  /*
  * Note: this is a deliberate design choice. We want to be able to transparently call Csa services without passing in HttpServletRequest and / or Locale.
  */
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

  private void addArticle(Article article, Service service) {
    // CRM-related properties
    if (article != null && !article.equals(Article.NONE)) {
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

  private InstitutionIdentityProvider convertIdentityProviderToInstitutionIdentityProvider(IdentityProvider identityProvider) {
    return new InstitutionIdentityProvider(identityProvider.getId(), identityProvider.getName(), identityProvider.getInstitutionId());
  }

  private void sendAdministrationEmail(ServiceProvider serviceProvider, IdentityProvider identityProvider, String issueKey, Action action) {
    String subject = String.format("[Csa (" + getHost() + ") request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
      action.getType().name(), action.getIdpId(), action.getSpId(), issueKey);

    StringBuilder body = new StringBuilder();
    body.append("Domain of Reporter: " + action.getInstitutionId() + "\n");
    body.append("SP EntityID: " + serviceProvider.getId() + "\n");
    body.append("SP Name: " + serviceProvider.getName() + "\n");

    body.append("IdP EntityID: " + identityProvider.getId() + "\n");
    body.append("IdP Name: " + identityProvider.getName() + "\n");


    body.append("Request: " + action.getType().name() + "\n");
    body.append("Applicant name: " + action.getUserName() + "\n");
    body.append("Applicant email: " + action.getUserEmail() + " \n");
    body.append("Mail applicant: mailto:" + action.getUserEmail() + "?CC=surfconext-beheer@surfnet.nl&SUBJECT=[" + issueKey + "]%20" + action.getType().name() + "%20to%20" + serviceProvider.getName() + "&BODY=Beste%20" + action.getUserName() + " \n");

    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:MM");
    body.append("Time: " + sdf.format(new Date()) + "\n");
    body.append("Remark from User:\n");
    body.append(action.getBody());
    emailService.sendMail(action.getUserEmail(), subject.toString(), body.toString());
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
    }
  }
}
