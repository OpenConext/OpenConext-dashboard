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

package csa.service.impl;

import csa.domain.Account;
import csa.domain.Article;
import csa.domain.ArticleMedium;
import csa.domain.ArticleMedium.ArticleMediumType;
import csa.model.License;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.*;

import com.google.common.collect.ImmutableMap;

/**
 * Utility class for LMNG. This class contains some static methods used in the
 * {@link LmngServiceImpl}
 */
public class LmngUtil implements CrmUtil {

  private static final Logger log = LoggerFactory.getLogger(LmngUtil.class);
  private static final DateTimeFormatter dateTimeFormatter = ISODateTimeFormat.dateTimeNoMillis();

  public static final String ENDPOINT_PLACEHOLDER = "%ENDPOINT%";
  public static final String UID_PLACEHOLDER = "%UID%";
  public static final String QUERY_PLACEHOLDER = "%FETCH_QUERY%";
  public static final String INSTITUTION_IDENTIFIER_PLACEHOLDER = "%INSTITUTION_ID%";

  private static final String SERVICE_IDENTIFIER_PLACEHOLDER = "%SERVICE_ID%";
  private static final String VALID_ON_DATE_PLACEHOLDER = "%VALID_ON%";
  private static final String ARTICLE_CONDITION_VALUE_PLACEHOLDER = "%ARTICLE_CONDITION_VALUES%";
  private static final String ARTICLE_INSTITUTION_CONDITION_PLACEHOLDER = "%INSTITUTION_CONDITION%";

  private static final String PATH_SOAP_FETCH_REQUEST = "lmngqueries/lmngSoapFetchMessage.xml";
  private static final String PATH_FETCH_QUERY_ARTICLES_FOR_SPS = "lmngqueries/lmngQueryArticlesForSps.xml";
  private static final String PATH_FETCH_QUERYCONDITION_ARTICLE = "lmngqueries/lmngArticleQueryConditionValue.xml";
  private static final String PATH_FETCH_QUERYCONDITION_INSTITUTION = "lmngqueries/lmngArticleQueryInstitutionCondition.xml";
  private static final String PATH_FETCH_ALL_ACCOUNTS = "lmngqueries/lmngQueryAllAccounts.xml";

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
  private static final String FETCH_RESULT_SPECIAL_CONDITIONS = "lmng_specialconditions";
  private static final String FETCH_RESULT_LMNG_IDENTIFIER = "lmng_sdnarticleid";// artikel.FIELDNAME
  private static final String FETCH_RESULT_ARTICLE_NAME = "lmng_name";
  private static final String FETCH_RESULT_LICENSEMODEL = "productvariation.lmng_licensemodel";
  private static final String FETCH_RESULT_INSTITUTE_NAME = "name";
  private static final String FETCH_RESULT_PRODUCT_ID = "product.lmng_productid";
  private static final String FETCH_RESULT_PRODUCT_NAME = "product.lmng_name";

  private static final String FETCH_RESULT_MEDIUM_URL = "articlemedium.lmng_downloadurl";
  private static final String FETCH_RESULT_MEDIUM_NAME = "articlemedium.lmng_name";
  private static final String FETCH_RESULT_MEDIUM_SUPPLIER_ID = "articlemedium.lmng_supplierid";
  private static final String FETCH_RESULT_MEDIUM_GOOGLE_ID = "{5859F910-5E12-DF11-A633-0019B9DE3AA4}";
  private static final String FETCH_RESULT_MEDIUM_APPLE_ID = "{5FF1FAB3-2410-DC11-A6C7-0019B9DE3AA4}";

  private static final String GROUP_LICENSEMODEL = "3";

  private static final Map<LicenseRetrievalAttempt, String> LICENSE_RETRIEVAL_ATTEMPT_TO_TEMPLATE = ImmutableMap.of(
    LicenseRetrievalAttempt.One, "lmngqueries/lmngQueryLicensesForIdpsAndSps.xml",
    LicenseRetrievalAttempt.Two, "lmngqueries/lmngQueryLicensesForIdpsAndSpsRevision2.xml",
    LicenseRetrievalAttempt.Three, "lmngqueries/lmngQueryLicensesForIdpsAndSpsRevision3.xml"
    );

  /**
   * Parse the result to an article(list)
   */
  @Override
  public List<Article> parseArticlesResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException {
    List<Article> resultList = new ArrayList<>();

    NodeList nodes = parse(webserviceResult);

    if (nodes == null) {
      return resultList;
    }

    int numberOfResults = nodes.getLength();
    log.debug("Number of results in Fetch query:" + numberOfResults);
    for (int i = 0; i < numberOfResults; i++) {
      Node resultNode = nodes.item(i);
      if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        Element resultElement = (Element) resultNode;
        Article newArticle = createArticle(resultElement);
        // try to find if this article is already in the list (possible multiple results for different mediatypes) and enrich the medium information
        for (Article article : resultList) {
          if (article.getLmngIdentifier() != null && article.getLmngIdentifier().equals(newArticle.getLmngIdentifier())) {
            if (newArticle.getAndroidPlayStoreMedium() != null) {
              article.setAndroidPlayStoreMedium(newArticle.getAndroidPlayStoreMedium());
            } else if (newArticle.getAppleAppStoreMedium() != null) {
              article.setAppleAppStoreMedium(newArticle.getAppleAppStoreMedium());
            }
            newArticle = null;
          }
        }
        if (newArticle != null) {
          resultList.add(newArticle);
        }
      }
    }

    return resultList;
  }

  /**
   * Parse the result to a license(list)
   */
  @Override
  public List<License> parseLicensesResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException {
    List<License> resultList = new ArrayList<>();

    NodeList nodes = parse(webserviceResult);

    if (nodes == null) {
      return resultList;
    }

    int numberOfResults = nodes.getLength();
    log.debug("Number of results in Fetch query:" + numberOfResults);
    for (int i = 0; i < numberOfResults; i++) {
      Node resultNode = nodes.item(i);
      if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        Element resultElement = (Element) resultNode;
        License newLicense = createLicense(resultElement);
        if (newLicense != null) {
          resultList.add(newLicense);
        }
      }
    }

    return resultList;
  }

  /**
   * Parse the result to an account(list)
   */
  @Override
  public List<Account> parseAccountsResult(String webserviceResult) throws ParserConfigurationException, SAXException, IOException {
    List<Account> resultList = new ArrayList<>();

    NodeList nodes = parse(webserviceResult);

    if (nodes == null) {
      return resultList;
    }

    int numberOfResults = nodes.getLength();
    log.debug("Number of results in Fetch query:" + numberOfResults);
    for (int i = 0; i < numberOfResults; i++) {
      Node resultNode = nodes.item(i);
      if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        Element resultElement = (Element) resultNode;
        Account account = createAccount(resultElement);
        if (account != null) {
          resultList.add(account);
        }
      }
    }

    return resultList;
  }

  @Override
  public String parseResultInstitute(String webserviceResult) throws ParserConfigurationException, SAXException, IOException {
    NodeList nodes = parse(webserviceResult);

    if (nodes == null) {
      return null;
    }

    int numberOfResults = nodes.getLength();
    log.debug("Number of results in Fetch query:" + numberOfResults);
    for (int i = 0; i < numberOfResults; i++) {
      Node resultNode = nodes.item(i);
      if (resultNode.getNodeType() == Node.ELEMENT_NODE) {
        Element resultElement = (Element) resultNode;
        return getFirstSubElementStringValue(resultElement, FETCH_RESULT_INSTITUTE_NAME);
      }
    }

    return null;
  }

  private NodeList parse(String webserviceResult) throws ParserConfigurationException, SAXException, IOException {
    DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
    DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
    InputStream inputStream = IOUtils.toInputStream(webserviceResult);
    Document doc = docBuilder.parse(inputStream);
    Element documentElement = doc.getDocumentElement();

    String fetchResultString = getFirstSubElementStringValue(documentElement, RESULT_ELEMENT);
    if (fetchResultString == null) {
      log.warn("Webservice response did not contain a 'GetDataResult' element. WebserviceResult:\n" + webserviceResult);
    } else {
      InputSource fetchInputSource = new InputSource(new StringReader(fetchResultString));
      Document fetchResultDocument = docBuilder.parse(fetchInputSource);
      Element resultset = fetchResultDocument.getDocumentElement();

      if (resultset == null || !"resultset".equals(resultset.getNodeName())) {
        log.warn("Webservice 'GetDataResult' element did not contain a 'resultset' element");
      } else {
        return resultset.getElementsByTagName("result");
      }
    }
    return null;
  }

  private Article createArticle(Element resultElement) {
    Article article = new Article();
    article.setArticleState(getFirstSubElementStringValue(resultElement, FETCH_RESULT_ARTICLE_STATUS));
    article.setDetailLogo(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DETAIL_LOGO));
    article.setEndUserDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_ENDUSER));
    article.setInstitutionDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_INSTITUTION));
    article.setLmngIdentifier(getFirstSubElementStringValue(resultElement, FETCH_RESULT_LMNG_IDENTIFIER));
    article.setServiceDescriptionNl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_DESCRIPTION_SERVICE));
    article.setSpecialConditions(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SPECIAL_CONDITIONS));
    article.setSupplierName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_SUPPLIER_NAME));
    article.setArticleName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_ARTICLE_NAME));
    article.setProductName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_PRODUCT_NAME));
    article.setLmngProductIdentifier(getFirstSubElementStringValue(resultElement, FETCH_RESULT_PRODUCT_ID));

    String mediumSupplier = getFirstSubElementStringValue(resultElement, FETCH_RESULT_MEDIUM_SUPPLIER_ID);
    if (FETCH_RESULT_MEDIUM_GOOGLE_ID.equals(mediumSupplier)) {
      ArticleMedium articleMedium = new ArticleMedium();
      articleMedium.setType(ArticleMediumType.ANDROIDMARKET);
      articleMedium.setName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_MEDIUM_NAME));
      articleMedium.setUrl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_MEDIUM_URL));
      article.setAndroidPlayStoreMedium(articleMedium);
    } else if (FETCH_RESULT_MEDIUM_APPLE_ID.equals(mediumSupplier)) {
      ArticleMedium articleMedium = new ArticleMedium();
      articleMedium.setType(ArticleMediumType.APPLESTORE);
      articleMedium.setName(getFirstSubElementStringValue(resultElement, FETCH_RESULT_MEDIUM_NAME));
      articleMedium.setUrl(getFirstSubElementStringValue(resultElement, FETCH_RESULT_MEDIUM_URL));
      article.setAppleAppStoreMedium(articleMedium);
    }

    log.debug("Created new Article object: {}", article);

    return article;
  }

  private Account createAccount(Element element) {
    String name = getFirstSubElementStringValue(element, "name");
    String status = getFirstSubElementStringValue(element, "statuscode");
    String guid = getFirstSubElementStringValue(element, "accountid");

    return new Account(name, status, guid);
  }

  private License createLicense(Element resultElement) {
    String licenseNumber = getFirstSubElementStringValue(resultElement, FETCH_RESULT_LICENSE_NUMBER);
    if (licenseNumber == null) {
      log.debug("Element did not contain a {}. Unable to create license", FETCH_RESULT_LICENSE_NUMBER );
      return null;
    }

    License license = new License();
    license.setLicenseNumber(licenseNumber);
    Date startDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_FROM)));
    license.setStartDate(startDate);
    Date endDate = new Date(dateTimeFormatter.parseMillis(getFirstSubElementStringValue(resultElement, FETCH_RESULT_VALID_TO)));
    license.setEndDate(endDate);
    String licenseModel = getFirstSubElementStringValue(resultElement, FETCH_RESULT_LICENSEMODEL);
    if (licenseModel != null && GROUP_LICENSEMODEL.equals(licenseModel)) {
      license.setGroupLicense(true);
    }
    log.debug("Created new License object: {}", license);
    return license;
  }

  /**
   * Get a child element with the given name and return the value of it as a
   * String This method will return the first (if available) item value,
   * possible multiple values will be ignored.
   *
   * @param element     The element to get the subelement from
   * @param subItemName the string of the subelement
   * @return a string representation of the content of the subelement
   */
  private String getFirstSubElementStringValue(Element element, String subItemName) {
    NodeList subItemListList = element.getElementsByTagName(subItemName);

    if (subItemListList == null || subItemListList.getLength() <= 0) {
      return null;
    }

    String result = null;
    Element subItemFirstElement = (Element) subItemListList.item(0);
    NodeList textFNList = subItemFirstElement.getChildNodes();
    if (textFNList != null) {
      if (((Node) textFNList.item(0)) != null && ((Node) textFNList.item(0)).getNodeValue() != null) {
        result = ((Node) textFNList.item(0)).getNodeValue().trim();
      }
    }

    return result;
  }

  public boolean isValidGuid(String guid) {
    return StringUtils.isEmpty(guid) || guid.matches("\\{[a-fA-F0-9]{8}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{4}-[a-fA-F0-9]{12}\\}");
  }

  @Override
  public String getLmngSoapRequestForIdpAndSp(String institutionId, List<String> serviceIds, Date validOn, String endpoint, LicenseRetrievalAttempt licenseRetrievalAttempt) throws IOException {
    checkNotNull(validOn);
    checkNotNull(serviceIds);

    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    ClassPathResource queryResource = new ClassPathResource(LICENSE_RETRIEVAL_ATTEMPT_TO_TEMPLATE.get(licenseRetrievalAttempt));

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
    query = StringEscapeUtils.escapeHtml4(query);
    result = fillInVariables(endpoint, result, query);

    return result;
  }

  private String fillInVariables(String endpoint, String result, String query) {
    return result.replaceAll(QUERY_PLACEHOLDER, query)
        .replaceAll(ENDPOINT_PLACEHOLDER, endpoint)
        .replaceAll(UID_PLACEHOLDER, UUID.randomUUID().toString());
  }

  public String getLmngSoapRequestForSps(Collection<String> serviceIds, String endpoint) throws IOException {
    checkNotNull(serviceIds);

    // Get the soap/fetch envelope
    String result = getLmngRequestEnvelope();

    ClassPathResource queryResource = new ClassPathResource(PATH_FETCH_QUERY_ARTICLES_FOR_SPS);
    ClassPathResource articleConditionResource = new ClassPathResource(PATH_FETCH_QUERYCONDITION_ARTICLE);

    try (InputStream inputStream = queryResource.getInputStream();
        InputStream articleInputStream = articleConditionResource.getInputStream()) {

      String articleConditionTemplate = IOUtils.toString(articleInputStream);
      String articleConditionValues = "";

      for (String serviceId : serviceIds) {
        articleConditionValues += articleConditionTemplate.replaceAll(SERVICE_IDENTIFIER_PLACEHOLDER, serviceId);
      }

      String query = IOUtils.toString(inputStream).replaceAll(ARTICLE_CONDITION_VALUE_PLACEHOLDER, articleConditionValues);

      return fillInVariables(endpoint, result, StringEscapeUtils.escapeHtml4(query));
    }
  }

  public String getLmngSoapRequestForAllAccount(boolean isInstitution, String endpoint) throws IOException {
    String result = getLmngRequestEnvelope();

    try (InputStream inputStream = new ClassPathResource(PATH_FETCH_ALL_ACCOUNTS).getInputStream()) {
      String query = IOUtils.toString(inputStream).replaceAll("%IS_INSTITUTION%", (isInstitution ? "1" : "0"));
      return fillInVariables(endpoint, result, StringEscapeUtils.escapeHtml4(query));
    }
  }

  public String getLmngRequestEnvelope() throws IOException {
    ClassPathResource envelopeResource = new ClassPathResource(PATH_SOAP_FETCH_REQUEST);

    try (InputStream inputStream = envelopeResource.getInputStream()) {
      return IOUtils.toString(inputStream);
    }
  }

}
