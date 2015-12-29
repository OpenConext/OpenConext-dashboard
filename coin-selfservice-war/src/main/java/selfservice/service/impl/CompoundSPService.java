/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package selfservice.service.impl;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.function.Function.identity;
import static java.util.stream.StreamSupport.stream;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.hibernate.HibernateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import selfservice.dao.CompoundServiceProviderDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.License;
import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.CompoundServiceProvider;
import selfservice.service.CrmService;
import selfservice.service.ServiceProviderService;

/**
 * Abstraction for the Compound Service Providers. This deals with persistence
 * and linking to Service Providers
 */
@Service
public class CompoundSPService {

  private final Logger LOG = LoggerFactory.getLogger(CompoundSPService.class);

  @Autowired
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Autowired
  private ServiceProviderService serviceProviderService;

  @Autowired
  private CrmService licensingService;

  public List<CompoundServiceProvider> getAllCSPs(long callDelay) {
    List<ServiceProvider> allServiceProviders;
    if (callDelay > 0) {
      allServiceProviders = serviceProviderService.getAllServiceProvidersRateLimited(callDelay);
    } else {
      allServiceProviders = serviceProviderService.getAllServiceProviders(true);
    }
    return getCSPs(null, allServiceProviders);
  }

  public List<CompoundServiceProvider> getAllBareCSPs() {
    List<ServiceProvider> allServiceProviders = serviceProviderService.getAllServiceProviders(false);
    return getCSPs(null, allServiceProviders);
  }

  public List<CompoundServiceProvider> getCSPsByIdp(IdentityProvider identityProvider) {
    if (identityProvider == null) {
      return new ArrayList<>();
    }
    // Base: the list of all service providers for this IDP
    List<ServiceProvider> allServiceProviders = serviceProviderService.getAllServiceProviders(identityProvider.getId());
    return getCSPs(identityProvider, allServiceProviders);
  }

  private List<CompoundServiceProvider> getCSPs(IdentityProvider identityProvider, List<ServiceProvider> allServiceProviders) {
    // Reference data: all compound service providers
    Iterable<CompoundServiceProvider> allBareCSPs = compoundServiceProviderDao.findAll();
    // Mapped by its SP entity ID
    Map<String, CompoundServiceProvider> mapByServiceProviderEntityId = stream(allBareCSPs.spliterator(), false).collect(Collectors.toMap(csp -> csp.getServiceProviderEntityId(), identity()));
    // Build a list of CSPs. Create new ones for SPs that have no CSP yet.
    List<CompoundServiceProvider> all = new ArrayList<>();
    for (ServiceProvider sp : allServiceProviders) {
      CompoundServiceProvider csp;
      if (mapByServiceProviderEntityId.containsKey(sp.getId())) {
        csp = mapByServiceProviderEntityId.get(sp.getId());
        csp = compound(identityProvider, sp, csp);
      } else {
        LOG.debug("No CompoundServiceProvider yet for SP with id {}, will create a new one.", sp.getId());
        csp = createCompoundServiceProvider(identityProvider, sp);
      }
      all.add(csp);
    }
    return all;
  }

  private CompoundServiceProvider compound(IdentityProvider identityProvider, ServiceProvider sp, CompoundServiceProvider csp) {
    csp.setServiceProvider(sp);
    Article article = getArticleForSp(sp);
    csp.setArticle(article);
    if (identityProvider != null) {
      csp.setLicenses(getLicensesForIdpAndArticle(identityProvider, article));
    }
    return csp;
  }

  /**
   * Create a CSP for the given SP.
   *
   * @param sp the SP
   * @return the created (and persisted) CSP
   */
  private CompoundServiceProvider createCompoundServiceProvider(IdentityProvider idp, ServiceProvider sp) {
    Article article = getArticleForSp(sp);
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp, article);
    if (idp != null) {
      csp.setLicenses(getLicensesForIdpAndArticle(idp, article));
    }
    try {
      csp = compoundServiceProviderDao.save(csp);
    } catch (RuntimeException e) {
      if (e instanceof HibernateException || e instanceof DataAccessException) {
        //let's give the database another try, otherwise rethrow
        CompoundServiceProvider cspFromDb = compoundServiceProviderDao.findByServiceProviderEntityId(sp.getId());
        if (cspFromDb != null) {
          csp = compound(idp, sp, cspFromDb);
        } else {
          throw e;
        }
      }
    }
    return csp;
  }

  public CompoundServiceProvider getCSPById(IdentityProvider idp, long compoundSpId) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findOne(compoundSpId);
    if (csp == null) {
      LOG.debug("Cannot find CSP by id {}, will return null", compoundSpId);
      return null;
    }
    ServiceProvider sp = serviceProviderService.getServiceProvider(csp.getServiceProviderEntityId(), idp.getId());
    if (sp == null) {
      LOG.info("Cannot get serviceProvider by known entity id: {}, cannot enrich CSP with SP information.",
        csp.getServiceProviderEntityId());
      return csp;
    }
    csp.setServiceProvider(sp);
    Article article = getArticleForSp(sp);
    csp.setArticle(article);
    csp.setLicenses(getLicensesForIdpAndArticle(idp, article));
    return csp;
  }

  public CompoundServiceProvider getCSPByServiceProviderEntityId(String serviceProviderEntityId) {
    ServiceProvider serviceProvider = serviceProviderService.getServiceProvider(serviceProviderEntityId);
    Assert.notNull(serviceProvider, "No such SP with entityId: " + serviceProviderEntityId);
    return getCSPByServiceProvider(serviceProvider);
  }

  public CompoundServiceProvider getCSPByServiceProvider(ServiceProvider serviceProvider) {
    checkNotNull(serviceProvider, "ServiceProvider may not be null");

    CompoundServiceProvider compoundServiceProvider = compoundServiceProviderDao.findByServiceProviderEntityId(serviceProvider.getId());
    if (compoundServiceProvider == null) {
      LOG.debug("No compound Service Provider for SP '{}' yet. Will init one and persist.", serviceProvider.getId());
      compoundServiceProvider = CompoundServiceProvider.builder(serviceProvider, getArticleForSp(serviceProvider));
      compoundServiceProvider = compoundServiceProviderDao.save(compoundServiceProvider);
      LOG.debug("Persisted a CompoundServiceProvider with id {}");
    } else {
      compoundServiceProvider.setServiceProvider(serviceProvider);
      compoundServiceProvider.setArticle(getArticleForSp(serviceProvider));
      compoundServiceProvider.updateTransientOriginFields();
    }
    return compoundServiceProvider;
  }

  private Article getArticleForSp(ServiceProvider sp) {
    checkNotNull(sp);

    List<String> allSpsIds = new ArrayList<>();
    allSpsIds.add(sp.getId());
    List<Article> articles = licensingService.getArticlesForServiceProviders(allSpsIds);

    for (Article article : articles) {
      if (article.getServiceProviderEntityId().equals(sp.getId())) {
        return article;
      }
    }
    return null;
  }

  private List<License> getLicensesForIdpAndArticle(IdentityProvider idp, Article article) {
    checkNotNull(idp);

    if (article != null) {
      return licensingService.getLicensesForIdpAndSp(idp, article.getLmngIdentifier());
    }
    return null;
  }

}
