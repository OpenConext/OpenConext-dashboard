package selfservice.service.impl;

import static java.util.stream.Collectors.toList;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import selfservice.cache.CrmCache;
import selfservice.cache.IdentityProviderCache;
import selfservice.cache.ServicesCache;
import selfservice.dao.FacetDao;
import selfservice.domain.Action;
import selfservice.domain.Category;
import selfservice.domain.CategoryValue;
import selfservice.domain.CrmArticle;
import selfservice.domain.Facet;
import selfservice.domain.IdentityProvider;
import selfservice.domain.LicenseStatus;
import selfservice.domain.Service;
import selfservice.domain.ServiceProvider;
import selfservice.domain.Taxonomy;
import selfservice.domain.csa.Article;
import selfservice.service.ActionsService;
import selfservice.service.Csa;
import selfservice.service.EmailService;
import selfservice.service.IdentityProviderService;
import selfservice.service.ServiceProviderService;

public class CsaImpl implements Csa {

  @Autowired
  private EmailService emailService;

  @Value("${administration.email.enabled}")
  private boolean sendAdministrationEmail;

  @Value("${administration.jira.ticket.enabled}")
  private boolean createAdministrationJiraTicket;

  @Autowired
  private ServiceProviderService serviceProviderService;

  @Autowired
  private FacetDao facetDao;

  @Autowired
  private ActionsService actionsService;

  @Autowired
  private ServicesCache servicesCache;

  @Autowired
  private IdentityProviderCache identityProviderCache;

  @Autowired
  private CrmCache crmCache;

  @Autowired
  private IdentityProviderService identityProviderService;

  private final String defaultLocale = "en";

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    return doGetServicesForIdP(getLocale(), idpEntityId);
  }

  private List<Service> doGetServicesForIdP(String language, String idpEntityId) {
    IdentityProvider identityProvider = Optional.ofNullable(identityProviderCache.getIdentityProvider(idpEntityId))
        .orElseThrow(() -> new IllegalArgumentException(String.format("No IdentityProvider known in SR with name:'%s'", idpEntityId)));

    List<String> serviceProviderIdentifiers = identityProviderCache.getServiceProviderIdentifiers(idpEntityId);

    return servicesCache.getAllServices(language).stream().filter(service -> {
      boolean isConnected = serviceProviderIdentifiers.contains(service.getSpEntityId());
      boolean showForInstitution = showServiceForInstitution(identityProvider, service);
      return showForInstitution || (service.isAvailableForEndUser() && isConnected);
    }).map(service -> {
        service.setConnected(serviceProviderIdentifiers.contains(service.getSpEntityId()));

        crmCache.getLicense(service, identityProvider.getInstitutionId()).ifPresent(license -> service.setLicense(license));
        crmCache.getArticle(service).map(this::getArticle).ifPresent(crmArticle -> {
          service.setHasCrmLink(true);
          service.setCrmArticle(crmArticle);
        });

        if (service.getLicenseStatus() == LicenseStatus.HAS_LICENSE_SURFMARKET) {
          service.setLicenseStatus(service.getLicense() != null ? LicenseStatus.HAS_LICENSE_SURFMARKET : LicenseStatus.NO_LICENSE);
        }

        return service;
    }).collect(Collectors.toList());
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
  }

  @Override
  public Service getServiceForIdp(String idpEntityId, long serviceId) {
    return doGetServicesForIdP(getLocale(), idpEntityId).stream()
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
    action = actionsService.saveAction(action);
    if (sendAdministrationEmail) {
      sendAdministrationEmail(serviceProvider, identityProvider, issueKey, action);
    }

    return action;
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

  private void sendAdministrationEmail(ServiceProvider serviceProvider, IdentityProvider identityProvider, String issueKey, Action action) {
    String subject = String.format(
        "[Csa (%s) request] %s connection from IdP '%s' to SP '%s' (Issue : %s)",
        getHost(), action.getType().name(), action.getIdpId(), action.getSpId(), issueKey);

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
