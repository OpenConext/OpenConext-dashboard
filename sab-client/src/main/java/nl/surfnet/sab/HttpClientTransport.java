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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class HttpClientTransport implements SabTransport {

  private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransport.class);


  private HttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

  private String username;
  private String password;
  private URI sabEndpoint;

  @Override
  public InputStream getResponse(String request) throws IOException {
    HttpPost httpPost = new HttpPost(sabEndpoint);

    httpPost.addHeader("Authorization", "Basic " + encodeUserPass(username, password));

    LOG.debug("Request to SAB: {}", request);

    StringEntity stringEntity = new StringEntity(request);
    httpPost.setEntity(stringEntity);
    HttpResponse response = httpClient.execute(httpPost);
    InputStream responseAsStream = response.getEntity().getContent();
    String responseAsString = IOUtils.toString(responseAsStream);
    LOG.debug("Response from SAB: {}", responseAsString);

    return new ByteArrayInputStream(responseAsString.getBytes());

  }

  String encodeUserPass(String username, String password) {
    return new String(Base64.encodeBase64((username + ":" + password).getBytes()));
  }


  public void setSabEndpoint(URI sabEndpoint) {
    this.sabEndpoint = sabEndpoint;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public void setUsername(String username) {
    this.username = username;
  }
}