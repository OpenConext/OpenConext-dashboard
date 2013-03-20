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

package nl.surfnet.sab;

import java.io.IOException;
import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
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

public class HttpClientTransportTest {
  private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransportTest.class);

  private LocalTestServer server;

  @Before
  public void setupServer() throws Exception {
    server = new LocalTestServer(null, null);

    server.register("/test", new HttpRequestHandler() {
      @Override
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {
        response.setEntity(new StringEntity("This is the response"));
        response.setStatusCode(200);
      }
    });

    server.start();
  }

  @Test
  public void testGetResponse() throws IOException {

    HttpClientTransport transport = new HttpClientTransport();
    transport.setSabEndpoint(URI.create("http:/" + server.getServiceAddress().toString() + "/test"));
    String response = IOUtils.toString(transport.getResponse("foobarRequest"));
    assertTrue(response.contains("This is the response"));
  }


  @Test
  public void encodeUserPass() {
    String encoded = new HttpClientTransport().encodeUserPass("foo", "bar");

    assertEquals("foo:bar", new String(Base64.decodeBase64(encoded)));
  }

  @Test
  public void testAuthorizationHeader() throws IOException {
    HttpClientTransport transport = new HttpClientTransport();
    transport.setUsername("theuser");
    transport.setPassword("thepass");
    transport.setSabEndpoint(URI.create("http:/" + server.getServiceAddress().toString() + "/authorization"));


    server.register("/authorization", new HttpRequestHandler() {
      @Override
      public void handle(HttpRequest request, HttpResponse response, HttpContext context) throws HttpException, IOException {

        String expectedAuthHeader = "Basic " + new String(Base64.encodeBase64("theuser:thepass".getBytes()));
        if (request.getFirstHeader("Authorization") == null ||
          ! expectedAuthHeader.equals(request.getFirstHeader("Authorization").getValue())) {
          String msg = "Authorization header is not set (correctly): " + request.getFirstHeader("Authorization").getValue();
          LOG.error(msg);
          throw new IOException(msg);
        }
        response.setEntity(new StringEntity("Authorized!"));
        response.setStatusCode(200);
      }
    });

    String response = IOUtils.toString(transport.getResponse("foobarRequest"));
    assertEquals("Authorized!", response);
  }


}
