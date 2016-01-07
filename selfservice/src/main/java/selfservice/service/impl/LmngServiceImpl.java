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
import static org.apache.http.HttpVersion.HTTP_1_1;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.common.base.Throwables;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.IdentityProvider;
import selfservice.domain.License;
import selfservice.domain.csa.Account;
import selfservice.domain.csa.Article;
import selfservice.service.CrmService;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements CrmService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String PATH_FETCH_QUERY_GET_INSTITUTION = "lmngqueries/lmngQueryGetInstitution.xml";

  private final CrmUtil lmngUtil = new LmngUtil();
  private final HttpClient httpClient;

  private final LmngIdentifierDao lmngIdentifierDao;
  private final String endpoint;

  public LmngServiceImpl(LmngIdentifierDao lmngIdentifierDao, String endpoint) {
    RequestConfig config = RequestConfig.custom()
        .setConnectTimeout(5000)
        .setSocketTimeout(10000).build();
    this.httpClient = HttpClients.custom().setDefaultRequestConfig(config).build();

    this.lmngIdentifierDao = lmngIdentifierDao;
    this.endpoint = endpoint;
  }

  @Cacheable(value = "crm")
  @Override
  public List<License> getLicensesForIdpAndSp(IdentityProvider identityProvider, String articleIdentifier) {
    checkNotNull(identityProvider);
    checkNotNull(articleIdentifier);

    List<License> result = new ArrayList<>();

    String lmngInstitutionId = getLmngIdentityId(identityProvider);

    if (!StringUtils.hasText(lmngInstitutionId)) {
      return result;
    }

    // apparently LMNG has a problem retrieving licenses when there has been a revision to the underlying agreement
    // yields the license. For this reason, we have two extra queries that we do when no licenses are found
    return Arrays.stream(CrmUtil.LicenseRetrievalAttempt.values()).map(attempt -> {
      try {
        String soapRequest = lmngUtil.getLmngSoapRequestForIdpAndSp(lmngInstitutionId, Arrays.asList(articleIdentifier), new Date(), endpoint, attempt);
        String webserviceResult = getWebServiceResult(soapRequest);
        return lmngUtil.parseLicensesResult(webserviceResult);
      } catch (Exception e) {
        log.error("Exception while retrieving licenses for article " + articleIdentifier, e);
        return Collections.<License>emptyList();
      }
    }).filter(r -> r.size() > 0).findFirst().orElse(Collections.<License>emptyList());
  }

  @Cacheable(value = "crm")
  @Override
  public List<Article> getArticlesForServiceProviders(List<String> serviceProvidersEntityIds) throws LmngException {
    try {
      Map<String, String> serviceIds = getLmngServiceIds(serviceProvidersEntityIds);

      if (CollectionUtils.isEmpty(serviceIds)) {
        return Collections.emptyList();
      }

      String soapRequest = lmngUtil.getLmngSoapRequestForSps(serviceIds.keySet(), endpoint);

      List<Article> parsedArticles = lmngUtil.parseArticlesResult(getWebServiceResult(soapRequest));

      parsedArticles.forEach(article -> article.setServiceProviderEntityId(serviceIds.get(article.getLmngIdentifier())));

      return parsedArticles;
    } catch (Exception e) {
      String exceptionMessage = String.format("Error retrieving articlesForServiceProviders. SP ids: %s", serviceProvidersEntityIds.toString());
      log.error(exceptionMessage, e);
      throw new LmngException(exceptionMessage, e);
    }
  }

  @Cacheable(value = "crm")
  @Override
  public String getServiceName(String guid) {
    Article article = getService(guid);
    return article == null ? null : article.getArticleName();
  }

  @Cacheable(value = "crm")
  @Override
  public Article getService(final String guid) {
    try {
      String soapRequest = lmngUtil.getLmngSoapRequestForSps(Arrays.asList(guid), endpoint);

      List<Article> resultList = lmngUtil.parseArticlesResult(getWebServiceResult(soapRequest));

      if (!CollectionUtils.isEmpty(resultList)) {
        return resultList.get(0);
      }
    } catch (Exception e) {
      log.error("Exception while retrieving article/license", e);
    }

    return null;
  }

  @Override
  public List<Account> getAccounts(boolean isInstitution) {
    List<Account> accounts = new ArrayList<>();
    try {
      String soapRequest = lmngUtil.getLmngSoapRequestForAllAccount(isInstitution, endpoint);
      String webserviceResult = getWebServiceResult(soapRequest);
      accounts = lmngUtil.parseAccountsResult(webserviceResult);
    } catch (Exception e) {
      log.error("Exception while retrieving article/license", e);
    }
    return accounts;
  }

  @Override
  @Cacheable(value = "crm")
  public String getInstitutionName(String guid) {
    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_GET_INSTITUTION);

    try (InputStream inputStream = queryResource.getInputStream()) {
      String soapRequest = lmngUtil.getLmngRequestEnvelope();

      String query = IOUtils.toString(inputStream).replaceAll(LmngUtil.INSTITUTION_IDENTIFIER_PLACEHOLDER, guid);

      soapRequest = soapRequest
          .replaceAll(LmngUtil.QUERY_PLACEHOLDER, StringEscapeUtils.escapeHtml4(query))
          .replaceAll(LmngUtil.ENDPOINT_PLACEHOLDER, endpoint)
          .replaceAll(LmngUtil.UID_PLACEHOLDER, UUID.randomUUID().toString());

      String webserviceResult = getWebServiceResult(soapRequest);

      return lmngUtil.parseResultInstitute(webserviceResult);
    } catch (Exception e) {
      log.error("Exception while retrieving article/license with GUID: {}", guid, e);
      return null;
    }
  }

  private String getWebServiceResult(final String soapRequest) throws IOException {
    log.debug("Calling the LMNG proxy webservice, endpoint: {}", endpoint);

    HttpUriRequest postRequest = RequestBuilder.post()
        .setUri(endpoint)
        .setVersion(HTTP_1_1)
        .setEntity(new StringEntity(soapRequest, ContentType.create("application/soap+xml", StandardCharsets.UTF_8))).build();

    long beforeCall = System.currentTimeMillis();
    HttpResponse httpResponse = httpClient.execute(postRequest);
    long afterCall = System.currentTimeMillis();
    log.debug("LMNG proxy webservice called in {} ms. Http response: {}", afterCall - beforeCall, httpResponse);

    String stringResponse = EntityUtils.toString(httpResponse.getEntity());

    if (httpResponse.getStatusLine().getStatusCode() != 200) {
      log.debug("LMNG webservice response content is:\n{}", stringResponse);
      throw new RuntimeException("Invalid response from LMNG webservice. Http response " + httpResponse);
    }

    return stringResponse;
  }

  private String getLmngIdentityId(IdentityProvider identityProvider) {
    // currently institutionId can be null, so check first
    if (identityProvider == null || identityProvider.getInstitutionId() == null) {
      return null;
    }

    return lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getInstitutionId());
  }

  private String getLmngServiceId(String serviceProviderEntityId) {
    return lmngIdentifierDao.getLmngIdForServiceProviderId(serviceProviderEntityId);
  }

  /**
   * Get the LMNG identifiers for the given SP list
   *
   * @return a map with the LMNGID as key and serviceprovider entity ID as value
   */
  private Map<String, String> getLmngServiceIds(List<String> serviceProvidersEntityIds) {
    Map<String, String> result = new HashMap<>();

    for (String spId : serviceProvidersEntityIds) {
      String serviceId = getLmngServiceId(spId);
      if (serviceId != null) {
        result.put(serviceId, spId);
      }
    }
    return result;
  }

  @Override
  public String performQuery(String rawQuery) {
    try {
      String query = StringEscapeUtils.escapeHtml4(rawQuery);

      String soapRequest = lmngUtil.getLmngRequestEnvelope()
          .replaceAll(LmngUtil.QUERY_PLACEHOLDER, query)
          .replaceAll(LmngUtil.ENDPOINT_PLACEHOLDER, endpoint)
          .replaceAll(LmngUtil.UID_PLACEHOLDER, UUID.randomUUID().toString());

      return getWebServiceResult(soapRequest);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  @CacheEvict(value = "crm", allEntries = true)
  public void evictCache() {
    log.info("Clear cache...");
  }

}
