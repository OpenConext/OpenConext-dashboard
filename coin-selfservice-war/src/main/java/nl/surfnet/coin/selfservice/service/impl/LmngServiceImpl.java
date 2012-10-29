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

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.impl.ssl.KeyStore;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.apache.http.params.CoreProtocolPNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String ENDPOINT_PLACEHOLDER = "%ENDPOINT%";
  private static final String UID_PLACEHOLDER = "%UID%";
  private static final String QUERY_PLACEHOLDER = "%FETCH_QUERY%";
  private static final String INSTITUTION_IDENTIFIER_PLACEHOLDER = "%INSTITUTION_ID%";
  private static final String SERVICE_IDENTIFIER_PLACEHOLDER = "%SERVICE_ID%";
  private static final String VALID_ON_DATE_PLACEHOLDER = "%VALID_ON%";
  private static final String ARTICLE_CONDITION_VALUE_PLACEHOLDER = "%ARTICLE_CONDITION_VALUES%";

  private static final String PATH_SOAP_FETCH_REQUEST = "lmngqueries/lmngSoapFetchMessage.xml";
  private static final String PATH_FETCH_QUERY_LICENCES_FOR_IDP_SP = "lmngqueries/lmngQueryLicencesForIdentityProviderAndService.xml";
  private static final String PATH_FETCH_QUERY_ARTICLES_LICENCES_FOR_IDP_SP = "lmngqueries/lmngQueryArticlesWithOrWithoutLicencesForIdpAndSp.xml";
  private static final String PATH_FETCH_QUERY_ARTICLE_CONDITION = "lmngqueries/lmngArticleQueryConditionValue.xml";
  private static final String PATH_FETCH_QUERY_ARTICLE_DETAILS = "lmngqueries/lmngQueryGetServiceDetails.xml";

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  private boolean debug;
  private String endpoint;
  private KeyStore keyStore;
  private KeyStore trustStore;
  private String keystorePassword;
  private boolean activeMode;

  @Override
  @Cacheable("selfserviceDefault")
  public Article getArticleForIdentityProviderAndServiceProvider(IdentityProvider identityProvider, ServiceProvider serviceProvider,
      Date validOn) {
    invariant();
    List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
    serviceProviders.add(serviceProvider);
    List<Article> results = getArticleForIdentityProviderAndServiceProviders(identityProvider, serviceProviders, new Date());
    if (results.isEmpty()) {
      return null;
    } else if (results.size() > 1) {
      log.warn("Got more than one result but expected zero or one. Error mail will be sent. N/O results=" + results.size());
      // TODO add errormail
      return results.get(0);
    } else {
      return results.get(0);
    }
  }

  @Override
  @Cacheable("selfserviceDefault")
  public List<Article> getArticleForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders, Date validOn) {
    invariant();
    try {
      String lmngInstitutionId = getLmngIdentityId(identityProvider);
      List<String> serviceIds = getLmngServiceIds(serviceProviders);

      // validation, we need an institutionId and at least one serviceId
      if (StringUtils.isBlank(lmngInstitutionId) || CollectionUtils.isEmpty(serviceIds)) {
        return null;
      }

      // get the file with the soap request
      String soapRequest = getLmngSoapRequestForIdpAndSp(lmngInstitutionId, serviceIds, validOn);
      if (debug) {
        LmngUtil.writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      // call the webservice
      String webserviceResult = getWebServiceResult(soapRequest);
      // read/parse the XML response to License objects
      return LmngUtil.parseResult(webserviceResult, debug);
    } catch (Exception e) {
      log.error("Exception while retrieving article/license", e);
      // TODO error mail
    }
    return null;
  }

  @Override
  @Cacheable("selfserviceDefault")
  public Article getArticleForServiceProvider(ServiceProvider serviceProvider) {
    invariant();
    String serviceId = getLmngServiceId(serviceProvider);
    Article result = null;

    if (serviceId == null) {
      log.info("No binding found for given SP. Unable to find article.");
    } else {
      try {
        String soapRequest = getLmngSoapRequestForSp(serviceId);
        if (debug) {
          LmngUtil.writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
        }

        // call the webservice
        String webserviceResult = getWebServiceResult(soapRequest);
        // read/parse the XML response to License objects

        List<Article> results = LmngUtil.parseResult(webserviceResult, debug);
        if (results.isEmpty()) {
          return null;
        } else if (results.size() > 1) {
          log.warn("Got more than one result but expected zero or one. Error mail will be sent. N/O results=" + results.size());
          // TODO add errormail
          return results.get(0);
        } else {
          return results.get(0);
        }
      } catch (Exception e) {
        log.error("Exception while retrieving article", e);
        // TODO error mail, we only expect one
      }
    }
    return result;
  }

  /**
   * Get the response from the webservice call (using credentials and endpoint
   * address from this class settings) after execututing the given soapRequest
   * string.
   * 
   * @param soapRequest
   *          A string representation of the soap request
   * @return an inputstream of the webservice response
   * @throws ClientProtocolException
   * @throws IOException
   * @throws KeyStoreException
   * @throws NoSuchAlgorithmException
   * @throws UnrecoverableKeyException
   * @throws KeyManagementException
   */
  private String getWebServiceResult(final String soapRequest) throws ClientProtocolException, IOException, KeyManagementException,
      UnrecoverableKeyException, NoSuchAlgorithmException, KeyStoreException {
    log.debug("Calling the LMNG proxy webservice.");
    SchemeRegistry schemeRegistry = new SchemeRegistry();

    PlainSocketFactory sf = PlainSocketFactory.getSocketFactory();
    schemeRegistry.register(new Scheme("http", 80, sf));

    SSLSocketFactory lSchemeSocketFactory = new SSLSocketFactory(keyStore.getJavaSecurityKeyStore(), keystorePassword,
        trustStore.getJavaSecurityKeyStore());
    schemeRegistry.register(new Scheme("https", 443, lSchemeSocketFactory));

    DefaultHttpClient httpclient = new DefaultHttpClient(new BasicClientConnectionManager(schemeRegistry));

    httpclient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

    HttpPost httppost = new HttpPost(endpoint);
    httppost.setHeader("Content-Type", "application/soap+xml");
    httppost.setEntity(new StringEntity(soapRequest));

    HttpResponse httpresponse = httpclient.execute(httppost);
    HttpEntity output = httpresponse.getEntity();

    // Continue only if we have a successful response (code 200)
    int status = httpresponse.getStatusLine().getStatusCode();
    // Get String representation of response
    StringWriter writer = new StringWriter();
    IOUtils.copy(output.getContent(), writer);
    String response = writer.toString();

    if (debug) {
      LmngUtil.writeIO("lmngWsResponseStatus" + status, StringEscapeUtils.unescapeHtml(response));
    }
    log.warn("LMNG proxy webservice called. Http response:" + httpresponse);

    if (status != 200) {
      log.debug("LMNG webservice response content is:\n" + response);
      throw new RuntimeException("Invalid response from LMNG webservice. Http response " + httpresponse);
    }
    return response;
  }

  /**
   * Get the LMNG identifier for the given IDP
   * 
   * @param identityProvider
   * @return
   */
  private String getLmngIdentityId(IdentityProvider identityProvider) {
    // currently institutionId can be null, so check first
    if (identityProvider != null && identityProvider.getInstitutionId() != null) {
      return lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getInstitutionId());
    }
    return null;
  }

  /**
   * Get the LMNG identifier for the given SP
   * 
   * @param serviceProvider
   * @return
   */
  private String getLmngServiceId(ServiceProvider serviceProvider) {
    if (serviceProvider != null && serviceProvider.getId() != null) {
      return lmngIdentifierDao.getLmngIdForServiceProviderId(serviceProvider.getId());
    }
    return null;
  }

  /**
   * Get the LMNG identifiers for the given SP list
   * 
   * @param serviceProviders
   * @return
   */
  private List<String> getLmngServiceIds(List<ServiceProvider> serviceProviders) {
    List<String> result = new ArrayList<String>();
    for (ServiceProvider sp : serviceProviders) {
      String serviceId = getLmngServiceId(sp);
      if (serviceId != null) {
        result.add(serviceId);
      }
    }
    return result;
  }

  private String getLmngSoapRequestForIdpAndSp(String institutionId, List<String> serviceIds, Date validOn) throws IOException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_ARTICLES_LICENCES_FOR_IDP_SP);

    InputStream inputStream = queryResource.getInputStream();
    String query = IOUtils.toString(inputStream);
    if (institutionId != null) {
      query = query.replaceAll(INSTITUTION_IDENTIFIER_PLACEHOLDER, institutionId);
    }

    ClassPathResource articleConditionResource = new ClassPathResource(PATH_FETCH_QUERY_ARTICLE_CONDITION);
    InputStream articleInputStream = articleConditionResource.getInputStream();
    String articleConditionTemplate = IOUtils.toString(articleInputStream);
    String articleConditionValues = "";
    for (String serviceId : serviceIds) {
      articleConditionValues += articleConditionTemplate.replaceAll(SERVICE_IDENTIFIER_PLACEHOLDER, serviceId);
    }
    query = query.replaceAll(ARTICLE_CONDITION_VALUE_PLACEHOLDER, articleConditionValues);

    if (validOn != null) {
      query = query.replaceAll(VALID_ON_DATE_PLACEHOLDER, simpleDateFormat.format(validOn));
    }

    // html encode the string
    query = StringEscapeUtils.escapeHtml(query);

    // Insert the query in the envelope and add a UID in the envelope
    result = result.replaceAll(QUERY_PLACEHOLDER, query);
    result = result.replaceAll(ENDPOINT_PLACEHOLDER, endpoint);
    result = result.replaceAll(UID_PLACEHOLDER, UUID.randomUUID().toString());
    return result;
  }

  private String getLmngSoapRequestForSp(String serviceId) throws IOException {
    Assert.notNull(serviceId);
    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_ARTICLE_DETAILS);

    InputStream inputStream = queryResource.getInputStream();
    String query = IOUtils.toString(inputStream);
    query = query.replaceAll(SERVICE_IDENTIFIER_PLACEHOLDER, serviceId);

    // html encode the string
    query = StringEscapeUtils.escapeHtml(query);

    // Insert the query in the envelope and add a UID in the envelope
    result = result.replaceAll(QUERY_PLACEHOLDER, query);
    result = result.replaceAll(ENDPOINT_PLACEHOLDER, endpoint);
    result = result.replaceAll(UID_PLACEHOLDER, UUID.randomUUID().toString());
    return result;
  }

  private String getLmngRequestEnvelope() throws IOException {
    ClassPathResource envelopeResource = new ClassPathResource(PATH_SOAP_FETCH_REQUEST);
    InputStream inputStream = envelopeResource.getInputStream();
    return IOUtils.toString(inputStream);
  }

  private void invariant() {
    if (!activeMode) {
      throw new RuntimeException(this.getClass().getSimpleName() + " is not active. No calls may be made.");
    }
  }

  // GETTERS AND SETTERS BELOW

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void setKeyStore(KeyStore keyStore) {
    this.keyStore = keyStore;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

  public void setTrustStore(KeyStore trustStore) {
    this.trustStore = trustStore;
  }

  public void setLmngIdentifierDao(LmngIdentifierDao lmngIdentifierDao) {
    this.lmngIdentifierDao = lmngIdentifierDao;
  }

  public void setActiveMode(boolean activeMode) {
    this.activeMode = activeMode;
  }

  public boolean isActiveMode() {
    return activeMode;
  }

}
