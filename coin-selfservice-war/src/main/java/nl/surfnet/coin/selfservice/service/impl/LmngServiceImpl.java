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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.Dispatch;
import javax.xml.ws.Service;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.http.HTTPBinding;

import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.impl.ntlm.NTLMSchemeFactory;

import org.apache.commons.io.IOUtils;
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
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import sun.misc.BASE64Encoder;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String PATH_SOAP_REQUEST_GET_LICENSE_QUERY = "lmngqueries/licenseForIdentityProvider.xml";

  // TODO get these from properties/config file
  private String endpointUrl = "http://lmng01.dev.coin.surf.net/MSCrmServices/2007/CrmService.asmx";
  // private String endpointUrl =
  // "http://lmng01.dev.coin.surf.net:80/MSCRMServices/2007/CrmService.asmx";
  private String endPointUsername = "adm_surfconext";
  private String endPointPassword = "Jfe8unB83nhNFuwe";

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider) {
    return getLicensesForIdentityProvider(identityProvider, new Date());
  }

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider, Date validOn) {

    try {
      // get the file with the soap request
      SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
      String soapRequest = getLmngSoapRequest(getLmngIdentityId(identityProvider), simpleDateFormat.format(validOn));

      // call the webservice
//      InputStream webserviceResult = getWebServiceResult(soapRequest);
//      log.debug(IOUtils.toString(webserviceResult));
//      InputStream alternativeWebserviceResult = getAlternativeWebServiceResult(soapRequest);
      InputStream secondAlternativeWebserviceResult = getSecondAlternativeWebServiceResult(soapRequest);

      // read/parse the XML response to License objects
      // return parseResult(webserviceResult);
      // TODO temporary get result from file while webservice is not working
      return parseResult(getTempResult());
    } catch (Exception e) {
      log.error("Exception while reading license", e);
      throw new RuntimeException("License retrieval exception", e);
    }

  }

  /**
   * Temporary method that gets the content of a temporary test file containing
   * an expected response string from a webservice call. TODO remove this method
   * (if webservice is working)
   * 
   * @throws IOException
   */
  private InputStream getTempResult() throws IOException {
    ClassPathResource res = new ClassPathResource("lmngqueries/tempResponseExample.xml");
    return res.getInputStream();
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
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    Document doc = docBuilder.parse(webserviceResult);

    // TODO the result will be in an XML soap envelope and contains html
    // encoded data. Get this data and decode it first.

    // normalize text representation
    doc.getDocumentElement().normalize();
    log.debug("Root element of the doc is " + doc.getDocumentElement().getNodeName());

    NodeList listOfResults = doc.getElementsByTagName("result");
    int totalResults = listOfResults.getLength();
    log.debug("Total no of results : " + totalResults);

    for (int s = 0; s < totalResults; s++) {
      Node resultItem = listOfResults.item(s);
      if (resultItem.getNodeType() == Node.ELEMENT_NODE) {
        License license = new License();

        Element resultItemElement = (Element) resultItem;

        license.setContactEmail(getSubElementStringValue(resultItemElement, "contact.emailaddress1"));
        license.setContactFullName(getSubElementStringValue(resultItemElement, "contact.fullname"));
        license.setDescription(getSubElementStringValue(resultItemElement, "product.lmng_description"));
        Date startDate = simpleDateFormat.parse(getSubElementStringValue(resultItemElement, "license.lmng_validfrom").substring(0, 10));
        license.setStartDate(startDate);
        Date endDate = simpleDateFormat.parse(getSubElementStringValue(resultItemElement, "license.lmng_validto").substring(0, 10));
        license.setEndDate(endDate);
        license.setIdentityName(getSubElementStringValue(resultItemElement, "name"));
        license.setProductName(getSubElementStringValue(resultItemElement, "product.lmng_name"));
        license.setSupplierName(getSubElementStringValue(resultItemElement, "supplier.name"));

        resultList.add(license);
      }
    }

    return resultList;
  }

  /**
   * Get a child element with the given name and return the value of it as a
   * String This method will return the first (if available) item value,
   * possible multiple values will be ignored.
   * 
   * @param resultItemElement
   * 
   * @param string
   * @return
   */
  private String getSubElementStringValue(Element resultItemElement, String subItemName) {

    NodeList subItemListList = resultItemElement.getElementsByTagName(subItemName);
    Element subItemFirstElement = (Element) subItemListList.item(0);
    String result = null;
    NodeList textFNList = subItemFirstElement.getChildNodes();
    if (textFNList != null) {
      result = ((Node) textFNList.item(0)).getNodeValue().trim();
    }
    log.debug("Subitem with name: " + subItemName + " has value: " + result);
    return result;
  }

  /**
   * Get a String representation of the soap response of the LMNG webservice
   * using the given soap request.
   * 
   * @param soapRequest
   * @return the result string
   */
  private InputStream getWebServiceResult(String soapRequest) {

    QName qname = new QName("");
    Service service = Service.create(qname);
    service.addPort(qname, HTTPBinding.HTTP_BINDING, endpointUrl);
    Dispatch<Source> dispatcher = service.createDispatch(qname, Source.class, Service.Mode.PAYLOAD);

    Map<String, Object> requestContext = dispatcher.getRequestContext();
    requestContext.put(MessageContext.HTTP_REQUEST_METHOD, "POST");
    requestContext.put(BindingProvider.USERNAME_PROPERTY, endPointUsername);
    requestContext.put(BindingProvider.PASSWORD_PROPERTY, endPointPassword);

    // next line gives: 2012-09-13 10:59:42.582 java[4931:1903] Unable to load
    // realm info from SCDynamicStore
    Source webserviceResult = dispatcher.invoke(new StreamSource(new StringReader(soapRequest)));

    if (webserviceResult instanceof StreamSource) {
      return ((StreamSource) webserviceResult).getInputStream();
    } else {
      log.debug("unknown webservice result");
    }
    return null;
  }

  /**
   * Alternative implementation to get a String representation of the soap
   * response of the LMNG webservice using the given soap request.
   * 
   * TODO remove either one of the methods when implementation is done
   * 
   * @param soapRequest
   * @return the result string
   * @throws IOException
   */
  private InputStream getAlternativeWebServiceResult(String soapRequest) throws IOException {

    URL url = new URL(endpointUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestMethod("POST");
    // TODO use apache commons base64
    BASE64Encoder enc = new BASE64Encoder();
    String userpassword = endPointUsername + ":" + endPointPassword;
    String encodedAuthorization = enc.encode(userpassword.getBytes());
    connection.setRequestProperty("Authorization", "Basic " + encodedAuthorization);

    connection.setRequestProperty("Content-Length", String.valueOf(soapRequest.length()));
    connection.setRequestProperty("Content-Type", "text/xml");
    connection.setRequestProperty("Connection", "Close");
    connection.setRequestProperty("SoapAction", "");
    connection.setDoOutput(true);
    PrintWriter pw = new PrintWriter(connection.getOutputStream());
    pw.write(soapRequest);
    pw.flush();
    connection.connect();
    String response = connection.getResponseMessage();
    return new ByteArrayInputStream(response.getBytes("UTF-8"));
  }

  private InputStream getSecondAlternativeWebServiceResult(String soapRequest) throws ClientProtocolException, IOException {
    // see http://hc.apache.org/httpcomponents-client-ga/ntlm.html
    // "http://lmng01.dev.coin.surf.net/MSCrmServices/2007/CrmService.asmx"

    DefaultHttpClient httpclient = new DefaultHttpClient();
    httpclient.getAuthSchemes().register("NTLM", new NTLMSchemeFactory());
    httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY, new NTCredentials(endPointUsername, endPointPassword, "", ""));
    httpclient.getParams().setParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, Boolean.FALSE);
    httpclient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
    httpclient.getParams().setParameter(CoreProtocolPNames.HTTP_CONTENT_CHARSET, "UTF-8");
    
    HttpPost httppost = new HttpPost(endpointUrl);
    httppost.setEntity(new StringEntity(soapRequest));

    httppost.setHeader(HTTP.TARGET_HOST, "host");
    //httppost.setHeader("Content-type", "application/soap+xml");
    httppost.setHeader("Content-type", "text/xml");
    
    HttpResponse httpresponse = httpclient.execute(new HttpHost("lmng01.dev.coin.surf.net"), httppost, new BasicHttpContext());
    HttpEntity output = httpresponse.getEntity();

    if (output != null) {
      log.debug("Result of second alternative webservice call:\n" + (EntityUtils.toString(output)));
    }
    return null; // TODO get or create an inputstream
  }

  /**
   * Get the LMNG identifier for the given IDP
   * 
   * @param identityProvider
   * @return
   */
  private String getLmngIdentityId(IdentityProvider identityProvider) {
    // TODO get correct IDP-id for LMNG and dateFormatter
    return identityProvider.getInstitutionId();
  }

  /**
   * Get a String representation of the soap request for the given institution
   * id and the given date
   * 
   * @param institutionId
   * @param validOn
   * @return
   * @throws IOException
   */
  private String getLmngSoapRequest(String institutionId, String validOn) throws IOException {
    ClassPathResource res = new ClassPathResource(PATH_SOAP_REQUEST_GET_LICENSE_QUERY);
    String result = null;
    InputStream inputStream = res.getInputStream();
    result = IOUtils.toString(inputStream);
    result.replaceAll("%INSTITUTION_ID%", institutionId);
    result.replaceAll("%VALID_ON%", validOn);
    return result;
  }

}
