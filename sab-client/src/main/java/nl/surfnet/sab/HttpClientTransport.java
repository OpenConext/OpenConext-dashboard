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
import org.apache.http.client.methods.HttpUriRequest;
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
import java.net.URI;

import static java.lang.String.format;

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
                             @Value("${sab-rest.endpoint") URI restEndPoint) {
    this.samlCredentials = samlCredentials;
    this.restCredentials = restCredentials;
    this.sabEndpoint = sabEndpoint;
    this.restEndPoint = restEndPoint;
    httpClient.getParams().setParameter("http.socket.timeout", TIMEOUT);
  }


  @Override
  public InputStream getResponse(final String request) throws IOException {
    HttpPost httpPost = new HttpPost(sabEndpoint);
    return handleRequest(httpPost, samlCredentials, new HttpRequestPreProcessor<HttpPost>() {
      @Override
      public void preProcess(HttpPost post) throws Exception {
        StringEntity stringEntity = new StringEntity(request);
        post.setEntity(stringEntity);
      }
    });
  }

  @Override
  public InputStream getRestResponse(String url) {
    HttpGet httpGet = new HttpGet(format("%s/%s", restEndPoint, url));
    return handleRequest(httpGet, restCredentials, null);
  }

  private InputStream handleRequest(HttpRequestBase request, UsernamePasswordCredentials credentials, HttpRequestPreProcessor preProcessor) {
    try {
      request.addHeader("Authorization", "Basic " + encodeUserPass(credentials));
      if (preProcessor != null) {
        preProcessor.preProcess(request);
      }
      HttpResponse httpResponse = httpClient.execute(request);
      return httpResponse.getEntity().getContent();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private String encodeUserPass(UsernamePasswordCredentials credentials) {
    return new String(Base64.encodeBase64(format("%s:%s", credentials.getUserName(), credentials.getPassword()).getBytes()));
  }

  private static interface HttpRequestPreProcessor<T extends HttpUriRequest> {
    void preProcess(T request) throws Exception;
  }

}