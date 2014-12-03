package nl.surfnet.coin.selfservice.saml;

import nl.surfnet.spring.security.opensaml.AuthnRequestGenerator;
import nl.surfnet.spring.security.opensaml.SAMLMessageHandler;
import nl.surfnet.spring.security.opensaml.controller.AuthnRequestController;
import nl.surfnet.spring.security.opensaml.util.IDService;
import nl.surfnet.spring.security.opensaml.util.TimeService;
import nl.surfnet.spring.security.opensaml.xml.EndpointGenerator;
import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.opensaml.saml2.core.AuthnRequest;
import org.opensaml.saml2.metadata.Endpoint;
import org.opensaml.saml2.metadata.SingleSignOnService;
import org.opensaml.ws.message.encoder.MessageEncodingException;
import org.opensaml.xml.security.CriteriaSet;
import org.opensaml.xml.security.credential.Credential;
import org.opensaml.xml.security.credential.CredentialResolver;
import org.opensaml.xml.security.credential.UsageType;
import org.opensaml.xml.security.criteria.EntityIDCriteria;
import org.opensaml.xml.security.criteria.UsageCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Properties;

public class PostBindingSupportingAuthnRequestController extends AuthnRequestController {
  private final static Logger LOG = LoggerFactory.getLogger(PostBindingSupportingAuthnRequestController.class);

  private final TimeService timeService;
  private final IDService idService;

  private SAMLMessageHandler samlMessageHandler;

  private String assertionConsumerServiceURL;

  private String entityID;

  private CredentialResolver credentialResolver;
  private String protocolBinding;

  public PostBindingSupportingAuthnRequestController() {
    this.timeService = new TimeService();
    this.idService = new IDService();
  }

  @Required
  public void setCredentialResolver(CredentialResolver cr) {
    this.credentialResolver = cr;
  }

  @Required
  public void setSAMLMessageHandler(SAMLMessageHandler samlMessageHandler) {
    this.samlMessageHandler = samlMessageHandler;
  }

  @Required
  public void setAssertionConsumerServiceURL(String assertionConsumerServiceURL) {
    this.assertionConsumerServiceURL = assertionConsumerServiceURL;
  }

  @Required
  public void setEntityID(final String entityID) {
    this.entityID = entityID;
  }

  @Required
  public void setProtocolBinding(String protocolBinding) {
    this.protocolBinding = protocolBinding;
  }

  @RequestMapping(value = { "/OpenSAML.sso/Login" }, method = RequestMethod.GET)
  public void commence(@RequestParam(value = "target") String target, HttpServletRequest request, HttpServletResponse response)
    throws IOException {

    AuthnRequestGenerator authnRequestGenerator = new AuthnRequestGenerator(entityID, timeService, idService);
    EndpointGenerator endpointGenerator = new EndpointGenerator();

    Endpoint endpoint = endpointGenerator.generateEndpoint(SingleSignOnService.DEFAULT_ELEMENT_NAME, target, assertionConsumerServiceURL);

    AuthnRequest authnReqeust = authnRequestGenerator.generateAuthnRequest(target, assertionConsumerServiceURL);
    authnReqeust.setProtocolBinding(protocolBinding);
    
    LOG.debug("Sending authnRequest to {}", target);

    Credential signingCredential;
    try {
      CriteriaSet criteriaSet = new CriteriaSet();
      criteriaSet.add(new EntityIDCriteria(entityID));
      criteriaSet.add(new UsageCriteria(UsageType.SIGNING));
      signingCredential = credentialResolver.resolveSingle(criteriaSet);

      // Could be injected from somewhere. Not yet needed currently.
      String relayState = null;

      samlMessageHandler.sendSAMLMessage(authnReqeust, endpoint, response, relayState, signingCredential);
    } catch (MessageEncodingException mee) {
      LOG.error("Could not send authnRequest to Identity Provider.", mee);
      response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    } catch (org.opensaml.xml.security.SecurityException e) {
      LOG.error("Could not send authnRequest to Identity Provider.", e);
    }
  }


}
