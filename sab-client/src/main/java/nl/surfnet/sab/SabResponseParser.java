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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * XPath parser for SAB responses.
 *
 */
@Component
public class SabResponseParser {

  public static final String XPATH_ORGANISATION = "//saml:Attribute[@Name='urn:oid:1.3.6.1.4.1.1076.20.100.10.50.1']/saml:AttributeValue";
  public static final String XPATH_ROLES = "//saml:Attribute[@Name='urn:oid:1.3.6.1.4.1.5923.1.1.1.7']/saml:AttributeValue";
  public static final String XPATH_STATUSCODE = "//samlp:StatusCode/@Value";
  public static final String XPATH_STATUSMESSAGE = "//samlp:StatusMessage";

  public static final String SAMLP_SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";

  public SabRoleHolder parse(InputStream inputStream) throws IOException {

    String organisation = null;
    List<String> roles = new ArrayList<String>();
    XPath xpath = getXPath();
    try {
      Document document = createDocument(inputStream);

      validateStatus(document, xpath);

      // Extract organisation
      XPathExpression organisationExpr = xpath.compile(XPATH_ORGANISATION);
      NodeList nodeList = (NodeList) organisationExpr.evaluate(document, XPathConstants.NODESET);
      for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
        Node node = nodeList.item(i);
        if (node != null) {
          organisation = StringUtils.trimWhitespace(node.getTextContent());
          node.getParentNode().getTextContent();
        }
      }

      // Extract roles
      XPathExpression rolesExpr = xpath.compile(XPATH_ROLES);
      NodeList rolesNodeList = (NodeList) rolesExpr.evaluate(document, XPathConstants.NODESET);
      for (int i = 0; rolesNodeList != null && i < rolesNodeList.getLength(); i++) {
        Node node = rolesNodeList.item(i);
        if (node != null) {
          roles.add(StringUtils.trimWhitespace(node.getTextContent()));
        }
      }

    } catch (XPathExpressionException e) {
      throw new RuntimeException(e);
    } catch (ParserConfigurationException e) {
      throw new RuntimeException(e);
    } catch (SAXException e) {
      throw new IOException(e);
    }
    return new SabRoleHolder(organisation, roles);
  }

  /**
   * Check that response contains the success status. Throw IOException with message otherwise.
   */
  private void validateStatus(Document document, XPath xpath) throws XPathExpressionException, IOException {

    XPathExpression statusCodeExpression = xpath.compile(XPATH_STATUSCODE);
    String statusCode = (String) statusCodeExpression.evaluate(document, XPathConstants.STRING);

    if (!SAMLP_SUCCESS.equals(statusCode)) {
      XPathExpression statusMessageExpression = xpath.compile(XPATH_STATUSMESSAGE);
      String statusMessage = (String) statusMessageExpression.evaluate(document, XPathConstants.STRING);
      throw new IOException("Unsuccessful status. Code: '" + statusCode + "', message: " + statusMessage);
    }
  }

  private Document createDocument(InputStream documentStream) throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setIgnoringElementContentWhitespace(true);
    factory.setValidating(false);

    DocumentBuilder builder = factory.newDocumentBuilder();
    return builder.parse(documentStream);
  }

  private XPath getXPath() {
    XPath xPath = javax.xml.xpath.XPathFactory.newInstance().newXPath();
    xPath.setNamespaceContext(new SabNgNamespaceResolver());
    return xPath;
  }

  private class SabNgNamespaceResolver implements NamespaceContext {

    @Override
    public String getNamespaceURI(String prefix) {
      if (prefix.equals("samlp")) {
        return "urn:oasis:names:tc:SAML:2.0:protocol";
      } else if (prefix.equals("saml")) {
        return "urn:oasis:names:tc:SAML:2.0:assertion";
      } else {
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
