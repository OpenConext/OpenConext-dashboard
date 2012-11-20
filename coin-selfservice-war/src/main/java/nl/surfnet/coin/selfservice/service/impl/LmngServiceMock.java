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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nl.surfnet.coin.selfservice.domain.Article;
import nl.surfnet.coin.selfservice.domain.IdentityProvider;
import nl.surfnet.coin.selfservice.domain.License;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;
import nl.surfnet.coin.selfservice.service.LmngService;
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
public class LmngServiceMock implements LmngService {

  private boolean debug;
  private String endpoint;
  private KeyStore keyStore;
  private KeyStore trustStore;
  private String keystorePassword;
  private boolean activeMode;

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private Article article;
  private License license;

  public LmngServiceMock() {
    try {
      TypeReference<Article> articleTypeReference = new TypeReference<Article>() {
      };
      this.article = (Article) parseJsonData(articleTypeReference, "lmng-json/articles.json");
      TypeReference<License> licenseTypeReference = new TypeReference<License>() {
      };
      this.license = (License) parseJsonData(licenseTypeReference, "lmng-json/licenses.json");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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

  public void setActiveMode(boolean activeMode) {
    this.activeMode = activeMode;
  }

  public boolean isActiveMode() {
    return activeMode;
  }

  private void invariant() {
    if (!activeMode) {
      throw new RuntimeException(this.getClass().getSimpleName() + " is not active. No calls can be made");
    }

  }

  @Override
  public String getInstitutionName(String guid) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getServiceName(String lmngId) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<License> getLicensesForIdpAndSp(IdentityProvider identityProvider, String articleIdentifier, Date validOn) {
    return Arrays.asList(new License[] {license});
  }

  @Override
  public List<Article> getArticlesForServiceProviders(List<String> serviceProviderEntityIds) {
    return Arrays.asList(new Article[] {article});
  }

}
