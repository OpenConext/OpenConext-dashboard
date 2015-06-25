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

package selfservice.sab;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.GregorianCalendar;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class SabClientTest {

  private SabClient sabClient;

  @Before
  public void setUp() throws Exception {
    sabClient = new SabClient(new LocalFileTransport("/response.xml", "/sab-json/profile.json"));
  }

  @Test
  public void createRequest() {
    String request = sabClient.createRequest("userid", "234567890");
    assertTrue(request.contains("<saml:NameID Format=\"urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified\">userid</saml:NameID>"));
    assertTrue(request.contains("ID=\"234567890\""));
    assertTrue(request.contains("IssueInstant=\"" + new GregorianCalendar().get(GregorianCalendar.YEAR)));
  }

  @Test
  public void exceptionWhileQueryingRole() throws IOException {
    String organisation = "SURFNET";
    String role = "Infraverantwoordelijke";

    SabTransport transport = mock(SabTransport.class);
    sabClient = new SabClient(transport);
    when(transport.getRestResponse(anyString(), anyString())).thenThrow(new RuntimeException("Intentionally"));
    final Collection<SabPerson> personsInRoleForOrganization = sabClient.getPersonsInRoleForOrganization(organisation, role);
    assertNotNull(personsInRoleForOrganization);
  }

  @Test(expected = IOException.class)
  public void invalidUser() throws IOException {
    String userId = "foo";

    sabClient = new SabClient(new LocalFileTransport("/response-invaliduser.xml", "/sab-json/profile.json"));
    sabClient.getRoles(userId);
  }

  @Test
  public void testGetPersonsInRoleForOrganization() throws Exception {
    Collection<SabPerson> actual = sabClient.getPersonsInRoleForOrganization("organisationAbbreviation", "SURFconextverantwoordelijke");
    assertEquals(6, actual.size());
  }

  @Test
  public void testOnlyReturnsPersonsWithTheGivenRole() throws Exception {
    Collection<SabPerson> actual = sabClient.getPersonsInRoleForOrganization("organisationAbbreviation", "OperationeelBeheerder");
    assertEquals(4, actual.size());
  }

  @Test
  public void testNoResultsFromRestInterface() throws Exception {
    sabClient = new SabClient(new LocalFileTransport("/response.xml", "/sab-json/minimal-roles.json"));
    Collection<SabPerson> actual = sabClient.getPersonsInRoleForOrganization("organisationAbbreviation", "SURFconextbeheerder");
    assertEquals(0, actual.size());
  }

}
