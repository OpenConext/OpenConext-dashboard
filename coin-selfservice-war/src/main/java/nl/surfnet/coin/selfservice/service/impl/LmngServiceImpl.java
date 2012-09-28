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
import java.io.StringReader;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.impl.ntlm.NTLMSchemeFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.NTCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String QUERY_PLACEHOLDER = "%FETCH_QUERY%";
  private static final String INSTITUTION_IDENTIFIER_PLACEHOLDER = "%INSTITUTION_ID%";
  private static final String VALID_ON_DATE_PLACEHOLDER = "%VALID_ON%";
  private static final String PATH_SOAP_FETCH_REQUEST = "lmngqueries/lmngSoapFetchMessage.xml";
  private static final String PATH_FETCH_QUERY_LICENCES_FOR_IDP = "lmngqueries/lmngQueryLicencesForIdentityProvider.xml";
  private static final String PATH_FETCH_QUERY_LICENCES_FOR_IDP_SP = "lmngqueries/lmngQueryLicencesForIdentityProviderAndService.xml";

  @Autowired
  private LmngIdentifierDao lmngIdentifierDao;

  private String endpoint;
  private String user;
  private String password;
  private Integer port = 80; // set default port to 80

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider) {
    return getLicensesForIdentityProvider(identityProvider, new Date());
  }

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider, Date validOn) {
    try {
      // get the file with the soap request
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String soapRequest = getLicenceForIdpRequest(getLmngIdentityId(identityProvider), simpleDateFormat.format(validOn));

      // call the webservice
      InputStream webserviceResult = getWebServiceResult(soapRequest);

      // read/parse the XML response to License objects
      return parseResult(webserviceResult);
    } catch (Exception e) {
      log.error("Exception while reading license", e);
      throw new RuntimeException("License retrieval exception", e);
    }

  }

  /**
   * This method tries to parse the result into License objects using XStream
   * and the expected result format.
   * 
   * @param webserviceResult
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ParseException
   */
  private List<License> parseResult(InputStream webserviceResult) throws ParserConfigurationException, SAXException, IOException,
      ParseException {
    List<License> resultList = new ArrayList<License>();
    DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(webserviceResult);
    Element documentElement = doc.getDocumentElement();

    String fetchResultString = getFirstSubElementStringValue(documentElement, "FetchResult");
    if (fetchResultString == null) {
      log.warn("Webservice response did not contain a 'FetchResult' element");
    } else {
      InputSource fetchInputSource = new InputSource(new StringReader(fetchResultString));
      Document fetchResultDocument = docBuilder.parse(fetchInputSource);
      Element resultset = fetchResultDocument.getDocumentElement();

      if (resultset == null || !"resultset".equals(resultset.getNodeName())) {
        log.warn("Webservice 'FetchResult' element did not contain a 'resultset' element");

      } else {
        NodeList results = resultset.getElementsByTagName("result");

        int numberOfResults = results.getLength();
        log.debug("Number of results in Fetch query:" + numberOfResults);
        for (int i = 0; i < numberOfResults; i++) {
          Node resultNode = results.item(i);
          if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
            License license = new License();
            Element resultElement = (Element) resultNode;

            license.setContactEmail(getFirstSubElementStringValue(resultElement, "contact.emailaddress1"));
            license.setContactFullName(getFirstSubElementStringValue(resultElement, "contact.fullname"));
            license.setDescription(getFirstSubElementStringValue(resultElement, "product.lmng_description"));
            Date startDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, "license.lmng_validfrom")));
            license.setStartDate(startDate);
            Date endDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, "license.lmng_validto")));
            license.setEndDate(endDate);
            license.setIdentityName(getFirstSubElementStringValue(resultElement, "name"));
            license.setProductName(getFirstSubElementStringValue(resultElement, "product.lmng_name"));
            license.setSupplierName(getFirstSubElementStringValue(resultElement, "supplier.name"));
            log.debug("Created new License object:" + license.toString());
            resultList.add(license);
          }
        }
      }
    }

    return resultList;
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
        result = ((Node) textFNList.item(0)).getNodeValue().trim();
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
   */
  private InputStream getWebServiceResult(String soapRequest) throws ClientProtocolException, IOException {
    DefaultHttpClient httpclient = new DefaultHttpClient();
    httpclient.getAuthSchemes().register("NTLM", new NTLMSchemeFactory());
    httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new NTCredentials(user, password, "", ""));
    httpclient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");

    HttpPost httppost = new HttpPost(endpoint);
    httppost.setEntity(new StringEntity(soapRequest));

    httppost.setHeader(HTTP.TARGET_HOST, "host");
    httppost.setHeader("Content-type", "text/xml");

    HttpResponse httpresponse = httpclient.execute(new HttpHost(new URL(endpoint).getHost(), port), httppost, new BasicHttpContext());
    HttpEntity output = httpresponse.getEntity();

    return output.getContent();
  }

  /**
   * Get the LMNG identifier for the given IDP
   * 
   * @param identityProvider
   * @return
   */
  private String getLmngIdentityId(IdentityProvider identityProvider) {
    // TODO check if we need Id or institutionId
    return lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getId());
  }

  /**
   * Get a String representation of the soap request for the query that gives
   * all licences for the given institutionid and the given date
   * 
   * @param institutionId
   * @param validOn
   * @return
   * @throws IOException
   */
  private String getLicenceForIdpRequest(String institutionId, String validOn) throws IOException {
    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    // Get the query String and replace the placeholders
    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_LICENCES_FOR_IDP);
    InputStream inputStream = queryResource.getInputStream();
    String query = IOUtils.toString(inputStream);
    if (institutionId != null) {
      query = query.replaceAll(INSTITUTION_IDENTIFIER_PLACEHOLDER, institutionId);
    }
    if (validOn != null) {
      query = query.replaceAll(VALID_ON_DATE_PLACEHOLDER, validOn);
    }

    // html encode the string
    query = StringEscapeUtils.escapeHtml(query);

    // Insert the query in the envelope
    result = result.replaceAll(QUERY_PLACEHOLDER, query);
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

  public void setUser(String user) {
    this.user = user;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setPort(Integer port) {
    this.port = port;
  }

  public void setLmngIdentifierDao(LmngIdentifierDao lmngIdentifierDao) {
    this.lmngIdentifierDao = lmngIdentifierDao;
  }

}
