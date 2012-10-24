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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.impl.ssl.KeyStore;

import org.apache.commons.io.FileUtils;
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
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);
  private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

  private static final String ENDPOINT_PLACEHOLDER = "%ENDPOINT%";
  private static final String UID_PLACEHOLDER = "%UID%";
  private static final String QUERY_PLACEHOLDER = "%FETCH_QUERY%";
  private static final String INSTITUTION_IDENTIFIER_PLACEHOLDER = "%INSTITUTION_ID%";
  private static final String SERVICE_IDENTIFIER_PLACEHOLDER = "%SERVICE_ID%";
  private static final String VALID_ON_DATE_PLACEHOLDER = "%VALID_ON%";
  private static final String ARTICLE_CONDITION_VALUE_PLACEHOLDER = "%ARTICLE_CONDITION_VALUES%";

  private static final String PATH_SOAP_FETCH_REQUEST = "lmngqueries/lmngSoapFetchMessage.xml";
  private static final String PATH_FETCH_QUERY_LICENCES_FOR_IDP_SP = "lmngqueries/lmngQueryLicencesForIdentityProviderAndService.xml";
  private static final String PATH_FETCH_QUERY_ARTICLE_CONDITION = "lmngqueries/lmngArticleQueryConditionValue.xml";
  private static final String PATH_FETCH_QUERY_ARTICLE_DETAILS = "lmngqueries/lmngQueryGetServiceDetails.xml";

  private static final String RESULT_ELEMENT = "GetDataResult";
  private static final String FETCH_RESULT_VALID_FROM = "license.lmng_validfrom";
  private static final String FETCH_RESULT_VALID_TO = "license.lmng_validto";
  private static final String FETCH_RESULT_LICENSE_NUMBER = "license.lmng_number";
  private static final String FETCH_RESULT_SUPPLIER_NAME = "supplier.name";
  private static final String FETCH_RESULT_ARTICLE_STATUS = "artikel.statuscode";
  private static final String FETCH_RESULT_DESCRIPTION_ENDUSER = "artikel.lmng_surfspotdescriptionlong";
  private static final String FETCH_RESULT_DESCRIPTION_INSTITUTION = "artikel.lmng_descriptionlong";
  private static final String FETCH_RESULT_DESCRIPTION_SERVICE = "artikel.lmng_description";
  private static final String FETCH_RESULT_DETAIL_LOGO = "image.lmng_url";
  private static final String FETCH_RESULT_SPECIAL_CONDITIONS = "image.lmng_url";
  private static final String FETCH_RESULT_LMNG_IDENTIFIER = "artikel.lmng_sdnarticleid";
  private static final String FETCH_RESULT_INSTITUTION_NAME = "name";
  private static final String FETCH_RESULT_PRODUCT_NAME = "product.lmng_name";

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
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider, ServiceProvider serviceProvider,
      Date validOn) {
    List<ServiceProvider> serviceProviders = new ArrayList<ServiceProvider>();
    serviceProviders.add(serviceProvider);
    return getLicenseArticlesForIdentityProviderAndServiceProviders(identityProvider, serviceProviders, new Date());
  }

  @Override
  @Cacheable("selfserviceDefault")
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders, Date validOn) {
    try {
      String lmngInstitutionId = getLmngIdentityId(identityProvider);
      List<String> serviceIds = getLmngServiceIds(serviceProviders);

      // validation, we need an institutionId and at least one serviceId
      if (lmngInstitutionId == null || serviceIds.size() == 0) {
        log.info("No valid parameters for LMNG information for identityProvider " + identityProvider + " and serviceProviders "
            + serviceProviders + " and date " + validOn + ". Possibly no binding found");
        return new ArrayList<Article>();
      }

      // get the file with the soap request
      String soapRequest = getLmngSoapRequestForIdpAndSp(lmngInstitutionId, serviceIds, validOn);
      if (debug) {
        writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
      }

      // call the webservice
      InputStream webserviceResult = getWebServiceResult(soapRequest);

      // read/parse the XML response to License objects
      return parseResult(webserviceResult);
    } catch (Exception e) {
      log.error("Exception while reading license", e);
      throw new RuntimeException("License retrieval exception", e);
    }
  }

  @Override
  @Cacheable("selfserviceDefault")
  public Article getArticleForServiceProvider(ServiceProvider serviceProvider) {
    String serviceId = getLmngServiceId(serviceProvider);
    Article result = null;

    if (serviceId == null) {
      log.info("No binding found for given SP. Unable to find article.");
    } else {
      try {
        String soapRequest = getLmngSoapRequestForSp(serviceId);
        if (debug) {
          writeIO("lmngRequest", StringEscapeUtils.unescapeHtml(soapRequest));
        }

        // call the webservice
        InputStream webserviceResult = getWebServiceResult(soapRequest);

        // read/parse the XML response to License objects
        List<Article> resulList = parseResult(webserviceResult);
        if (resulList != null && resulList.size()>0) {
          result = resulList.get(0);
        }
      } catch (Exception e) {
        log.error("Exception while reading license", e);
        throw new RuntimeException("License retrieval exception", e);
      }

      
    }

    return result;
  }
  
  /**
   * This method tries to parse the result into Article objects with possible licenses
   * 
   * @param webserviceResult
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ParseException
   */
  private List<Article> parseResult(InputStream webserviceResult) throws ParserConfigurationException, SAXException, IOException,
      ParseException {
    List<Article> resultList = new ArrayList<Article>();

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    try {
      Document doc = docBuilder.parse(webserviceResult);
      Element documentElement = doc.getDocumentElement();

      String fetchResultString = getFirstSubElementStringValue(documentElement, RESULT_ELEMENT);
      if (fetchResultString == null) {
        log.warn("Webservice response did not contain a 'GetDataResult' element. Empty response, please contact LMNG webservice admin");
        try {
          TransformerFactory tfactory = TransformerFactory.newInstance();
          Transformer xform = tfactory.newTransformer();
          Source src = new DOMSource(doc);
          StringWriter writer = new StringWriter();
          Result result = new StreamResult(writer);
          xform.transform(src, result);
          String responseText = writer.toString();
          writeIO("lmngFailedResponse", responseText);
          log.debug("Response:\n" + responseText);
        } catch (Exception e) {
          log.debug("Unable to read response");
        }
      } else {
        if (debug) {
          writeIO("lmngFetchResponse", StringEscapeUtils.unescapeHtml(fetchResultString));
        }
        InputSource fetchInputSource = new InputSource(new StringReader(fetchResultString));
        Document fetchResultDocument = docBuilder.parse(fetchInputSource);
        Element resultset = fetchResultDocument.getDocumentElement();

        if (resultset == null || !"resultset".equals(resultset.getNodeName())) {
          log.warn("Webservice 'GetDataResult' element did not contain a 'resultset' element");

        } else {
          NodeList results = resultset.getElementsByTagName("result");

          int numberOfResults = results.getLength();
          log.debug("Number of results in Fetch query:" + numberOfResults);
          for (int i = 0; i < numberOfResults; i++) {
            Node resultNode = results.item(i);
            if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
              Element resultElement = (Element) resultNode;

              resultList.add(createArticle(resultElement));
            }
          }
        }
      }
    } catch (SAXParseException se) {
      log.debug("Unable to parse response from LMNG webservice.", se);
      StringWriter writer = new StringWriter();
      IOUtils.copy(webserviceResult, writer);
      log.debug("LMNG webservice response is:\n" + writer.toString());
    }
    return resultList;
  }

  private Article createArticle(Element resultElement) {
    Article article = new Article();
    article.setArticleState(getFirstSubElementStringValue(resultElement, FETCH_RESULT_ARTICLE_STATUS));
    article.setDetailLogo(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DETAIL_LOGO));
    article.setEndUserDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_ENDUSER));
    article.setInstitutionDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_INSTITUTION));
    article.setInstitutionName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_INSTITUTION_NAME));
    article.setLmngIdentifier(getFirstSubElementStringValue(resultElement, FETCH_RESULT_LMNG_IDENTIFIER));
    article.setServiceDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_SERVICE));
    article.setSpecialConditions(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SPECIAL_CONDITIONS));
    article.setSupplierName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SUPPLIER_NAME));
    article.setProductName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_PRODUCT_NAME));
    
    String licenseNumber = getFirstSubElementStringValue(resultElement, FETCH_RESULT_LICENSE_NUMBER);
    if (licenseNumber != null) {
      License license = new License();
      license.setLicenseNumber(licenseNumber);
      Date startDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_FROM)));
      license.setStartDate(startDate);
      Date endDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_TO)));
      license.setEndDate(endDate);
      article.addLicense(license);
    }
    
    log.debug("Created new Article object:" + article.toString());
    return article;
  }

  /**
   * Get a child element with the given name and return the value of it as a
   * String This method will return the first (if available) item value,
   * possible multiple values will be ignored.
   * 
   * @param element
   *          The element to get the subelement from
   * 
   * @param string
   *          the string of the subelement
   * @return a string representation of the content of the subelement
   */
  private String getFirstSubElementStringValue(Element element, String subItemName) {
    String result = null;
    NodeList subItemListList = element.getElementsByTagName(subItemName);
    if (subItemListList != null && subItemListList.getLength() > 0) {
      Element subItemFirstElement = (Element) subItemListList.item(0);
      NodeList textFNList = subItemFirstElement.getChildNodes();
      if (textFNList != null) {
        if (((Node) textFNList.item(0)) != null && ((Node) textFNList.item(0)).getNodeValue() != null) {
          result = ((Node) textFNList.item(0)).getNodeValue().trim();
        }
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
  private InputStream getWebServiceResult(final String soapRequest) throws ClientProtocolException, IOException, KeyManagementException,
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

    log.debug("Done calling the LMNG proxy webservice. Response:" + httpresponse);
    return output.getContent();
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

    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_LICENCES_FOR_IDP_SP);

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

  /**
   * Write the given content to a file with the given filename (and add a
   * datetime prefix). For debugging purposes
   * 
   * @param filename
   * @param content
   */
  private void writeIO(String filename, String content) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssS");
    try {
      String fullFileName = System.getProperty("java.io.tmpdir") + filename + "_" + sdf.format(new Date()) + ".xml";
      FileUtils.writeStringToFile(new File(fullFileName), content);
      log.debug("wrote I/O file to " + fullFileName);
    } catch (IOException e) {
      log.debug("Failed to write input/output file. " + e.getMessage());
    }

  }

  public void setActiveMode(boolean activeMode) {
    this.activeMode = activeMode;
  }

  public boolean isActiveMode() {
    return activeMode;
  }
}
