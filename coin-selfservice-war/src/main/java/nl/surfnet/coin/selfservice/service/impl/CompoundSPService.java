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

package nl.surfnet.coin.selfservice.service.impl;

import java.util.AbstractMap;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.hibernate.HibernateException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Abstraction for the Compound Service Providers. This deals with persistence
 * and linking to Service Providers
 */
@Component
public class CompoundSPService {

  private static final int LMNG_TIMEOUT_AFTER_EXCEPTION_SECONDS = 600;

  @Value("${lmngArticleCacheSeconds}")
  private int lmngArticleCacheExpireSeconds;

  @Value("${lmngLicenseCacheSeconds}")
  private int lmngLicenseCacheExpireSeconds;

  private Logger LOG = LoggerFactory.getLogger(CompoundSPService.class);

  /*
   * Cached resultlist of LMNG license data. A resultlist is stored per IDP with
   * an expire date
   */
  private Map<AbstractMap.SimpleEntry<IdentityProvider, Article>, AbstractMap.SimpleEntry<DateTime, List<License>>> cachedLicenses;

  /*
   * Cached resultlist of LMNG article data. A resultlist is IDP independent but
   * has an expire date
   */
  private AbstractMap.SimpleEntry<DateTime, List<Article>> cachedArticles;

  @Resource
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Resource(name = "providerService")
  private ServiceProviderService serviceProviderService;

  @Resource
  private LmngService licensingService;

  public List<CompoundServiceProvider> getCSPsByIdp(IdentityProvider identityProvider) {

    // Base: the list of all service providers for this IDP
    List<ServiceProvider> allServiceProviders = serviceProviderService.getAllServiceProviders(identityProvider.getId());

    // Reference data: all compound service providers
    List<CompoundServiceProvider> allBareCSPs = compoundServiceProviderDao.findAll();
    // Mapped by its SP entity ID
    Map<String, CompoundServiceProvider> mapByServiceProviderEntityId = mapByServiceProviderEntityId(allBareCSPs);

    // Build a list of CSPs. Create new ones for SPs that have no CSP yet.
    List<CompoundServiceProvider> all = new ArrayList<CompoundServiceProvider>();
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
    Article article = getCachedArticle(sp, false);
    csp.setArticle(article);
    csp.setLicenses(getCachedLicenses(identityProvider, article));
    return csp;
  }

  /**
   * Create a CSP for the given SP.
   * 
   * @param sp
   *          the SP
   * @return the created (and persisted) CSP
   */
  private CompoundServiceProvider createCompoundServiceProvider(IdentityProvider idp, ServiceProvider sp) {
    Article article = getCachedArticle(sp, false);
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp, article);
    csp.setLicenses(getCachedLicenses(idp, article));
    try {
      compoundServiceProviderDao.saveOrUpdate(csp);
      compoundServiceProviderDao.evict();
    } catch (RuntimeException e) {
      if (e instanceof HibernateException || e instanceof DataAccessException) {
        //let's give the database another try, otherwise rethrow
        CompoundServiceProvider cspFromDb = compoundServiceProviderDao.findByEntityId(sp.getId());
        if (cspFromDb != null) {
          csp = compound(idp, sp, cspFromDb);
        } else {
          throw e;
        }
      }
    }
    return csp;
  }

  private Map<String, CompoundServiceProvider> mapByServiceProviderEntityId(List<CompoundServiceProvider> allCSPs) {
    Map<String, CompoundServiceProvider> map = new HashMap<String, CompoundServiceProvider>();
    for (CompoundServiceProvider csp : allCSPs) {
      map.put(csp.getServiceProviderEntityId(), csp);
    }
    return map;
  }

  /**
   * Get a CSP by its ID, for the given IDP.
   * 
   * @param idp
   *          the IDP
   * @param compoundSpId
   *          long
   * @return
   */
  public CompoundServiceProvider getCSPById(IdentityProvider idp, long compoundSpId, boolean refreshCache) {
    CompoundServiceProvider csp = compoundServiceProviderDao.findById(compoundSpId);
    ServiceProvider sp = serviceProviderService.getServiceProvider(csp.getServiceProviderEntityId(), idp.getId());
    if (sp == null) {
      LOG.info("Cannot get serviceProvider by known entity id: {}, cannot enrich CSP with SP information.",
          csp.getServiceProviderEntityId());
      return csp;
    }
    csp.setServiceProvider(sp);
    Article article = getCachedArticle(sp, refreshCache);
    csp.setArticle(article);
    csp.setLicenses(getCachedLicenses(idp, article));
    return csp;
  }

  /**
   * Get a CSP by its ServiceProvider
   * 
   * @param serviceProviderEntityId
   *          the ServiceProvider
   * @return
   */
  public CompoundServiceProvider getCSPById(String serviceProviderEntityId) {

    ServiceProvider serviceProvider = serviceProviderService.getServiceProvider(serviceProviderEntityId);
    Assert.notNull(serviceProvider, "No such SP with entityId: " + serviceProviderEntityId);

    CompoundServiceProvider compoundServiceProvider = compoundServiceProviderDao.findByEntityId(serviceProvider.getId());
    if (compoundServiceProvider == null) {
      LOG.debug("No compound Service Provider for SP '{}' yet. Will init one and persist.", serviceProviderEntityId);
      compoundServiceProvider = CompoundServiceProvider.builder(serviceProvider, getCachedArticle(serviceProvider, false));
      compoundServiceProviderDao.saveOrUpdate(compoundServiceProvider);
      LOG.debug("Persisted a CompoundServiceProvider with id {}");
    } else {
      compoundServiceProvider.setServiceProvider(serviceProvider);
      compoundServiceProvider.setArticle(getCachedArticle(serviceProvider, false));
    }
    return compoundServiceProvider;
  }

  /**
   * Get an (LNMG)article for the given SP, the article will be retrieved from
   * the cache if it's available and not expired. Otherwise a new list of
   * articles for all SP's will be retrieved from LMNG and placed in the cache
   * with an expiration date based upon the lmngArticleCacheExpireSeconds
   * variable.
   * 
   * @param sp
   *          the SP to get the article for
   * @param refreshCache
   *          if true the cache will be forced to refresh (expirationdate
   *          independent)
   * @return the (possibly cached) article
   */
  private Article getCachedArticle(ServiceProvider sp, boolean refreshCache) {
    if (!licensingService.isActiveMode()) {
      LOG.debug("Returning Article.NONE because lmngService is inactive");
      return Article.NONE;
    }
    Assert.notNull(sp);

    // Check and update (if needed) cache
    DateTime now = getNow();
    if (cachedArticles == null || refreshCache || cachedArticles.getKey().isBefore(now)) {
      List<ServiceProvider> allSps = serviceProviderService.getAllServiceProviders();
      List<String> allSpsIds = new ArrayList<String>();
      for (ServiceProvider serviceProvider : allSps) {
        allSpsIds.add(serviceProvider.getId());
      }
      List<Article> articles;
      DateTime invalidationDate = new DateTime(now).plusSeconds(lmngArticleCacheExpireSeconds);
      try {
        articles = licensingService.getArticlesForServiceProviders(allSpsIds);
      } catch (LmngException e) {
        // Cache cannot be updated. Continue using old values. create empty item
        // if we have nothing at all
        LOG.warn("Cache for articles cannot be updated. Exception thrown: " + e.getMessage());
        if (cachedArticles == null) {
          articles = Collections.emptyList();
        } else {
          articles = cachedArticles.getValue();
        }
        invalidationDate = new DateTime(now).plusSeconds(LMNG_TIMEOUT_AFTER_EXCEPTION_SECONDS);
      }
      cachedArticles = new SimpleEntry<DateTime, List<Article>>(invalidationDate, articles);
    }

    // find and return article from cache
    for (Article article : cachedArticles.getValue()) {
      if (article.getServiceProviderEntityId().equals(sp.getId())) {
        return article;
      }
    }

    LOG.debug("No Article found for given SP with ID " + sp.getId());
    return null;
  }

  /**
   * Get a list of (LNMG)licenses for the given IDP and Article, the licenses
   * will be retrieved from the cache if it's available and not expired.
   * Otherwise a new list of licenses for this IDP and article combination
   * (valid at the current date) will be retrieved from LMNG and placed in the
   * cache with an expiration date based upon the lmngArticleCacheExpireSeconds
   * variable. Cache items are based on a key of the combination 'IDP and
   * article'.
   * 
   * @param idp
   *          the IDP to get the license for
   * @param article
   *          the article to get the license for
   * @return the (possibly cached) list of licenses (in general there will be
   *         just 1 active license per IDP/article)
   */
  private List<License> getCachedLicenses(IdentityProvider idp, Article article) {
    if (!licensingService.isActiveMode()) {
      LOG.debug("Returning License.NONE because lmngService is inactive");
      return License.NONE;
    }

    Assert.notNull(idp);
    if (article != null) {
      if (cachedLicenses == null) {
        cachedLicenses = new HashMap<AbstractMap.SimpleEntry<IdentityProvider, Article>, AbstractMap.SimpleEntry<DateTime, List<License>>>();
      }
      DateTime now = getNow();
      SimpleEntry<IdentityProvider, Article> entry = new SimpleEntry<IdentityProvider, Article>(idp, article);
      SimpleEntry<DateTime, List<License>> cachedValue = cachedLicenses.get(entry);
      if (cachedValue == null || cachedValue.getKey().isBefore(now)) {
        List<License> licenses;
        DateTime invalidationDate = new DateTime(now).plusSeconds(lmngLicenseCacheExpireSeconds);
        try {
          licenses = licensingService.getLicensesForIdpAndSp(idp, article.getLmngIdentifier(), now.toDate());
        } catch (LmngException e) {
          // Cache cannot be updated. Continue using old values. create empty
          // item if we have nothing at all
          LOG.warn("Cache for licenses cannot be updated. Exception thrown: " + e.getMessage());
          if (cachedValue == null) {
            licenses = Collections.emptyList();
          } else {
            licenses = cachedValue.getValue();
          }
          invalidationDate = new DateTime(now).plusSeconds(LMNG_TIMEOUT_AFTER_EXCEPTION_SECONDS);
        }
        cachedValue = new SimpleEntry<DateTime, List<License>>(invalidationDate, licenses);
        cachedLicenses.put(entry, cachedValue);
      }
      return cachedValue.getValue();
    }
    // no article? -> no licenses either, return null.
    return null;
  }

  /**
   * @return a new DateTime object (representing this moment) used for possible
   *         overriding in tests
   */
  protected DateTime getNow() {
    return new DateTime();
  }

  public void setLmngArticleCacheExpireSeconds(int lmngArticleCacheExpireSeconds) {
    this.lmngArticleCacheExpireSeconds = lmngArticleCacheExpireSeconds;
  }

  public void setLmngLicenseCacheExpireSeconds(int lmngLicenseCacheExpireSeconds) {
    this.lmngLicenseCacheExpireSeconds = lmngLicenseCacheExpireSeconds;
  }
}
