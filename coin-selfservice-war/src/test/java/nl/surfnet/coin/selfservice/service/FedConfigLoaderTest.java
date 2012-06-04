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

package nl.surfnet.coin.selfservice.service;

import java.io.IOException;
import java.util.List;

import com.thoughtworks.xstream.XStream;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import nl.surfnet.coin.selfservice.domain.ACL;
import nl.surfnet.coin.selfservice.domain.ARP;
import nl.surfnet.coin.selfservice.domain.ContactPerson;
import nl.surfnet.coin.selfservice.domain.ContactPersonType;
import nl.surfnet.coin.selfservice.domain.FederatieConfig;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ProviderType;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.util.XStreamFedConfigBuilder;

import static junit.framework.Assert.assertEquals;

/**
 * Tests the loading of SURFfederatie XML
 */
public class FedConfigLoaderTest {

  private static final Logger log = LoggerFactory.getLogger(FedConfigLoaderTest.class);

  @Test
  public void testParseXML() throws IOException {
    final XStream xStream = XStreamFedConfigBuilder.getXStreamForFedConfig(true);
    Resource resource = new ClassPathResource("fedcfg.xml");
    final FederatieConfig config = (FederatieConfig) xStream.fromXML(resource.getInputStream());
    log.trace(xStream.toXML(config));
    final List<ServiceProvider> sps = config.getSps();
    final ServiceProvider serviceProvider = sps.get(0);
    assertEquals("http://example.com/sp-entity-id", serviceProvider.getId());
  }

  @Test
  public void testBuildXML() {

    FederatieConfig config = new FederatieConfig();
    String idpId = "urn:federation:testidp";
    String institutionId = "ACME University";
    String idpName = "Test IdP";
    IdentityProvider idp = new IdentityProvider(idpId, institutionId, idpName);
    idp.setType(ProviderType.saml20);

    String contactName = "John Doe";
    String contactEmail = "john.doe@example.com";
    String contactPhone = "+31123456789";
    ContactPerson adminContact = new ContactPerson(contactName, contactEmail);
    adminContact.setContactPersonType(ContactPersonType.administrative);
    adminContact.setTelephoneNumber(contactPhone);

    ContactPerson techContact = new ContactPerson(contactName, contactEmail);
    techContact.setContactPersonType(ContactPersonType.technical);

    idp.addContactPerson(adminContact);
    idp.addContactPerson(techContact);

    config.addIdP(idp);

    String spId = "urn:federation:testsp";
    String spName = "Test sp";
    ServiceProvider serviceProvider = new ServiceProvider(spId, spName);
    serviceProvider.addContactPerson(adminContact);
    serviceProvider.setHomeUrl("http://example.com/testsp");
    serviceProvider.setLogoUrl("http://example.com/logo.png");
    serviceProvider.setType(ProviderType.saml20);

    ARP arp = new ARP();
    arp.addAttributeName("urn:mace:dir:attribute-def:uid");
    arp.addAttributeName("urn:mace:dir:attribute-def:cn");
    arp.addAttributeName("urn:mace:dir:attribute-def:mail");
    arp.addAttributeName("urn:mace:dir:attribute-def:eduPersonAffiliation");
    serviceProvider.addArp(arp);


    ACL acl = new ACL();
    acl.addIdpRef(idpId);
    serviceProvider.setAcl(acl);

    config.addSp(serviceProvider);

    XStream xStream = XStreamFedConfigBuilder.getXStreamForFedConfig(true);
    log.debug(xStream.toXML(config));
  }
}
