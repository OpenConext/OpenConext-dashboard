/*
 * Copyright 2013 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.surfnet.sab;

import static nl.surfnet.bod.web.WebUtils.not;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Resource;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import nl.surfnet.bod.util.Environment;
import nl.surfnet.bod.util.HttpUtils;
import nl.surfnet.bod.util.XmlUtils;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

/**
 * This component is named, only to enable injection of this specific component
 * in an integration test. At 'normal' runtime a component is injected using the
 * property {$sab.handler.class}
 *
 */
public class SabNgEntitlementsHandler implements EntitlementsHandler {

  private static final String STATUS_MESSAGE_NO_ROLES = "Could not find any roles for given NameID";
  private static final String STATUS_SUCCESS = "Success";

  private static final String REQUEST_TEMPLATE_LOCATION = "/xmlsabng/request-entitlement-template.xml";
  private static final String XPATH_STATUS_CODE = "//samlp:Status/samlp:StatusCode/@Value";
  private static final String XPATH_IN_RESPONSE_TO = "//samlp:Response/@InResponseTo";
  private static final String XPATH_STATUS_MESSAGE = "//samlp:Status/samlp:StatusMessage";
  private static final String XPATH_INSTITUTE = "//saml:Attribute[@Name='urn:oid:1.3.6.1.4.1.1076.20.100.10.50.1']";
  private static final String XPATH_SAML_CONDITIONS = "//saml:Conditions";
  private static final String XPATH_ISSUE_INSTANT = "//saml:Assertion/@IssueInstant";

  private final Logger logger = LoggerFactory.getLogger(this.getClass());

  private DefaultHttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

  @Value("${sab.endpoint}")
  private String sabEndPoint;

  @Value("${sab.user}")
  private String sabUser;

  @Value("${sab.issuer}")
  private String sabIssuer;

  @Value("${sab.enabled}")
  private String sabEnabled;

  @Resource
  private Environment bodEnvironment;

  /*
   * (non-Javadoc)
   *
   * @see
   * nl.surfnet.bod.sabng.EntitlementsHandler#checkInstitutes(java.lang.String)
   */
  @Override
  public List<String> checkInstitutes(String nameId) {

    if (not(isSabEnabled())) {
      logger.warn("Consulting SaB Entitlements is disabled");
      return Lists.newArrayList();
    }

    String messageId = UUID.randomUUID().toString();
    String requestBody = createRequest(messageId, sabIssuer, nameId);

    HttpPost httpPost = new HttpPost(sabEndPoint);
    httpPost.addHeader(HttpUtils.getBasicAuthorizationHeader(sabUser, bodEnvironment.getSabPassword()));

    try {
      StringEntity stringEntity = new StringEntity(requestBody);
      httpPost.setEntity(stringEntity);
      HttpResponse response = httpClient.execute(httpPost);

      return getInstitutesWhichHaveBoDAdminEntitlement(messageId, response.getEntity().getContent());
    }
    catch (XPathExpressionException | IllegalStateException | IOException e) {
      throw new RuntimeException(e);
    }

  }

  @VisibleForTesting
  String getRoleToMatch() {
    return bodEnvironment.getSabRole();
  }

  private String getStatusToMatch() {
    return STATUS_SUCCESS;
  }

  @VisibleForTesting
  void checkConditions(Document document) throws XPathExpressionException {
    XPath xPath = getXPath();

    XPathExpression conditionsExpression = xPath.compile(XPATH_SAML_CONDITIONS);
    Node conditions = ((Node) conditionsExpression.evaluate(document, XPathConstants.NODE));
    if (conditions == null) {
      // Nothing to check, might be in case of error message
      return;
    }
    NamedNodeMap attributes = conditions.getAttributes();

    DateTime notBeforeOrAfter = XmlUtils.getDateTimeFromXml(attributes.getNamedItem("NotBefore").getTextContent());
    DateTime notOnOrAfter = XmlUtils.getDateTimeFromXml(attributes.getNamedItem("NotOnOrAfter").getTextContent());

    XPathExpression issueInstantExpression = xPath.compile(XPATH_ISSUE_INSTANT);
    String issueInstantString = (String) issueInstantExpression.evaluate(document, XPathConstants.STRING);
    DateTime issueInstant = XmlUtils.getDateTimeFromXml(issueInstantString);

    validateIssueInstant(issueInstant, notBeforeOrAfter, notOnOrAfter);
  }

  @VisibleForTesting
  void validateIssueInstant(DateTime issueInstant, DateTime notBeforeOrAfter, DateTime notOnOrAfter) {
    Preconditions.checkState(issueInstant.isAfter(notBeforeOrAfter) && issueInstant.isBefore(notOnOrAfter),
      "IssueInstant [%s] is not between [%s] and [%s]", issueInstant, notBeforeOrAfter, notOnOrAfter);
  }

  /**
   * Since SAB will not be available in all environments at time of our release,
   * we need to be able to turn it off.
   *
   * @return true when sab should be used, false otherwise
   */
  @VisibleForTesting
  boolean isSabEnabled() {
    return sabEnabled == null || Boolean.parseBoolean(sabEnabled);
  }

  @VisibleForTesting
  void setSabEnabled(String booleanString) {
    this.sabEnabled = booleanString;
  }

  @VisibleForTesting
  String createRequest(String messageId, String issuer, String nameId) {
    String template;
    try {
      template = IOUtils.toString(this.getClass().getResourceAsStream(REQUEST_TEMPLATE_LOCATION), "UTF-8");
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    return MessageFormat.format(template, messageId, issuer, nameId);
  }

  @VisibleForTesting
  List<String> getInstitutesWhichHaveBoDAdminEntitlement(String messageId, InputStream responseStream)
    throws XPathExpressionException {

    Document document = createDocument(responseStream);

    checkStatusCodeAndMessageIdOrFail(document, getStatusToMatch(), messageId);
    checkConditions(document);

    return getInstitutesWithRoleToMatch(document, getRoleToMatch());
  }

  @VisibleForTesting
  void setHttpClient(DefaultHttpClient httpClient) {
    this.httpClient = httpClient;
  }

  private List<String> getInstitutesWithRoleToMatch(final Document document, final String roleToMatch)
    throws XPathExpressionException {
    XPath xPath = getXPath();
    List<String> institutes = new ArrayList<>();

    XPathExpression expression = xPath.compile(XPATH_INSTITUTE);
    NodeList nodeList = (NodeList) expression.evaluate(document, XPathConstants.NODESET);

    for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
      Node node = nodeList.item(i);
      if (node != null) {
        String entitlements = node.getParentNode().getTextContent();
        if (checkEntitlements(entitlements, roleToMatch)) {
          institutes.add(StringUtils.trimWhitespace(node.getTextContent()));
        }
      }
    }

    logger.debug("Found institutes [{}] with entitlement: {}", roleToMatch,
      StringUtils.collectionToCommaDelimitedString(institutes));

    return institutes;
  }

  private Document createDocument(InputStream responseStream) {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setIgnoringElementContentWhitespace(true);
    factory.setValidating(false);

    String responseString;
    try {
      responseString = IOUtils.toString(responseStream);
    }
    catch (IOException ioExc) {
      throw new RuntimeException(ioExc);
    }

    Document document;
    try {
      DocumentBuilder builder = factory.newDocumentBuilder();
      InputStream response = new ByteArrayInputStream(responseString.getBytes("UTF-8"));
      document = builder.parse(response);
    }
    catch (ParserConfigurationException | SAXException | IOException e) {
      throw new RuntimeException("Response was: [" + responseString + "]", e);
    }
    return document;
  }

  private void checkStatusCodeAndMessageIdOrFail(Document document, String statusToMatch, String inResponseToIdToMatch)
    throws XPathExpressionException {
    XPath xPath = getXPath();

    String statusCode = getStatusCode(document, xPath);
    String statusMessage = getStatusMessage(document, xPath);

    if (not(statusCode.contains(statusToMatch))) {
      Preconditions.checkState(StringUtils.hasText(statusMessage) && statusMessage.contains(STATUS_MESSAGE_NO_ROLES),
        "Could not retrieve roles, statusCode: [%s], statusMessage: %s", statusCode,
        statusMessage);
    }

    String inResponseToId = getResponseId(document, xPath);
    Preconditions.checkState(inResponseToId.equals(inResponseToIdToMatch),
      "InResponseTo does not match. Expected [%s], but was: [%s]",
      inResponseToIdToMatch, inResponseToId);
  }

  private XPath getXPath() {
    XPath xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
    xPath.setNamespaceContext(new SabNgNamespaceResolver());

    return xPath;
  }

  private boolean checkEntitlements(String entitlements, String roleToMatch) throws XPathExpressionException {
    return StringUtils.hasText(entitlements) && entitlements.contains(roleToMatch);
  }

  private String getResponseId(Document document, XPath xPath) throws XPathExpressionException {
    XPathExpression responseIdExpression = xPath.compile(XPATH_IN_RESPONSE_TO);
    return ((String) responseIdExpression.evaluate(document, XPathConstants.STRING));
  }

  private String getStatusMessage(Document document, XPath xPath) throws XPathExpressionException {
    XPathExpression statusMessageExpression = xPath.compile(XPATH_STATUS_MESSAGE);
    return (String) statusMessageExpression.evaluate(document, XPathConstants.STRING);
  }

  private String getStatusCode(Document document, XPath xPath) throws XPathExpressionException {
    XPathExpression statusExpression = xPath.compile(XPATH_STATUS_CODE);
    return (String) statusExpression.evaluate(document, XPathConstants.STRING);
  }

  private class SabNgNamespaceResolver implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {

      switch (prefix) {
        case "samlp":
          return "urn:oasis:names:tc:SAML:2.0:protocol";
        case "saml":
          return "urn:oasis:names:tc:SAML:2.0:assertion";
        default:
          return XMLConstants.NULL_NS_URI;
      }
    }

    @Override
    public String getPrefix(String namespaceURI) {
      return null;
    }

    @Override
    public Iterator<?> getPrefixes(String namespaceURI) {
      return null;
    }
  }

}