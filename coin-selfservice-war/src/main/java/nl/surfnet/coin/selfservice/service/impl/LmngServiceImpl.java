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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.service.LmngService;
import nl.surfnet.coin.selfservice.service.impl.ssl.KeyStore;
import nl.surfnet.coin.shared.domain.ErrorMail;
import nl.surfnet.coin.shared.service.ErrorMessageMailer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
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
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LmngService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String ENDPOINT_PLACEHOLDER = "%ENDPOINT%";
  private static final String UID_PLACEHOLDER = "%UID%";
  private static final String QUERY_PLACEHOLDER = "%FETCH_QUERY%";
  private static final String INSTITUTION_IDENTIFIER_PLACEHOLDER = "%INSTITUTION_ID%";
  private static final String SERVICE_IDENTIFIER_PLACEHOLDER = "%SERVICE_ID%";
  private static final String VALID_ON_DATE_PLACEHOLDER = "%VALID_ON%";
  private static final String ARTICLE_CONDITION_VALUE_PLACEHOLDER = "%ARTICLE_CONDITION_VALUES%";
  private static final String ARTICLE_INSTITUTION_CONDITION_PLACEHOLDER = "%INSTITUTION_CONDITION%";

  private static final String PATH_SOAP_FETCH_REQUEST = "lmngqueries/lmngSoapFetchMessage.xml";
  private static final String PATH_FETCH_QUERY_LICENSES_LICENCES_FOR_IDP_SP = "lmngqueries/lmngQueryLicensesForIdpsAndSps.xml";
  private static final String PATH_FETCH_QUERY_ARTICLES_FOR_SPS = "lmngqueries/lmngQueryArticlesForSps.xml";
  private static final String PATH_FETCH_QUERY_GET_INSTITUTION = "lmngqueries/lmngQueryGetInstitution.xml";
  private static final String PATH_FETCH_QUERYCONDITION_ARTICLE = "lmngqueries/lmngArticleQueryConditionValue.xml";
  private static final String PATH_FETCH_QUERYCONDITION_INSTITUTION = "lmngqueries/lmngArticleQueryInstitutionCondition.xml";

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  @Resource(name = "errorMessageMailer")
  private ErrorMessageMailer errorMessageMailer;

  private boolean debug;
  private String endpoint;
  private KeyStore keyStore;
  private KeyStore trustStore;
  private String keystorePassword;
  private boolean activeMode;

  @Override
  public List<License> getLicensesForIdpAndSp(IdentityProvider identityProvider, String articleIdentifier, Date validOn) {
    List<License> result = new ArrayList<License>();
    invariant();
    try {
      String lmngInstitutionId = IdentityProvider.NONE.equals(identityProvider) ? null : getLmngIdentityId(identityProvider);

      // validation, we need at least one serviceId
      if (articleIdentifier == null) {
        return result;
      }

      List<String> articleIdentifiers = new ArrayList<String>();
      articleIdentifiers.add(articleIdentifier);

      // get the file with the soap request
      String soapRequest = getLmngSoapRequestForIdpAndSp(lmngInstitutionId, articleIdentifiers, validOn);
      if (debug) {
        LmngUtil.writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      // call the webservice
      String webserviceResult = getWebServiceResult(soapRequest);
      // read/parse the XML response to License objects
      result = LmngUtil.parseLicensesResult(webserviceResult, debug);
    } catch (Exception e) {
      log.error("Exception while retrieving licenses", e);
      sendErrorMail(identityProvider, articleIdentifier, e.getMessage(), "getLicensesForIdpAndSp");
    }
    return result;
  }

  @Override
  public List<Article> getArticlesForServiceProviders(List<String> serviceProvidersEntityIds) {
    List<Article> result = new ArrayList<Article>();
    invariant();
    try {
      Map<String, String> serviceIds = getLmngServiceIds(serviceProvidersEntityIds);

      // validation, we need at least one serviceId
      if (CollectionUtils.isEmpty(serviceIds)) {
        return result;
      }

      // get the file with the soap request
      String soapRequest = getLmngSoapRequestForSps(serviceIds.keySet());
      if (debug) {
        LmngUtil.writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      // call the webservice
      String webserviceResult = getWebServiceResult(soapRequest);
      // read/parse the XML response to License objects
      List<Article> parsedArticles = LmngUtil.parseArticlesResult(webserviceResult, debug);

      for (Article article : parsedArticles) {
        article.setServiceProviderEntityId(serviceIds.get(article.getLmngIdentifier()));
        result.add(article);
      }
    } catch (Exception e) {
      log.error("Exception while retrieving articles", e);
      sendErrorMail(serviceProvidersEntityIds, e.getMessage(), "getArticlesForServiceProviders");
    }
    return result;
  }

  @Override
  public String getServiceName(String guid) {
    invariant();
    String result = null;
    try {
      // get the file with the soap request
      String soapRequest = getLmngSoapRequestForSps(Arrays.asList(new String[] { guid }));
      if (debug) {
        LmngUtil.writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      // call the webservice
      String webserviceResult = getWebServiceResult(soapRequest);
      // read/parse the XML response to License objects
      List<Article> resultList = LmngUtil.parseArticlesResult(webserviceResult, debug);
      if (resultList != null && resultList.size() > 0) {
        result = resultList.get(0).getProductName();
      }
    } catch (Exception e) {
      log.error("Exception while retrieving article/license", e);
      sendErrorMail(guid, e.getMessage(), "getServiceName");
    }
    return result;
  }

  @Override
  public String getInstitutionName(String guid) {
    invariant();
    String result = null;
    try {
      ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_GET_INSTITUTION);

      // Get the soap/fetch envelope
      String soapRequest = getLmngRequestEnvelope();

      InputStream inputStream;
      inputStream = queryResource.getInputStream();
      String query = IOUtils.toString(inputStream);
      query = query.replaceAll(INSTITUTION_IDENTIFIER_PLACEHOLDER, guid);

      // html encode the string
      query = StringEscapeUtils.escapeHtml(query);

      // Insert the query in the envelope and add a UID in the envelope
      soapRequest = soapRequest.replaceAll(QUERY_PLACEHOLDER, query);
      soapRequest = soapRequest.replaceAll(ENDPOINT_PLACEHOLDER, endpoint);
      soapRequest = soapRequest.replaceAll(UID_PLACEHOLDER, UUID.randomUUID().toString());

      if (debug) {
        LmngUtil.writeIO("lmngRequestInstitution", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      String webserviceResult = getWebServiceResult(soapRequest);
      result = LmngUtil.parseResultInstitute(webserviceResult, debug);
    } catch (Exception e) {
      log.error("Exception while retrieving article/license", e);
      sendErrorMail(guid, e.getMessage(), "getInstitutionName");
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

    Date beforeCall = new Date();
    HttpResponse httpresponse = httpclient.execute(httppost);
    Date afterCall = new Date();

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

    long durationSeconds = (afterCall.getTime() - beforeCall.getTime()) / 1000;
    log.warn("LMNG proxy webservice called in " + durationSeconds + " seconds. Http response:" + httpresponse);

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
  private String getLmngServiceId(String serviceProviderEntityId) {
    if (serviceProviderEntityId != null) {
      return lmngIdentifierDao.getLmngIdForServiceProviderId(serviceProviderEntityId);
    }
    return null;
  }

  /**
   * Get the LMNG identifiers for the given SP list
   * 
   * @param serviceProviders
   * @return a map with the LMNGID as key and serviceprovider entity ID as value
   */
  private Map<String, String> getLmngServiceIds(List<String> serviceProvidersEntityIds) {
    Map<String, String> result = new HashMap<String, String>();
    for (String spId : serviceProvidersEntityIds) {
      String serviceId = getLmngServiceId(spId);
      if (serviceId != null) {
        result.put(serviceId, spId);
      }
    }
    return result;
  }

  private String getLmngSoapRequestForIdpAndSp(String institutionId, List<String> serviceIds, Date validOn) throws IOException {
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    Assert.notNull(validOn);
    Assert.notNull(serviceIds);

    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_LICENSES_LICENCES_FOR_IDP_SP);

    InputStream inputStream = queryResource.getInputStream();
    String query = IOUtils.toString(inputStream);

    ClassPathResource articleConditionResource = new ClassPathResource(PATH_FETCH_QUERYCONDITION_ARTICLE);
    InputStream articleInputStream = articleConditionResource.getInputStream();
    String articleConditionTemplate = IOUtils.toString(articleInputStream);
    String articleConditionValues = "";
    for (String serviceId : serviceIds) {
      articleConditionValues += articleConditionTemplate.replaceAll(SERVICE_IDENTIFIER_PLACEHOLDER, serviceId);
    }

    String institutionConditionTemplate = "";
    if (institutionId != null) {
      ClassPathResource institutionConditionResource = new ClassPathResource(PATH_FETCH_QUERYCONDITION_INSTITUTION);
      InputStream institutionConditionInputStream = institutionConditionResource.getInputStream();
      institutionConditionTemplate = IOUtils.toString(institutionConditionInputStream);
      institutionConditionTemplate = institutionConditionTemplate.replaceAll(INSTITUTION_IDENTIFIER_PLACEHOLDER, institutionId);
    }

    // replace base query with placeholders
    query = query.replaceAll(ARTICLE_CONDITION_VALUE_PLACEHOLDER, articleConditionValues);
    query = query.replaceAll(ARTICLE_INSTITUTION_CONDITION_PLACEHOLDER, institutionConditionTemplate);
    query = query.replaceAll(VALID_ON_DATE_PLACEHOLDER, simpleDateFormat.format(validOn));

    // html encode the string
    query = StringEscapeUtils.escapeHtml(query);

    // Insert the query in the envelope and add a UID in the envelope
    result = result.replaceAll(QUERY_PLACEHOLDER, query);
    result = result.replaceAll(ENDPOINT_PLACEHOLDER, endpoint);
    result = result.replaceAll(UID_PLACEHOLDER, UUID.randomUUID().toString());
    return result;
  }

  private String getLmngSoapRequestForSps(Collection<String> serviceIds) throws IOException {
    Assert.notNull(serviceIds);

    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    // TODO change path
    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_ARTICLES_FOR_SPS);

    InputStream inputStream = queryResource.getInputStream();
    String query = IOUtils.toString(inputStream);

    ClassPathResource articleConditionResource = new ClassPathResource(PATH_FETCH_QUERYCONDITION_ARTICLE);
    InputStream articleInputStream = articleConditionResource.getInputStream();
    String articleConditionTemplate = IOUtils.toString(articleInputStream);
    String articleConditionValues = "";
    for (String serviceId : serviceIds) {
      articleConditionValues += articleConditionTemplate.replaceAll(SERVICE_IDENTIFIER_PLACEHOLDER, serviceId);
    }

    // replace base query with placeholders
    query = query.replaceAll(ARTICLE_CONDITION_VALUE_PLACEHOLDER, articleConditionValues);

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

  /*
   * Send a mail
   */
  private void sendErrorMail(IdentityProvider idp, String articleIdentifier, String message, String method) {
    String shortMessage = "Exception while retrieving article/license";

    String idpEntityId = idp == null ? "NULL" : idp.getId();
    String institutionId = idp == null ? "NULL" : idp.getInstitutionId();

    String formattedMessage = String.format(
        "LMNG call for Identity Provider '%s' with institution ID '%s' and Article with Id's '%s' failed with the following message: '%s'",
        idpEntityId, institutionId, articleIdentifier, message);
    ErrorMail errorMail = new ErrorMail(shortMessage, formattedMessage, formattedMessage, getHost(), "LMNG");
    errorMail.setLocation(this.getClass().getName() + "#get" + method);
    errorMessageMailer.sendErrorMail(errorMail);
  }

  /*
   * Send a mail
   */
  private void sendErrorMail(List<String> spIds, String message, String method) {
    String shortMessage = "Exception while retrieving article/license";
    String spEntityIds = "";
    if (spIds != null) {
      for (String spId : spIds) {
        spEntityIds += spId + ", ";
      }
    }

    String formattedMessage = String.format("LMNG call for Service Providers with id's '%s' failed with the following message: '%s'",
        spEntityIds, message);
    ErrorMail errorMail = new ErrorMail(shortMessage, formattedMessage, formattedMessage, getHost(), "LMNG");
    errorMail.setLocation(this.getClass().getName() + "#get" + method);
    errorMessageMailer.sendErrorMail(errorMail);
  }

  /*
   * Send a mail
   */
  private void sendErrorMail(String guid, String message, String method) {
    String shortMessage = "Exception while retrieving institute from LMNG";
    String formattedMessage = String.format("LMNG call for institute GUID '%s' failed with the following message: '%s'", guid, message);
    ErrorMail errorMail = new ErrorMail(shortMessage, formattedMessage, formattedMessage, getHost(), "LMNG");
    errorMail.setLocation(this.getClass().getName() + "#get" + method);
    errorMessageMailer.sendErrorMail(errorMail);
  }

  private String getHost() {
    try {
      return InetAddress.getLocalHost().toString();
    } catch (UnknownHostException e) {
      return "UNKNOWN";
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
