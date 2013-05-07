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

package nl.surfnet.coin.selfservice.cdkclient;

import java.io.IOException;
import java.util.List;

import nl.surfnet.coin.selfservice.api.model.LicenseInformation;
import nl.surfnet.coin.selfservice.api.model.LicenseStatus;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.entity.StringEntity;
import org.apache.http.localserver.LocalTestServer;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CdkClientTest {

  private static final Logger LOG = LoggerFactory.getLogger(CdkClientTest.class);


  private LocalTestServer server;

  @Before
  public void setupServer() throws Exception {
    server = new LocalTestServer(null, null);

    server.register("/test", new HttpRequestHandler() {
      @Override
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        LOG.debug("Request: {}", request.getRequestLine().getUri());
        assertTrue("Request should contain idp", request.getRequestLine().getUri().contains("idpEntityId=someIdp"));
//        response.setEntity(.....);
        response.setEntity(new StringEntity("[{\"spEntityId\":\"spEntityId\",\"status\":\"AVAILABLE\",\"license\":{\"startDate\":1367911454753,\"endDate\":1367911454753,\"licenseNumber\":\"DWS-XX-GLK76\",\"institutionName\":\"Institution Name\",\"groupLicense\":true}}]"));
        response.setHeader("Content-Type", "application/json");
        response.setStatusCode(200);
      }
    });

    server.start();
  }

  @Test
  public void licenseInformation() throws IOException {
    String endpoint = "http:/" + server.getServiceAddress().toString() + "/test";
    LOG.debug("Server listens at: {}", endpoint);
    CdkClient cdkClient = new CdkClient();
    cdkClient.setCdkLicensesLocation(endpoint);
    List<LicenseInformation> licenseInformation = cdkClient.getLicenseInformation("someIdp");
    assertEquals(1, licenseInformation.size());
    assertEquals(LicenseStatus.AVAILABLE, licenseInformation.get(0).getStatus());
  }
}
