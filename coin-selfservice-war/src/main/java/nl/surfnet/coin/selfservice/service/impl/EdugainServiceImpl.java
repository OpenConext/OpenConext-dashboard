package nl.surfnet.coin.selfservice.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import nl.surfnet.coin.selfservice.service.EdugainApp;
import nl.surfnet.coin.selfservice.service.EdugainService;

public class EdugainServiceImpl implements EdugainService {

  private AtomicReference<List<EdugainApp>> apps = new AtomicReference(new ArrayList<EdugainApp>());
  private static final Logger LOG = LoggerFactory.getLogger(EdugainServiceImpl.class);

  private final Optional<URI> webSource;
  private final Optional<File> fileSource;
  private static final String XPATH_EXPRESSION = "/md:EntitiesDescriptor/md:EntityDescriptor/*[md:AssertionConsumerService]/..";
  private static final String UIINFO_PREFIX = "md:SPSSODescriptor/md:Extensions/mdui:UIInfo/";

  public EdugainServiceImpl(URI webSource) {
    LOG.debug("Initializing in web-mode");
    this.webSource = Optional.of(webSource);
    this.fileSource = Optional.absent();
  }

  public EdugainServiceImpl(File source) {
    LOG.debug("Initializing in file-mode");
    Preconditions.checkState(source.canRead(), "Can't read the edugain source file: " + source);
    this.fileSource = Optional.of(source);
    this.webSource = Optional.absent();
  }

  @Override
  public List<EdugainApp> getApps() {
    return ImmutableList.copyOf(apps.get());
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60) // refresh after an hour
  public void refreshApps() {
    List<EdugainApp> newApps = new ArrayList<>();

    try(InputStream inputStream = webSource.isPresent()? getContents(webSource.get()) : getContents(fileSource.get())) {
      final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
      Document document = builder.parse(inputStream);
      XPath xPath =  XPathFactory.newInstance().newXPath();
      xPath.setNamespaceContext(NAMESPACE_CONTEXT);
      NodeList nodeList = (NodeList) xPath.evaluate(XPATH_EXPRESSION, document, XPathConstants.NODESET);
      LOG.debug("Found {} items.", nodeList.getLength());


      for (int i = 0; i < nodeList.getLength(); i++) {
        // items appear as md:EntityDescriptor and just EntityDescriptor.
        Node entryNode = nodeList.item(i);
        EdugainApp edugainApp = new EdugainApp();

        String id = (String) xPath.evaluate("@entityID", entryNode, XPathConstants.STRING);
        edugainApp.setAppUrl(id);


        edugainApp.setName((String) xPath.evaluate(UIINFO_PREFIX + "mdui:DisplayName[@xml:lang='en']", entryNode, XPathConstants.STRING));

        edugainApp.setDescription((String) xPath.evaluate(UIINFO_PREFIX + "mdui:Description[@xml:lang='en']", entryNode, XPathConstants.STRING));
        edugainApp.setLogoUrl((String) xPath.evaluate(UIINFO_PREFIX + "mdui:Logo/text()", entryNode, XPathConstants.STRING));
        newApps.add(edugainApp);
      }
      apps.set(newApps);
    } catch (Exception e) {
      LOG.warn("Exception occurred while refreshing edugain apps. Not replacing the current set of apps, which has  " + apps.get().size() +" entries. Exception info: ", e);
    }


  }

  private static InputStream getContents(File file) {
    try {
      return new FileInputStream(file);
    } catch (FileNotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private static InputStream getContents(URI webUri) {
    // use httpclient
    return null;
  }


  private static final NamespaceContext NAMESPACE_CONTEXT = new NamespaceContext() {

    @Override
    public String getNamespaceURI(String prefix) {
      switch (prefix){
        case "xml":
          return "http://www.w3.org/XML/1998/namespace";
        case "md":
          return "urn:oasis:names:tc:SAML:2.0:metadata";
        case "mdui":
            return "urn:oasis:names:tc:SAML:metadata:ui";
        default:
          return XMLConstants.NULL_NS_URI;
      }
    }

    @Override
    public String getPrefix(String namespaceURI) {
      return null;
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
      return Collections.emptyIterator();
    }
  };

}
