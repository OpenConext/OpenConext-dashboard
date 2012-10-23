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

import java.util.Date;
import java.util.List;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LicensingService;
import nl.surfnet.coin.selfservice.service.impl.ssl.KeyStore;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;

/**
 * LicensingServiceMock.java
 * 
 */
@SuppressWarnings("unused")
public class LmngServiceMock implements LicensingService {

  private boolean debug;
  private String endpoint;
  private KeyStore keyStore;
  private KeyStore trustStore;
  private String keystorePassword;

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<Article> articles;

  @SuppressWarnings("unchecked")
  public LmngServiceMock() {
    try {
      TypeReference<List<Article>> typeReference = new TypeReference<List<Article>>() {
      };
      this.articles = (List<Article>) parseJsonData(typeReference, "lmng-json/licenses.json");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders) {
    return this.articles;
  }

  @Override
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProviders(IdentityProvider identityProvider,
      List<ServiceProvider> serviceProviders, Date validOn) {
    return this.articles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.selfservice.service.LicensingService#
   * getLicensesForIdentityProviderAndServiceProvider
   * (nl.surfnet.coin.selfservice.domain.IdentityProvider,
   * nl.surfnet.coin.selfservice.domain.ServiceProvider)
   */
  @Override
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider,
      ServiceProvider serviceProvider) {
    return this.articles;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.selfservice.service.LicensingService#
   * getLicensesForIdentityProviderAndServiceProvider
   * (nl.surfnet.coin.selfservice.domain.IdentityProvider,
   * nl.surfnet.coin.selfservice.domain.ServiceProvider, java.util.Date)
   */
  @Override
  public List<Article> getLicenseArticlesForIdentityProviderAndServiceProvider(IdentityProvider identityProvider,
      ServiceProvider serviceProvider, Date validOn) {
    return this.articles;
  }

  private Object parseJsonData(TypeReference<? extends Object> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void setDebug(boolean debug) {
    this.debug = debug;
  }

  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  public void setKeyStore(KeyStore keyStore) {
    this.keyStore = keyStore;
  }

  public void setTrustStore(KeyStore trustStore) {
    this.trustStore = trustStore;
  }

  public void setKeystorePassword(String keystorePassword) {
    this.keystorePassword = keystorePassword;
  }

}
