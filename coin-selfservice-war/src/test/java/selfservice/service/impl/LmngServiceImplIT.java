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
package selfservice.service.impl;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.csa.Account;
import selfservice.domain.csa.IdentityProvider;
import selfservice.service.impl.LmngException;
import selfservice.service.impl.LmngServiceImpl;
import selfservice.domain.License;

@Ignore("Only used for integration testing")
@RunWith(MockitoJUnitRunner.class)
public class LmngServiceImplIT {

  private LmngServiceImpl subject;

  @Mock
  private LmngIdentifierDao lmngIdentifierDao;

  private static String ENDPOINT = "https://crmproxypilot.surfmarket.nl/crmservice.svc";

  @Before
  public void before() {
    subject = new LmngServiceImpl(lmngIdentifierDao, ENDPOINT);
  }

  @Test
  public void testRetrievalAllAccounts() throws IOException {
    List<Account> accounts = subject.getAccounts(true);

    assertThat(accounts, hasSize(greaterThan(400)));

    accounts = subject.getAccounts(false);

    assertThat(accounts, hasSize(greaterThan(200)));
  }

  @Test
  public void testPerformArticles() throws Exception {
    String query = IOUtils.toString(new ClassPathResource("lmngqueries/lmngQueryAllArticles.xml").getInputStream());
    String result = subject.performQuery(query);

    assertThat(result, containsString("<GetDataResult>"));
  }

  @Test
  public void testRetrieveInstitutionName() throws IOException {
    String guid = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";

    String instituteName = subject.getInstitutionName(guid);

    assertEquals("Open Universiteit Nederland", instituteName);
  }

  @Test
  public void testRetrieveLicenseWithTwoRevisions() throws LmngException {
    IdentityProvider identityProvider = new IdentityProvider();
    identityProvider.setId("erasmus");
    identityProvider.setInstitutionId("Erasmus");
    final String lmngId = "{A14817CC-1B10-DC11-A6C7-0019B9DE3AA4}";
    final String articleId = "{A9666CC8-4491-E211-9DB6-0050569E0011}";
    when(this.lmngIdentifierDao.getLmngIdForIdentityProviderId(identityProvider.getInstitutionId())).thenReturn(lmngId);

    // {F46CCB08-6135-E111-B32A-0050569E0007} {4EF1EE04-ED7C-E111-8393-0050569E0011} {FFA274E1-E5DA-E111-8363-0050569E0011} {6157077A-D933-E211-BCF7-0050569E0013}

    List<License> result = subject.getLicensesForIdpAndSp(identityProvider, articleId);
    assertTrue(result.size() > 0);
  }


  @Test
  public void testRawQuery() {
    subject.performQuery("<fetch version=\"1.0\" output-format=\"xml-platform\" mapping=\"logical\" distinct=\"true\">" +
      "<entity name=\"lmng_sdnarticle\">" +
//"   <filter>"+
//"     <condition attribute=\"lmng_sdnarticleid\" operator=\"in\">"+
//"       <value>{099F8003-64A7-E211-9388-0050569E66E5}</value>"+
//"     </condition>"+
//"   </filter>"+
      "   <link-entity name=\"lmng_sdnarticle_lmng_product\" from=\"lmng_sdnarticleid\" to=\"lmng_sdnarticleid\" visible=\"false\" intersect=\"true\">" +
      "         <attribute name=\"lmng_sdnarticleid\"/>" +
      "     <link-entity name=\"lmng_product\" from=\"lmng_productid\" to=\"lmng_productid\" alias=\"product\">" +
      "        <link-entity name=\"lmng_productvariation\" from=\"lmng_productid\" to=\"lmng_productid\" alias=\"productvariation\">" +
      "         <attribute name=\"lmng_licensemodel\"/>" +
      "         <link-entity name=\"lmng_licenseagreement\" from=\"lmng_productvariationid\" to=\"lmng_productvariationid\" alias=\"license\" >" +
      "           <attribute name=\"lmng_number\"/>" +
      "           <attribute name=\"lmng_validfrom\"/>" +
      "           <attribute name=\"lmng_validto\"/>" +
      "           <attribute name=\"lmng_organisationid\"/>" +
      "           <filter type=\"and\">" +
      "             <condition attribute=\"lmng_validfrom\" operator=\"on-or-before\" value=\"2013-05-01\" />" +
      "             <condition attribute=\"lmng_validto\" operator=\"on-or-after\" value=\"2013-05-01\" />" +
      "             <condition attribute=\"statuscode\" operator=\"eq\" value=\"4\"/>" +
      "             <condition attribute=\"lmng_organisationid\" operator=\"eq\" value=\"{837326CA-1A10-DC11-A6C7-0019B9DE3AA4}\" />" +
      "           </filter>" +
      "         </link-entity>" +
      "       </link-entity>" +
      "     </link-entity>" +
      "   </link-entity>" +
      " </entity>" +
      "</fetch>");
  }

}
