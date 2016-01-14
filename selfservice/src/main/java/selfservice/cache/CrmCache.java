package selfservice.cache;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import org.apache.commons.lang3.SerializationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.License;
import selfservice.domain.Service;
import selfservice.domain.csa.Article;
import selfservice.domain.csa.MappingEntry;
import selfservice.service.CrmService;

@Component
public class CrmCache extends AbstractCache {

  private static final Logger LOG = LoggerFactory.getLogger(CrmCache.class);

  private final CrmService crmService;
  private final LmngIdentifierDao lmngIdentifierDao;

  /**
   * Cache of Licenses, keyed by Entry of Idp institutionId and spEntityId
   */
  private AtomicReference<Map<MappingEntry, License>> licenseCache = new AtomicReference<>(new HashMap<>());

  /**
   * Cache of Articles, keyed by spEntityId
   */
  private AtomicReference<Map<String, Article>> articleCache = new AtomicReference<>(new HashMap<>());

  private List<MappingEntry> idpToLmngId;
  private List<MappingEntry> spToLmngId;

  @Autowired
  public CrmCache(CrmService crmService, LmngIdentifierDao lmngIdentifierDao,
                  @Value("${cache.crm.initialDelay}") long initialDelay,
                  @Value("${cache.crm.delay}") long delay) {
    super(initialDelay, delay);
    this.crmService = crmService;
    this.lmngIdentifierDao = lmngIdentifierDao;
  }

  @Override
  protected synchronized void doPopulateCache() {
    populateMappings();
    licenseCache.set(createNewLicensesCache());
    articleCache.set(createNewArticleCache());
  }

  private void populateMappings() {
    idpToLmngId = lmngIdentifierDao.findAllIdentityProviders();
    spToLmngId = lmngIdentifierDao.findAllServiceProviders();
  }

  private Map<String, Article> createNewArticleCache() {
    Map<String, Article> newCache = new HashMap<>();

    for (MappingEntry spAndLmngId : spToLmngId) {
      String spEntityId = spAndLmngId.getKey();
      String lmngId = spAndLmngId.getValue();

      List<Article> articlesForServiceProviders = crmService.getArticlesForServiceProviders(Collections.singletonList(spEntityId));

      if (articlesForServiceProviders.size() > 1) {
        LOG.info("Unexpected: list of articles for SP ({}) is larger than 1: {}", spEntityId, articlesForServiceProviders);
        articlesForServiceProviders.forEach(a -> LOG.info("Article found: {}", a));
      }

      if (articlesForServiceProviders.size() >= 1) {
        newCache.put(spEntityId, articlesForServiceProviders.get(0));
      } else {
        LOG.info("No article found for SP {}, with lmng id: {}", spEntityId, lmngId);
      }
    }
    return newCache;
  }

  private Map<MappingEntry, License> createNewLicensesCache() {
    Map<MappingEntry, License> newLicenseCache = new HashMap<>();

    for (MappingEntry idpLmngEntry : idpToLmngId) {
      String idpInstitutionId = idpLmngEntry.getKey();
      for (MappingEntry spLmngEntry : spToLmngId) {
        String spEntityId = spLmngEntry.getKey();
        String spLmngId = spLmngEntry.getValue();

        IdentityProvider idp = new IdentityProvider(idpInstitutionId, idpInstitutionId, "dummy");

        List<License> licensesForIdpAndSp = crmService.getLicensesForIdpAndSp(idp, spLmngId);
        if (licensesForIdpAndSp.isEmpty()) {
          continue;
        }

        if (licensesForIdpAndSp.size() > 1) {
          LOG.warn("Unexpected: list of licenses by IdP and SP ({} and {}) is larger than 1: {}", idpInstitutionId, spEntityId, licensesForIdpAndSp.size());
        }
        License license = licensesForIdpAndSp.get(0);
        LOG.trace("License found by IdP and SP ({} and {}): {}", idpInstitutionId, spEntityId, license);
        newLicenseCache.put(new MappingEntry(idpInstitutionId, spEntityId), license);
      }
    }
    return newLicenseCache;
  }

  public License getLicense(Service service, String idpInstitutionId) {
    if (service.getSpEntityId() == null || idpInstitutionId == null) {
      /*
       * First check:
       *
       * If this is the case then the Service is based upon a CRM guid defined in the csa.properties (key=public.api.lmng.guids). It must be displayed in the
       * services API , however it can't have a license as this is per-sp basis.
       *
       * Second check:
       *
       * It is possible that in SR there is no registered institutionID for an IdP. This is a misconfiguration, but not something we want to bring to the attention here.
       */
      return null;
    }

    MappingEntry entry = new MappingEntry(idpInstitutionId, service.getSpEntityId());
    License license = licenseCache.get().get(entry);
    LOG.debug("Looked for license for service {} and idpInstitutionId {}, and found: {}", service.getSpEntityId(), idpInstitutionId, license);
    return license;
  }

  public Article getArticle(Service service) {
    if (service.getSpEntityId() == null) {
      // This happens for 'crm only' services, with no reference to Service Registry's services.
      return null;
    }
    return SerializationUtils.clone(articleCache.get().get(service.getSpEntityId()));
  }

  @Override
  protected String getCacheName() {
    return "CRM Cache";
  }

}
