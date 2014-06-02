package nl.surfnet.coin.selfservice.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
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

  private static final String ENTRY_XPATH_EXPRESSION = "/md:EntitiesDescriptor/md:EntityDescriptor/*[md:AssertionConsumerService]/..";
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
  public List<DashboardApp> getApps(Set<String> spEntityIdsToFilterOut) {
    List<DashboardApp> results = new ArrayList<>();
    List<DashboardApp> candidates = ImmutableList.copyOf(apps.get());

    for (DashboardApp candidate: candidates) {
      if (!spEntityIdsToFilterOut.contains(candidate.getSpEntityId())) {
        results.add(candidate);
      }
    }
    return results;
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
      NodeList nodeList = (NodeList) xPath.evaluate(ENTRY_XPATH_EXPRESSION, document, XPathConstants.NODESET);
      LOG.debug("Found {} items.", nodeList.getLength());

      for (int i = 0; i < nodeList.getLength(); i++) {
        // items appear as md:EntityDescriptor and just EntityDescriptor.
        Node entryNode = nodeList.item(i);
        DashboardApp edugainApp = new DashboardApp();
        edugainApp.setEdugain(true);

        String entityId = (String) xPath.evaluate("@entityID", entryNode, XPathConstants.STRING);
        edugainApp.setSpEntityId(entityId);
        edugainApp.setAppUrl(entityId);
        String name = (String) xPath.evaluate(UIINFO_PREFIX + "mdui:DisplayName[@xml:lang='en']", entryNode, XPathConstants.STRING);
        if (name.trim().length() < 1) {
          // skip this entry, looking at the source xml has led us to conclude that there is no hope of finding a display-name somewhere else in the document;
          LOG.debug("Skipping entity {}, no display name found", entityId);
          continue;
        }

        edugainApp.setName(name);
        edugainApp.setDescription((String) xPath.evaluate(UIINFO_PREFIX + "mdui:Description[@xml:lang='en']", entryNode, XPathConstants.STRING));

        // attempt to find the first support info
        Node supportNode = (Node) xPath.evaluate("md:ContactPerson[@contactType='support'][1]", entryNode, XPathConstants.NODE);
        if (supportNode != null) {
          String roughEmail = (String) xPath.evaluate("md:EmailAddress", supportNode, XPathConstants.STRING);
          // some of them have "mailto:" prepended
          String email = roughEmail.toLowerCase().replaceAll("mailto:", "");
          edugainApp.setSupportMail(email);
        }
        edugainApp.setId(edugainApp.getAppUrl().hashCode() + edugainApp.getName().hashCode() + edugainApp.getDescription().hashCode());

        ARP arp = new ARP();
        arp.setNoArp(true);
        arp.setNoAttrArp(true);
        boolean hasAttr = false;
        NodeList arpAttributeNodes = (NodeList) xPath.evaluate("md:SPSSODescriptor/md:AttributeConsumingService/md:RequestedAttribute[@isRequired='true']/@Name", entryNode, XPathConstants.NODESET);
        for (int j = 0; j < arpAttributeNodes.getLength(); j++) {
          Node attrNode = arpAttributeNodes.item(j);
          final String propertyName = attrNode.getNodeValue();
          arp.getAttributes().put(propertyName, Arrays.<Object>asList("*"));
          hasAttr = true;
        }
        if (hasAttr) {
          arp.setNoArp(false);
          arp.setNoAttrArp(false);
        }
        edugainApp.setArp(arp);
        newApps.add(edugainApp);
      }
      LOG.debug("Caching edugain apps, currently holding {} entries", newApps.size());
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
