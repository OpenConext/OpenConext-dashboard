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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Date;
import java.util.List;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;
import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.internal.stubbing.answers.Returns;
import org.springframework.core.io.ClassPathResource;

/**
 * Test class for {@code LmngServiceImpl}
 * 
 */
public class LmngServiceImplTest implements HttpRequestHandler {

  private static LocalTestServer testServer;

  private static LmngServiceImpl lmngServiceImpl;

  private String xmlFile;

  @BeforeClass
  public static void beforeClass() throws Exception {
    testServer = new LocalTestServer(null, null);
    testServer.start();

    lmngServiceImpl = new LmngServiceImpl();
    InetSocketAddress addr = testServer.getServiceAddress();
    lmngServiceImpl.setEndpoint("http://" + addr.getHostName() + "/mock/crm");
    lmngServiceImpl.setDebug(false);

    LmngIdentifierDao dao = mock(LmngIdentifierDao.class, new Returns("whatever"));
    lmngServiceImpl.setLmngIdentifierDao(dao);

  }

  @Before
  public void before() {
    testServer.register("/mock/*", this);
  }

  @Test
  @Ignore
  public void testFetchResultSingleLicense() {
    xmlFile = "lmngRequestResponse/tempResponseExampleActual.xml";
    IdentityProvider identityProvider = new IdentityProvider();
    identityProvider.setInstitutionId("dummy");
    ServiceProvider serviceProvider = new ServiceProvider("dummysp");
    List<Article> articles = lmngServiceImpl.getLicenseArticlesForIdentityProviderAndServiceProvider(identityProvider, serviceProvider, new Date());
    assertEquals("Aanbesteden1", articles.get(0).getServiceDescriptionNl());
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.apache.http.protocol.HttpRequestHandler#handle(org.apache.http.HttpRequest
   * , org.apache.http.HttpResponse, org.apache.http.protocol.HttpContext)
   */
  @Override
  public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
    response.setEntity(new StringEntity(IOUtils.toString(new ClassPathResource(xmlFile).getInputStream())));
    response.setStatusCode(200);
  }

}
