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
package nl.surfnet.coin.selfservice.service.impl;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.Account;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.service.LmngService;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * LmngServiceImplIT.java
 * 
 * @TODO move to the integration tests
 *       (/conext-integration-tests/src/test/java/nl/surfnet/conext/test/)
 * 
 *       NOTE! we us this for a local integration test only
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:coin-selfservice-context.xml",
    "classpath:coin-selfservice-properties-integration-context.xml", "classpath:coin-shared-context.xml" })
public class LmngServiceImplIT {

  @Autowired
  private LmngService licensingService;

  @Before
  public void init() throws FileNotFoundException, IOException {
  }

  // we us this for a local integration test only
//  @Test
  public void testRetrieveLmngGoogleEdugroepGreencloudSurfMarket() throws IOException, LmngException {

    List<String> spIds = new ArrayList<String>();
    spIds.add("http://www.google.com");
    spIds.add("Greencloud");
    spIds.add("EDUgroepen");

    List<Article> articles = licensingService.getArticlesForServiceProviders(spIds);

    assertNotNull(articles);
    assertEquals("Incorrect number of results", 3, articles.size());

    assertEquals("Incorrect name for product", "GreenQloud", articles.get(0).getProductName());

    assertEquals("Incorrect name for product", "Google apps voor edu", articles.get(1).getProductName());

    assertEquals("Incorrect name for product", "Edugroepen", articles.get(2).getProductName());

  }

//  @Test
  public void testRetrievalAllAccounts() throws IOException {
    List<Account> accounts = licensingService.getAccounts(true);
    System.out.println(accounts.size());

    accounts = licensingService.getAccounts(false);
    System.out.println(accounts.size());
    ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
            .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);
    System.out.println(objectMapper.writeValueAsString(accounts));

  }

//  @Test
  public void testPerformArticles() throws Exception {
    String query = IOUtils.toString(new ClassPathResource("lmngqueries/lmngQueryAllArticles.xml").getInputStream());
    String result = licensingService.performQuery(query);
    System.out.println(StringEscapeUtils.unescapeHtml(result));
  }

  // we us this for a local integration test only
  @Test
  public void testRetrieveInstitutionName() throws IOException {
    String guid = "{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}";
    
    String instituteName = licensingService.getInstitutionName(guid);

    assertNotNull(instituteName);
    assertEquals("Incorrect institution name", "Open Universiteit Nederland", instituteName);
  }

  // we us this for a local integration test only
  @Test
  public void testRetrieveArticle() throws IOException {
    String guid =
                              "{A1EA4AF9-6C9E-E111-B429-0050569E0013}";
    Article instituteName = licensingService.getService(guid);

    assertNotNull(instituteName);
  }

}
