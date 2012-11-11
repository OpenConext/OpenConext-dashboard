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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.ServiceProviderService;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

/**
 * Abstraction for the Compound Service Providers. This deals with persistence
 * and linking to Service Providers
 */
@Component
public class CompoundSPService {

  @Value("${lmngCacheSeconds}")
  private int lmngCacheExpireSeconds;

  private Logger LOG = LoggerFactory.getLogger(CompoundSPService.class);

  /*
   * Cached resultlist of LMNG data. A resultlist is stored per IDP with an
   * expire date
   */
  private final Map<IdentityProvider, AbstractMap.SimpleEntry<DateTime, List<Article>>> lmngCachedResults = new HashMap<IdentityProvider, AbstractMap.SimpleEntry<DateTime, List<Article>>>();

  @Resource
  private CompoundServiceProviderDao compoundServiceProviderDao;

  @Resource(name = "providerService")
  private ServiceProviderService serviceProviderService;

  @Resource
  private LicensingService licensingService;

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

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
        csp.setServiceProvider(sp);
        csp.setArticle(getCachedArticleForIdpAndSp(identityProvider, sp, false));
      } else {
        LOG.debug("No CompoundServiceProvider yet for SP with id {}, will create a new one.", sp.getId());
        csp = createCompoundServiceProvider(identityProvider, sp);
      }
      all.add(csp);
    }
    return all;
  }

  /**
   * Create a CSP for the given SP. 
   * 
   * @param sp
   *          the SP
   * @return the created (and persisted) CSP
   */
  private CompoundServiceProvider createCompoundServiceProvider(IdentityProvider idp, ServiceProvider sp) {
    CompoundServiceProvider csp = CompoundServiceProvider.builder(sp, getCachedArticleForIdpAndSp(idp, sp, false));
    compoundServiceProviderDao.saveOrUpdate(csp);
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
    csp.setArticle(getCachedArticleForIdpAndSp(idp, sp, refreshCache));
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
      compoundServiceProvider = CompoundServiceProvider.builder(serviceProvider,
          getCachedArticleForIdpAndSp(IdentityProvider.NONE, serviceProvider, false));
      compoundServiceProviderDao.saveOrUpdate(compoundServiceProvider);
      LOG.debug("Persisted a CompoundServiceProvider with id {}");
    } else {
      compoundServiceProvider.setServiceProvider(serviceProvider);
      compoundServiceProvider.setArticle(getCachedArticleForIdpAndSp(IdentityProvider.NONE, serviceProvider, false));

    }
    return compoundServiceProvider;
  }

  /**
   * Get an article from the licensingservice belonging to the given IDP and SP
   * 
   * @param idp
   * @param sp
   * @return
   */
  private Article getCachedArticleForIdpAndSp(IdentityProvider idp, ServiceProvider sp, boolean refreshCache) {
    if (!licensingService.isActiveMode()) {
      LOG.info("Returning Article.NONE because licensingService is inactive");
      return Article.NONE;
    }
    Assert.notNull(idp);
    Assert.notNull(sp);
    String spLmngIdentifier = lmngIdentifierDao.getLmngIdForServiceProviderId(sp.getId());
    if (spLmngIdentifier != null) {
      for (Article article : getCachedArticlesForIdp(idp, refreshCache)) {
        if (article.getLmngIdentifier().equals(spLmngIdentifier)) {
          // Get first article (we expect a max of one article per SP)
          return article;
        }
      }
    }
    LOG.debug("No lmngidentifier for this SP or no LMNG result found, nothing to return. LmngID = " + spLmngIdentifier);
    return null;
  }

  /**
   * Get the list of articles from the cache (or from LMNG if cache is empty or
   * invalid).
   * 
   * @param idp
   *          the corresponding IDP
   * @return the list of articles belonging to the IDP
   */
  private List<Article> getCachedArticlesForIdp(IdentityProvider idp, boolean refreshCache) {
    if (!licensingService.isActiveMode()) {
      LOG.info("Returning null because licensingService is inactive");
      return null;
    }
    SimpleEntry<DateTime, List<Article>> result = lmngCachedResults.get(idp);
    DateTime now = getNow();
    if (result == null || result.getKey().isBefore(now) || refreshCache) {
      // reload from lmng
      List<ServiceProvider> allServiceProviders =
        idp == IdentityProvider.NONE
          ? serviceProviderService.getAllServiceProviders()
          : serviceProviderService.getAllServiceProviders(idp.getId());
      List<Article> lmngResult = licensingService.getArticleForIdentityProviderAndServiceProviders(idp, allServiceProviders, now.toDate());
      if (lmngResult == null || lmngResult.isEmpty()) {
        LOG.warn("No LMNG data retrieved. Cache not updated.");
        // return current value (possibly null)
        return result == null ? new ArrayList<Article>() : result.getValue();
      } else {
        DateTime invalidationDate = new DateTime(now).plusSeconds(lmngCacheExpireSeconds);
        result = new SimpleEntry<DateTime, List<Article>>(invalidationDate, lmngResult);
        lmngCachedResults.put(idp, result);
        return result.getValue();
      }
    } else {
      return result.getValue();
    }
  }

  /**
   * @return a new DateTime object (representing this moment) used for possible
   *         overriding in tests
   */
  protected DateTime getNow() {
    return new DateTime();
  }

  public void setLmngCacheExpireSeconds(int lmngCacheExpireSeconds) {
    this.lmngCacheExpireSeconds = lmngCacheExpireSeconds;
  }
}
