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

package nl.surfnet.coin.selfservice.provisioner;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.InstitutionIdentityProvider;
import nl.surfnet.coin.selfservice.domain.CoinUser;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.opensaml.DefaultBootstrap;
import org.opensaml.saml2.core.Assertion;
import org.opensaml.xml.Configuration;
import org.opensaml.xml.ConfigurationException;
import org.opensaml.xml.XMLObject;
import org.opensaml.xml.io.UnmarshallingException;
import org.springframework.core.io.ClassPathResource;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class SAMLProvisionerTest {

  @InjectMocks
  private SAMLProvisioner provisioner;

  @Mock
  private Csa csa;


  @Before
  public void before() {
    provisioner = new SAMLProvisioner();
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void test_provision_happy_flow() throws Exception {
    when(csa.getInstitutionIdentityProviders("https://surfguest.nl")).thenReturn(Collections.singletonList(new InstitutionIdentityProvider("https://surfguest.nl", "SURFguest", null)));
    final Assertion a = readAssertionFromFile("assertion.xml");

    CoinUser cu = (CoinUser) provisioner.provisionUser(a);

    assertEquals("urn:collab:person:surfguest.nl:gvanderploeg", cu.getUsername());
    assertEquals("https://surfguest.nl", cu.getIdp().getId());
    assertEquals("SURFguest", cu.getIdp().getName());
    assertEquals("surfguest.nl", cu.getSchacHomeOrganization());
    assertEquals("Geert van der Ploeg", cu.getDisplayName());
    assertEquals("gvanderploeg@iprofs.nl", cu.getEmail());
    assertEquals("urn:collab:person:surfguest.nl:gvanderploeg", cu.getUid());
  }

  @Test
  public void testMultipleAttributeValues() throws Exception {
    when(csa.getInstitutionIdentityProviders("https://surfguest.nl")).thenReturn(Collections.singletonList(new InstitutionIdentityProvider("https://surfguest.nl", "SURFguest", null)));
    final Assertion a = readAssertionFromFile("assertion.xml");

    CoinUser cu = (CoinUser) provisioner.provisionUser(a);
    List<String> values = cu.getAttributeMap().get("urn:mace:dir:attribute-def:eduPersonEntitlement");
    assertEquals(4, values.size());
  }

  private Assertion readAssertionFromFile(String filename) throws ConfigurationException, IOException, UnmarshallingException,
    SAXException, ParserConfigurationException {
    DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
    f.setNamespaceAware(true);
    DefaultBootstrap.bootstrap();
    return (Assertion) readFromFile(f.newDocumentBuilder(), new ClassPathResource(filename).getFile());
  }

  /**
   * OpenSAML Helper method to read an XML object from a file.
   */
  public XMLObject readFromFile(DocumentBuilder builder, File file) throws IOException, UnmarshallingException, SAXException {
    Element element = builder.parse(file).getDocumentElement();
    return Configuration.getUnmarshallerFactory().getUnmarshaller(element).unmarshall(element);
  }
}
