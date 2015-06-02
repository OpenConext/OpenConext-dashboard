package nl.surfnet.coin.selfservice.service.impl;

import nl.surfnet.coin.selfservice.domain.*;
import nl.surfnet.coin.selfservice.service.Csa;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.token.grant.client.ClientCredentialsResourceDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class CsaImpl implements Csa {

  private String defaultLocale = "en";

  private String accessTokenUri;

  private String clientId;

  private String clientSecret;

  private String spaceDelimitedScopes;

  private String serviceUrl;

  private OAuth2RestTemplate csaService;

  public CsaImpl(String accessTokenUri, String clientId, String clientSecret, String spaceDelimitedScopes, String serviceUrl) {
    this.accessTokenUri = accessTokenUri;
    this.clientId = clientId;
    this.clientSecret = clientSecret;
    this.spaceDelimitedScopes = spaceDelimitedScopes;
    this.serviceUrl = serviceUrl;
    csaService = new OAuth2RestTemplate(csaConfiguration());
  }

  private OAuth2ProtectedResourceDetails csaConfiguration() {
    ClientCredentialsResourceDetails details = new ClientCredentialsResourceDetails();
    details.setId("dashboard");
    details.setClientId(clientId);
    details.setClientSecret(clientSecret);
    details.setAccessTokenUri(accessTokenUri);
    details.setScope(Arrays.asList(spaceDelimitedScopes.split(" ")));
    return details;
  }


  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    String url = serviceUrl + "/api/protected/idp/services.json?idpEntityId={idpEntityId}&lang={lang}";
    return restoreCategoryReferences(Arrays.asList(csaService.getForEntity(url, Service[].class, idpEntityId, getLocale()).getBody()));
  }

  @Override
  public List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId) {
    String url = serviceUrl + "/api/protected/identityproviders.json?identityProviderId={identityProviderId}";
    return Arrays.asList(csaService.getForEntity(url, InstitutionIdentityProvider[].class, identityProviderId).getBody());

  }

  @Override
  public List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders() {
    String url = serviceUrl + "/api/protected/all-identityproviders.json";
    return Arrays.asList(csaService.getForEntity(url, InstitutionIdentityProvider[].class).getBody());
  }

  @Override
  public List<Action> getJiraActions(String idpEntityId) {
    String url = serviceUrl + "/api/protected/actions.json?idpEntityId={idpEntityId}";
    return Arrays.asList(csaService.getForEntity(url, Action[].class, idpEntityId).getBody());
  }

  @Override
  public Taxonomy getTaxonomy() {
    Taxonomy taxonomy = csaService.getForEntity(serviceUrl + "/api/public/taxonomy.json?lang={lang}", Taxonomy.class, getLocale()).getBody();
    List<Category> categories = taxonomy.getCategories();
    for (Category category : categories) {
      List<CategoryValue> values = category.getValues();
      for (CategoryValue value : values) {
        value.setCategory(category);
      }
    }
    return taxonomy;
  }

  @Override
  public List<OfferedService> findOfferedServicesFor(String idpEntityId) {
    String url = serviceUrl + "/api/protected/idp/offered-services.json?idpEntityId={idpEntityId}";
    Map<String, String> variables = new HashMap<>();
    variables.put("idpEntityId", idpEntityId);
    variables.put("lang", getLocale());
    return Arrays.asList(csaService.getForEntity(url, OfferedService[].class, idpEntityId, getLocale()).getBody());
  }

  @Override
  public Service getServiceForIdp(String idpEntityId, long serviceId) {
    String url = serviceUrl + "/api/protected/services/{serviceId}.json?idpEntityId={idpEntityId}&lang={lang}";
    Service service = csaService.getForEntity(url, Service.class, serviceId, idpEntityId, getLocale()).getBody();
    service.restoreCategoryReferences();
    return service;
  }

  @Override
  public Action createAction(Action action) {
    String url = serviceUrl + "/api/protected/service.json?idpEntityId={idpEntityId}&spEntityId={spEntityId}&lang={lang}";
    return csaService.postForEntity(url, action, Action.class).getBody();
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

}
