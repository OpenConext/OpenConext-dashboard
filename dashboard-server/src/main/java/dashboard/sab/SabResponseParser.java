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
package dashboard.sab;

import com.google.common.base.Throwables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static javax.xml.transform.OutputKeys.ENCODING;
import static javax.xml.transform.OutputKeys.INDENT;
import static javax.xml.transform.OutputKeys.METHOD;
import static javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION;

/**
 * XPath parser for SAB responses.
 */
@Component
public class SabResponseParser {

    private static final Logger LOG = LoggerFactory.getLogger(SabResponseParser.class);

    public static final String XPATH_ORGANISATION = "//saml:Attribute[@Name='urn:oid:1.3.6.1.4.1.1076.20.100.10.50.1']/saml:AttributeValue";
    public static final String XPATH_ROLES = "//saml:Attribute[@Name='urn:oid:1.3.6.1.4.1.5923.1.1.1.7']/saml:AttributeValue";
    public static final String XPATH_STATUSCODE = "//samlp:StatusCode/@Value";
    public static final String XPATH_STATUSMESSAGE = "//samlp:StatusMessage";

    public static final String SAMLP_SUCCESS = "urn:oasis:names:tc:SAML:2.0:status:Success";
    private static final String SAMLP_RESPONDER = "urn:oasis:names:tc:SAML:2.0:status:Responder";

    /**
     * Prefix of the status message if a user is queried that cannot be found.
     */
    private static final String NOT_FOUND_MESSAGE_PREFIX = "Could not find any roles for given NameID";

    private final DocumentBuilderFactory documentBuilderFactory;
    private final javax.xml.xpath.XPathFactory xPathFactory;

    public SabResponseParser() {
        documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(true);
        documentBuilderFactory.setIgnoringElementContentWhitespace(true);
        documentBuilderFactory.setValidating(false);

        xPathFactory = javax.xml.xpath.XPathFactory.newInstance();
    }

    public SabRoleHolder parse(InputStream inputStream) throws IOException {
        String organisation;
        List<String> roles;
        XPath xpath = getXPath();
        try {
            Document document = createDocument(inputStream);
            validateStatus(document, xpath);
            organisation = extractOrganisation(xpath, document);
            roles = extractRoles(xpath, document);
            return new SabRoleHolder(organisation, roles);
        } catch (XPathExpressionException | ParserConfigurationException e) {
            throw Throwables.propagate(e);
        } catch (SAXException e) {
            throw new IOException(e);
        }


    }

    private List<String> extractRoles(XPath xpath, Document document) throws XPathExpressionException {
        List<String> roles = new ArrayList<>();
        XPathExpression rolesExpr = xpath.compile(XPATH_ROLES);
        NodeList rolesNodeList = (NodeList) rolesExpr.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; rolesNodeList != null && i < rolesNodeList.getLength(); i++) {
            Node node = rolesNodeList.item(i);
            if (node != null) {
                roles.add(StringUtils.trimWhitespace(node.getTextContent()));
            }
        }

        return roles;
    }

    private String extractOrganisation(XPath xpath, Document document) throws XPathExpressionException {
        XPathExpression organisationExpr = xpath.compile(XPATH_ORGANISATION);
        NodeList nodeList = (NodeList) organisationExpr.evaluate(document, XPathConstants.NODESET);
        for (int i = 0; nodeList != null && i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node != null) {
                return StringUtils.trimWhitespace(node.getTextContent());
            }
        }
        return null;
    }

    /**
     * Check that response contains the success status. Throw IOException with message otherwise.
     */
    private void validateStatus(Document document, XPath xpath) throws XPathExpressionException, IOException {
        XPathExpression statusCodeExpression = xpath.compile(XPATH_STATUSCODE);
        String statusCode = (String) statusCodeExpression.evaluate(document, XPathConstants.STRING);

        if (!SAMLP_SUCCESS.equals(statusCode)) {
            // Status message is only set if status code not 'success'.
            XPathExpression statusMessageExpression = xpath.compile(XPATH_STATUSMESSAGE);
            String statusMessage = (String) statusMessageExpression.evaluate(document, XPathConstants.STRING);

            if (SAMLP_RESPONDER.equals(statusCode) && statusMessage.startsWith(NOT_FOUND_MESSAGE_PREFIX)) {
                LOG.debug("Given nameId not found in SAB. Is regarded by us as 'valid' response, although server response indicates a server error.");
            } else {
                throw new IOException(String.format("Unsuccessful status. Code: '%s', message: %s, document:\n %s", statusCode, statusMessage, prettyPrintDocument(document)));
            }
        }
    }

    private String prettyPrintDocument(Document document) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OMIT_XML_DECLARATION, "no");
            transformer.setOutputProperty(METHOD, "xml");
            transformer.setOutputProperty(INDENT, "yes");
            transformer.setOutputProperty(ENCODING, "UTF-8");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

            StringWriter stringWriter = new StringWriter();
            transformer.transform(new DOMSource(document), new StreamResult(stringWriter));
            return stringWriter.getBuffer().toString();
        } catch (TransformerException | TransformerFactoryConfigurationError e) {
            LOG.error("Failed to pretty print a document", e);
            return "";
        }
    }

    private Document createDocument(InputStream documentStream) throws ParserConfigurationException, IOException, SAXException {
        return documentBuilderFactory.newDocumentBuilder().parse(documentStream);
    }

    private XPath getXPath() {
        XPath xPath = xPathFactory.newXPath();
        xPath.setNamespaceContext(new SabNgNamespaceResolver());
        return xPath;
    }

    private static class SabNgNamespaceResolver implements NamespaceContext {

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
        public Iterator<String> getPrefixes(String namespaceURI) {
            return null;
        }
    }
}
