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
import java.net.MalformedURLException;
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

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import sun.misc.BASE64Encoder;

/**
 * Implementation of a licensing service that get's it information from a
 * webservice interface on LMNG
 */
public class LmngServiceImpl implements LicensingService {

  private static final Logger log = LoggerFactory.getLogger(LmngServiceImpl.class);

  private static final String PATH_SOAP_REQUEST_GET_LICENSE_QUERY = "lmngqueries/licenseForIdentityProvider.xml";

  // TODO get these from properties/config file
  private String endpointUrl = "http://localhost:8098/MSCrmServices/2007/CrmService.asmx";
  // private String endpointUrl = "http://lmng01.dev.coin.surf.net:80/MSCRMServices/2007/CrmService.asmx";
  private String endPointUsername = "surf-o\\adm_surfconext";
  private String endPointPassword = "Jfe8unB83nhNFuwe";

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider) {
    return getLicensesForIdentityProvider(identityProvider, new Date());
  }

  @Override
  public List<License> getLicensesForIdentityProvider(IdentityProvider identityProvider, Date validOn) {

    // get the file with the soap request
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
    String soapRequest = getLmngSoapRequest(getLmngIdentityId(identityProvider), simpleDateFormat.format(validOn));

    // call the webservice
    InputStream webserviceResult = getWebServiceResult(soapRequest);
    InputStream alternativeWebserviceResult = getAlternativeWebServiceResult(soapRequest);
    
    // read/parse the XML response to License objects
    // return parseResult(webserviceResult);
    // TODO temporary get result from file while webservice is not working
    return parseResult(getTempResult()); 
    
  }

  /**
   * Temporary method that gets the content of a temporary test file containing
   * an expected response string from a webservice call. TODO remove this method
   * (if webservice is working)
   */
  private InputStream getTempResult() {
    ClassPathResource res = new ClassPathResource("lmngqueries/tempResponseExample.xml");
    try {
      return res.getInputStream();
    } catch (IOException e) {
      log.error("Exception caught while reading temp resultfile");
    }
    return null;
  }

  /**
   * This method tries to parse the result into License objects using XStream
   * and the expected result format.
   * 
   * @param webserviceResult
   */
  private List<License> parseResult(InputStream webserviceResult) {
    List<License> resultList = new ArrayList<License>();
    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    try {

      DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
      DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
      Document doc = docBuilder.parse(webserviceResult);

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
          Date startDate = simpleDateFormat.parse(getSubElementStringValue(resultItemElement, "license.lmng_validfrom").substring(0,10));
          license.setStartDate(startDate);
          Date endDate = simpleDateFormat.parse(getSubElementStringValue(resultItemElement, "license.lmng_validto").substring(0,10));
          license.setEndDate(endDate);
          license.setIdentityName(getSubElementStringValue(resultItemElement, "name"));
          license.setProductName(getSubElementStringValue(resultItemElement, "product.lmng_name"));
          license.setSupplierName(getSubElementStringValue(resultItemElement, "supplier.name"));
          
          resultList.add(license);
        }
      }

    } catch (SAXParseException err) {
      log.error("Exception while parsing result from webservice.", err);
    } catch (SAXException e) {
      log.error("Exception while parsing result from webservice.", e);
    } catch (IOException e) {
      log.error("Exception while parsing result from webservice.", e);
    } catch (ParserConfigurationException e) {
      log.error("Exception while parsing result from webservice.", e);
    } catch (ParseException e) {
      log.error("Exception while parsing date in result from webservice.", e);
    }

    return resultList;
  }

  /**
   * Get a child element with the given name and return the value of it as a String
   * This method will return the first (if available) item value, possible multiple
   * values will be ignored.
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

    Source webserviceResult = dispatcher.invoke(new StreamSource(new StringReader(soapRequest)));

    if (webserviceResult instanceof StreamSource) {
      return ((StreamSource)webserviceResult).getInputStream();
    } else {
      log.debug("unknown webservice result");
    }
    return null;
  }

  /**
   * Alternative implementation to get a String representation of the soap response of the LMNG webservice
   * using the given soap request.
   * 
   * TODO remove either one of the methods when implementation is done
   * 
   * @param soapRequest
   * @return the result string
   */
  private InputStream getAlternativeWebServiceResult(String soapRequest) {

    try {
      URL url = new URL(endpointUrl);
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("POST");
      //TODO use apache commons base64
      BASE64Encoder enc = new BASE64Encoder();
      String userpassword = endPointUsername + ":" + endPointPassword;
      String encodedAuthorization = enc.encode( userpassword.getBytes() );
      connection.setRequestProperty("Authorization", "Basic "+ encodedAuthorization);
      
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

    } catch (MalformedURLException e) {
      log.error("Exception while creating wsdl url");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    return null;
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
   */
  private String getLmngSoapRequest(String institutionId, String validOn) {
    ClassPathResource res = new ClassPathResource(PATH_SOAP_REQUEST_GET_LICENSE_QUERY);
    String result = null;
    try {
      InputStream inputStream = res.getInputStream();
      result = IOUtils.toString(inputStream);
      result.replaceAll("%INSTITUTION_ID%", institutionId);
      result.replaceAll("%VALID_ON%", validOn);
    } catch (IOException e) {
      log.error("Exception caught while reading file with soap request for LMNG.");
    }
    return result;
  }

}
