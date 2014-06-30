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

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;

@Component
public class HttpClientTransport implements SabTransport {

  private static final Logger LOG = LoggerFactory.getLogger(HttpClientTransport.class);

  private static final Integer TIMEOUT = new Integer(10000);
  private HttpClient httpClient = new DefaultHttpClient(new PoolingClientConnectionManager());

  private final UsernamePasswordCredentials samlCredentials;
  private final UsernamePasswordCredentials restCredentials;
  private final URI sabEndpoint;
  private final URI restEndPoint;

  @Autowired
  public HttpClientTransport(@Qualifier("samlCredentials") UsernamePasswordCredentials samlCredentials,
                             @Qualifier("restCredentials") UsernamePasswordCredentials restCredentials,
                             @Value("${sab.endpoint}") URI sabEndpoint,
                             @Value("${sab-rest.endpoint}") URI restEndPoint) {
    this.samlCredentials = samlCredentials;
    this.restCredentials = restCredentials;
    this.sabEndpoint = sabEndpoint;
    this.restEndPoint = restEndPoint;
    httpClient.getParams().setParameter("http.socket.timeout", TIMEOUT);
  }


  @Override
  public InputStream getResponse(final String request) throws IOException {
    HttpPost httpPost = new HttpPost(sabEndpoint);
    StringEntity stringEntity = new StringEntity(request);
    httpPost.setEntity(stringEntity);
    return handleRequest(httpPost, samlCredentials);
  }

  @Override
  public InputStream getRestResponse(String organisationAbbreviation, String role) {
    HttpGet httpGet = null;
    try {
      httpGet = new HttpGet(format("%s/profile?abbrev=%s&role=%s", restEndPoint, encode(organisationAbbreviation, "UTF-8"), encode(role, "UTF-8")));
    } catch (UnsupportedEncodingException e) {
      throw new RuntimeException(e);
    }
    return handleRequest(httpGet, restCredentials);
  }

  private InputStream handleRequest(HttpRequestBase request, UsernamePasswordCredentials credentials) {
    try {
      request.addHeader("Authorization", "Basic " + encodeUserPass(credentials));
      HttpResponse httpResponse = httpClient.execute(request);
      return httpResponse.getEntity().getContent();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String encodeUserPass(UsernamePasswordCredentials credentials) {
    return new String(Base64.encodeBase64(format("%s:%s", credentials.getUserName(), credentials.getPassword()).getBytes()));
  }

}