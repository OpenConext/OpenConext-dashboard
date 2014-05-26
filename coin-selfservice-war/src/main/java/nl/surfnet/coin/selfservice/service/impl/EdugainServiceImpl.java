package nl.surfnet.coin.selfservice.service.impl;

import java.io.File;
import java.io.FileInputStream;
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

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;

import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.selfservice.service.DashboardApp;
import nl.surfnet.coin.selfservice.service.EdugainService;

public class EdugainServiceImpl implements EdugainService {

  private AtomicReference<List<DashboardApp>> apps = new AtomicReference(new ArrayList<DashboardApp>());
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
  public List<DashboardApp> getApps() {
    return ImmutableList.copyOf(apps.get());
  }

  @Override
  public Optional<DashboardApp> getApp(Long id) {
    for (DashboardApp edugainApp: this.apps.get()) {
      if (id.equals(edugainApp.getId())){
        return Optional.of(edugainApp);
      }
    }
    return Optional.absent();
  }

  @Scheduled(fixedDelay = 1000 * 60 * 60) // refresh after an hour
  public void refreshApps() {

    final Optional<InputStream> inputStreamOptional = webSource.isPresent() ? getContents(webSource.get()) : getContents(fileSource.get());
    if (!inputStreamOptional.isPresent()) {
      return; // we could not read, we leave everything as it is
    }

    try(InputStream inputStream = inputStreamOptional.get()){
      List<DashboardApp> newApps = new ArrayList<>();
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
        DashboardApp edugainApp = new DashboardApp();
        edugainApp.setEdugain(true);

        String id = (String) xPath.evaluate("@entityID", entryNode, XPathConstants.STRING);
        edugainApp.setAppUrl(id);
        edugainApp.setName((String) xPath.evaluate(UIINFO_PREFIX + "mdui:DisplayName[@xml:lang='en']", entryNode, XPathConstants.STRING));
        edugainApp.setDescription((String) xPath.evaluate(UIINFO_PREFIX + "mdui:Description[@xml:lang='en']", entryNode, XPathConstants.STRING));

        // assign an id based on name,description and appUrl
        edugainApp.setId(edugainApp.getAppUrl().hashCode() + edugainApp.getName().hashCode() + edugainApp.getDescription().hashCode());

        ARP arp = new ARP();
        arp.setNoArp(true);
        arp.setNoAttrArp(true);
        edugainApp.setArp(arp);
        newApps.add(edugainApp);
      }
      apps.set(newApps);
    } catch (Exception e) {
      LOG.warn("Exception occurred while refreshing edugain apps. Not replacing the current set of apps, which has  " + apps.get().size() +" entries. Exception info: ", e);
    }

  }

  private static Optional<InputStream> getContents(File file) {
    try {
      final FileInputStream fileInputStream = new FileInputStream(file);
      return Optional.of((InputStream) fileInputStream);
    } catch (Exception e) {
      LOG.error("Unable to open file, no items will be read", e);
    }
    return Optional.absent();
  }

  private static Optional<InputStream> getContents(URI webUri) {
    try {
      HttpClient httpclient = new DefaultHttpClient();
      // read it in at most 20 seconds or give up
      httpclient.getParams().setParameter("http.socket.timeout", new Integer(20000));
      HttpGet httpget = new HttpGet(webUri);
      HttpResponse response = httpclient.execute(httpget);
      HttpEntity entity = response.getEntity();
      if (entity != null) {
        return Optional.of(entity.getContent());
      }
    } catch (Exception e) {
      LOG.warn("Could no read edugain apps from {}, no items will be read", webUri);
    }
    return Optional.absent();
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
