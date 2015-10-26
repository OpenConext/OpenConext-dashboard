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
package selfservice.service.impl;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import selfservice.domain.*;
import selfservice.service.Csa;

import org.springframework.core.io.ClassPathResource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

/**
 * Mock implementation of CSA. To be filled with lots of data for local development. Perhaps JSON-local-file-backed.
 */
@SuppressWarnings("unchecked")
public class CsaMock implements Csa {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
    //.setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<Action> actionsCreated = new ArrayList<>();

  private List<Service> getServices() {
    List<Service> services = (List<Service>) parseJsonData(new TypeReference<List<Service>>() {
    }, "csa-json/protected-services.json");
    return restoreCategoryReferences(services);

  }

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    return getServices();

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
  public List<LicenseContactPerson> licenseContactPersons(String idpEntityId) {
    if (idpEntityId.equals("http://mock-idp")) {
      return (List<LicenseContactPerson>) parseJsonData(new TypeReference<List<LicenseContactPerson>>() {
      }, "csa-json/license-contact-persons.json");
    } else {
      return new ArrayList<>();
    }
  }

  @Override
  public List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId) {
    if (identityProviderId.endsWith("-3") || identityProviderId.endsWith("-4")) {
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

  @Override
  public List<InstitutionIdentityProvider> serviceUsedBy(String spEntityId) {
    if (spEntityId.equals("https://bod.dummy.sp")) {
      return new ArrayList<>();
    }
    return getAllInstitutionIdentityProviders();
  }

  public Object parseJsonData(TypeReference<?> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
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
