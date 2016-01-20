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

import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;

import com.google.common.base.Throwables;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class HttpClientTransport implements SabTransport {

  private static final int SOCKET_TIMEOUT = 10000;

  private final HttpClient httpClient;

  private final UsernamePasswordCredentials samlCredentials;
  private final UsernamePasswordCredentials restCredentials;
  private final URI sabEndpoint;
  private final URI restEndPoint;

  @Autowired
  public HttpClientTransport(@Value("${sab.username}") String sabUserName,
                             @Value("${sab.password}") String sabPassword,
                             @Value("${sab-rest.username}") String sabRestUserName,
                             @Value("${sab-rest.password}") String sabRestPassword,
                             @Value("${sab.endpoint}") URI sabEndpoint,
                             @Value("${sab-rest.endpoint}") URI restEndPoint) {
    this.samlCredentials = new UsernamePasswordCredentials(sabUserName, sabPassword);
    this.restCredentials = new UsernamePasswordCredentials(sabRestUserName, sabRestPassword);
    this.sabEndpoint = sabEndpoint;
    this.restEndPoint = restEndPoint;

    this.httpClient = HttpClients.custom()
        .setDefaultRequestConfig(RequestConfig.custom().setSocketTimeout(SOCKET_TIMEOUT).build())
        .setConnectionManager(new PoolingHttpClientConnectionManager()).build();
  }

  @Override
  public InputStream getResponse(String request) throws IOException {
    HttpUriRequest httpRequest = RequestBuilder
        .post()
        .setUri(sabEndpoint)
        .setEntity(new StringEntity(request)).build();
    return handleRequest(httpRequest, samlCredentials);
  }

  @Override
  public InputStream getRestResponse(String organisationAbbreviation, String role) {
    try {
      HttpGet httpGet = new HttpGet(format("%s/profile?abbrev=%s&role=%s", restEndPoint, encode(organisationAbbreviation, "UTF-8"), encode(role, "UTF-8")));
      return handleRequest(httpGet, restCredentials);
    } catch (UnsupportedEncodingException e) {
      throw Throwables.propagate(e);
    }
  }

  private InputStream handleRequest(HttpUriRequest request, UsernamePasswordCredentials credentials) {
    try {
      request.addHeader(AUTHORIZATION, "Basic " + encodeUserPass(credentials));
      HttpResponse httpResponse = httpClient.execute(request);
      return httpResponse.getEntity().getContent();
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

  private String encodeUserPass(UsernamePasswordCredentials credentials) {
    return new String(Base64.encodeBase64(format("%s:%s", credentials.getUserName(), credentials.getPassword()).getBytes()));
  }

}
