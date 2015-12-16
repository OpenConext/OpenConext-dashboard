/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package selfservice.api.csa;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import selfservice.api.cache.CrmCache;
import selfservice.api.cache.ProviderCache;
import selfservice.api.cache.ServicesCache;
import selfservice.domain.CrmArticle;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.IdentityProvider;
import selfservice.interceptor.AuthorityScopeInterceptor;
import selfservice.domain.License;
import selfservice.domain.Service;

@Controller
@RequestMapping
public class CsaServicesController extends BaseApiController {

  @Resource
  private ServicesCache servicesCache;

  @Resource
  private ProviderCache providerCache;

  @Resource
  private CrmCache crmCache;

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/services.json")
  @ResponseBody
  public List<Service> getProtectedServices(@RequestParam(value = "lang", defaultValue = "en") String language,
                                     final HttpServletRequest request) {
    String ipdEntityId = getIdpEntityIdFromToken(request);
    /*
     * Non-client-credential client where we only return linked services
     */
    return doGetServicesForIdP(language, ipdEntityId, false);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/service.json")
  @ResponseBody
  public Service getServiceForSpEntityId(@RequestParam(value = "lang", defaultValue = "en") String language,
                                  @RequestParam(value = "idpEntityId") String idpEntityId,
                                  @RequestParam(value = "spEntityId") String spEntityId,
                                  HttpServletRequest request) {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES);
    List<Service> allServices = doGetServicesForIdP(language, idpEntityId, true);
    for (Service service : allServices) {
      if (service.getSpEntityId().equals(spEntityId)) {
        return service;
      }
    }
    throw new RuntimeException("Non-existent service by sp entity id '" + spEntityId + "'");
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/idp/services.json")
  @ResponseBody public List<Service> getProtectedServicesByIdp(
    @RequestParam(value = "lang", defaultValue = "en") String language,
    @RequestParam(value = "idpEntityId") String idpEntityId,
    HttpServletRequest request) {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES);
    /*
     * Client-credential client where we also return non-linked services (e.g. dashboard functionality)
     */
    return doGetServicesForIdP(language, idpEntityId, true);
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/services/{serviceId}.json")
  @ResponseBody
  public Service getServiceForIdp(
      @PathVariable("serviceId") long serviceId,
      @RequestParam(value = "lang", defaultValue = "en") String language,
      @RequestParam(value = "idpEntityId") String idpEntityId,
      final HttpServletRequest request) {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES);
    List<Service> allServices = doGetServicesForIdP(language, idpEntityId, true);
    for (Service service : allServices) {
      if (service.getId() == serviceId) {
        return service;
      }
    }
    throw new RuntimeException("Non-existent service ID('" + serviceId + "')");
  }

  @RequestMapping(method = RequestMethod.GET, value = "/api/protected/cache/clear.json")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @ResponseBody
  public void clearCache(final HttpServletRequest request) {
    verifyScope(request, AuthorityScopeInterceptor.OAUTH_CLIENT_SCOPE_CROSS_IDP_SERVICES);
    this.providerCache.evictSynchronously();
    this.servicesCache.evictSynchronously();
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

}
