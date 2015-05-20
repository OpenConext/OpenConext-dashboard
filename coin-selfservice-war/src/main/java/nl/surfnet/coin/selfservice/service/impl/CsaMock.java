/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package nl.surfnet.coin.selfservice.service.impl;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.*;
import nl.surfnet.coin.oauth.OauthClient;
import org.apache.commons.lang.NotImplementedException;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Mock implementation of CSA. To be filled with lots of data for local development. Perhaps JSON-local-file-backed.
 */
@SuppressWarnings("unchecked")
public class CsaMock implements Csa {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<Action> actionsCreated = new ArrayList<>();

  @Override
  public List<Service> getPublicServices() {

    List<Service> services = (List<Service>) parseJsonData(new TypeReference<List<Service>>() {
    }, "csa-json/public-services.json");
    return restoreCategoryReferences(services);
  }

  @Override
  public List<Service> getProtectedServices() {
    List<Service> services = (List<Service>) parseJsonData(new TypeReference<List<Service>>() {
    }, "csa-json/protected-services.json");
    return restoreCategoryReferences(services);
  }

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    return getProtectedServices();

  }

  @Override
  public List<OfferedService> findOfferedServicesFor(String idpEntityId) {
    List<OfferedService> result = new ArrayList<>();
    for (Service service : getProtectedServices()) {
      result.add(new OfferedService(service));
    }
    return result;
  }

  @Override
  public Service getServiceForIdp(String idpEntityId, long serviceId) {
    List<Service> services = getServicesForIdp(idpEntityId);
    for (Service s : services) {
      if (s.getId() == serviceId) {
        return s;
      }
    }
    return null;
  }

  @Override
  public Service getServiceForIdp(String idpEntityId, String spEntityId) {
    List<Service> services = getServicesForIdp(idpEntityId);
    for (Service s : services) {
      if (s.getSpEntityId() != null && s.getSpEntityId().equals(spEntityId)) {
        return s;
      }
    }
    return null;
  }

  @Override
  public void setCsaBaseLocation(String location) {
  }

  @Override
  public Statistics getStatistics(int month, int year) {
    throw new NotImplementedException();
  }

  @Override
  public Taxonomy getTaxonomy() {
    Taxonomy taxonomy = (Taxonomy) parseJsonData(new TypeReference<Taxonomy>() {
    }, "csa-json/taxonomy_" + getLocale() + ".json");
    List<Category> categories = taxonomy.getCategories();
    for (Category category : categories) {
      List<CategoryValue> values = category.getValues();
      for (CategoryValue value : values) {
        value.setCategory(category);
      }
    }
    return taxonomy;

  }

  @Override
  public List<Action> getJiraActions(String idpEntityId) {
    List<Action> actions = (List<Action>) parseJsonData(new TypeReference<List<Action>>() {
    }, "csa-json/actions.json");
    actions.addAll(actionsCreated);
    return actions;
  }

  @Override
  public void clearProviderCache() {
  }

  @Override
  public Action createAction(Action action) {
    action.setStatus(JiraTask.Status.OPEN);
    action.setJiraKey("TEST-" + System.currentTimeMillis());
    action.setId(System.currentTimeMillis());
    action.setIdpName("Mock IdP");
    action.setSpName("Mock SP");

    actionsCreated.add(action);
    return action;
  }

  @Override
  public List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId) {
    if(identityProviderId.endsWith("-3") || identityProviderId.endsWith("-4")) {
      return (List<InstitutionIdentityProvider>) parseJsonData(new TypeReference<List<InstitutionIdentityProvider>>() {
      }, "csa-json/institution-identity-providers-2.json");

    } else {
      return (List<InstitutionIdentityProvider>) parseJsonData(new TypeReference<List<InstitutionIdentityProvider>>() {
      }, "csa-json/institution-identity-providers.json");
    }
  }

  @Override
  public List<InstitutionIdentityProvider> getAllInstitutionIdentityProviders() {
    return (List<InstitutionIdentityProvider>) parseJsonData(new TypeReference<List<InstitutionIdentityProvider>>() {
    }, "csa-json/all-institution-identity-providers.json");
  }


  public Object parseJsonData(TypeReference<?> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void setOauthClient(OauthClient oauthClient) {
  }

  private List<Service> restoreCategoryReferences(List<Service> services) {
    for (Service service : services) {
      service.restoreCategoryReferences();
    }
    return services;
  }

  private String getLocale() {
    Locale locale = null;
    ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (sra != null) {
      HttpServletRequest request = sra.getRequest();
      if (request != null) {
        locale = RequestContextUtils.getLocale(request);
      }
    }
    return locale != null ? locale.getLanguage() : "en";

  }


}
