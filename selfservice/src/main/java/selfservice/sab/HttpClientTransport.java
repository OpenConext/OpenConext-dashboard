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
import static org.apache.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URLEncoder;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
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

    RequestConfig requestConfig = RequestConfig.custom()
        .setConnectTimeout(2000)
        .setConnectionRequestTimeout(2000)
        .setSocketTimeout(2000).build();

    this.httpClient = HttpClients.custom()
        .setDefaultRequestConfig(requestConfig)
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
  public InputStream getRestResponse(String organisationAbbreviation, String role) throws IOException {
    HttpGet httpGet = new HttpGet(format("%s/profile?abbrev=%s&role=%s", restEndPoint, URLEncoder.encode(organisationAbbreviation, "UTF-8"), URLEncoder.encode(role, "UTF-8")));
    return handleRequest(httpGet, restCredentials);
  }

  private InputStream handleRequest(HttpUriRequest request, UsernamePasswordCredentials credentials) throws IOException {
    request.addHeader(AUTHORIZATION, "Basic " + encodeUserPass(credentials));

    HttpResponse response = httpClient.execute(request);

    if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
      throw new IOException("Failed response: " + response.getStatusLine());
    }

    return response.getEntity().getContent();
  }

  private String encodeUserPass(UsernamePasswordCredentials credentials) {
    return new String(Base64.encodeBase64(format("%s:%s", credentials.getUserName(), credentials.getPassword()).getBytes()));
  }

}
