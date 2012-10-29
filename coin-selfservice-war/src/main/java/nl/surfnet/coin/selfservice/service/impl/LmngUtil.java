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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.License;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class for LMNG. This class contains some static methods used in the
 * {@link LmngServiceImpl}
 * 
 */
public class LmngUtil {

  private static final Logger log = LoggerFactory.getLogger(LmngUtil.class);
  private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

  private static final String RESULT_ELEMENT = "GetDataResult";
  private static final String FETCH_RESULT_VALID_FROM = "license.lmng_validfrom";
  private static final String FETCH_RESULT_VALID_TO = "license.lmng_validto";
  private static final String FETCH_RESULT_LICENSE_NUMBER = "license.lmng_number";
  private static final String FETCH_RESULT_SUPPLIER_NAME = "supplier.name";
  private static final String FETCH_RESULT_ARTICLE_STATUS = "statuscode";// artikel.FIELDNAME
  private static final String FETCH_RESULT_DESCRIPTION_ENDUSER = "lmng_surfspotdescriptionlong";// artikel.FIELDNAME
  private static final String FETCH_RESULT_DESCRIPTION_INSTITUTION = "lmng_descriptionlong";// artikel.FIELDNAME
  private static final String FETCH_RESULT_DESCRIPTION_SERVICE = "lmng_description";// artikel.FIELDNAME
  private static final String FETCH_RESULT_DETAIL_LOGO = "image.lmng_url";
  private static final String FETCH_RESULT_SPECIAL_CONDITIONS = "image.lmng_url";
  private static final String FETCH_RESULT_LMNG_IDENTIFIER = "lmng_sdnarticleid";// artikel.FIELDNAME
  private static final String FETCH_RESULT_INSTITUTION_NAME = "name";
  private static final String FETCH_RESULT_PRODUCT_NAME = "product.lmng_name";
  private static final String FETCH_RESULT_LICENSEMODEL = "productvariation.lmng_licensemodel";

  private static final String GROUP_LICENSEMODEL = "3";
  
  
  /**
   * This method tries to parse the result into Article objects with possible
   * licenses
   * 
   * @param webserviceResult
   * @throws ParserConfigurationException
   * @throws IOException
   * @throws SAXException
   * @throws ParseException
   */
  public static List<Article> parseResult(String webserviceResult, boolean writeResponseToFile) throws ParserConfigurationException,
      SAXException, IOException, ParseException {
    List<Article> resultList = new ArrayList<Article>();

    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    InputStream inputStream = IOUtils.toInputStream(webserviceResult);
    Document doc = docBuilder.parse(inputStream);
    Element documentElement = doc.getDocumentElement();

    String fetchResultString = getFirstSubElementStringValue(documentElement, RESULT_ELEMENT);
    if (fetchResultString == null) {
      log.warn("Webservice response did not contain a 'GetDataResult' element. WebserviceResult:\n" + webserviceResult);
    } else {
      if (writeResponseToFile) {
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
    return resultList;
  }

  private static Article createArticle(Element resultElement) {
    Article article = new Article();
    article.setArticleState(getFirstSubElementStringValue(resultElement, FETCH_RESULT_ARTICLE_STATUS));
    article.setDetailLogo(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DETAIL_LOGO));
    article.setEndUserDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_ENDUSER));
    article.setInstitutionDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_INSTITUTION));
    article.setLmngIdentifier(getFirstSubElementStringValue(resultElement, FETCH_RESULT_LMNG_IDENTIFIER));
    article.setServiceDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_SERVICE));
    article.setSpecialConditions(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SPECIAL_CONDITIONS));
    article.setSupplierName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SUPPLIER_NAME));
    article.setProductName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_PRODUCT_NAME));

    String licenseNumber = getFirstSubElementStringValue(resultElement, FETCH_RESULT_LICENSE_NUMBER);
    if (licenseNumber != null) {
      License license = new License();
      license.setLicenseNumber(licenseNumber);
      license.setInstitutionName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_INSTITUTION_NAME));
      Date startDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_FROM)));
      license.setStartDate(startDate);
      Date endDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_TO)));
      license.setEndDate(endDate);
      String licenseModel = getFirstSubElementStringValue(resultElement, FETCH_RESULT_LICENSEMODEL);
      if (licenseModel != null && GROUP_LICENSEMODEL.equals(licenseModel)) {
        license.setGroupLicense(true);
      }
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
  private static String getFirstSubElementStringValue(Element element, String subItemName) {
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
   * Write the given content to a file with the given filename (and add a
   * datetime prefix). For debugging purposes
   * 
   * @param filename
   * @param content
   */
  public static void writeIO(String filename, String content) {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd-HHmmssS");
    try {
      String fullFileName = System.getProperty("java.io.tmpdir") + filename + "_" + sdf.format(new Date()) + ".xml";
      FileUtils.writeStringToFile(new File(fullFileName), content);
      log.debug("wrote I/O file to " + fullFileName);
    } catch (IOException e) {
      log.debug("Failed to write input/output file. " + e.getMessage());
    }

  }

}
