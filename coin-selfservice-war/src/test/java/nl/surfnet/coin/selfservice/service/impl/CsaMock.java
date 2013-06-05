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

import java.util.ArrayList;
import java.util.List;

import nl.surfnet.coin.csa.Csa;
import nl.surfnet.coin.csa.model.*;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;

/**
 * Mock implementation of CSA. To be filled with lots of data for local development. Perhaps JSON-local-file-backed.
 */
public class CsaMock implements Csa {


  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<Action> actionsCreated = new ArrayList<Action>();

  @Override
  public List<Service> getPublicServices() {
    return (List<Service>) parseJsonData(new TypeReference<List<Service>>(){}, "csa-json/public-services.json");
  }

  @Override
  public List<Service> getProtectedServices() {
    return (List<Service>) parseJsonData(new TypeReference<List<Service>>(){}, "csa-json/protected-services.json");
  }

  @Override
  public List<Service> getServicesForIdp(String idpEntityId) {
    return (List<Service>) parseJsonData(new TypeReference<List<Service>>(){}, "csa-json/services-for-idp.json");

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
    return (Service) parseJsonData(new TypeReference<Service>(){}, "csa-json/service-for-idp.json");
  }

  @Override
  public void setCsaBaseLocation(String location) {
  }

  @Override
  public Taxonomy getTaxonomy() {
    return (Taxonomy) parseJsonData(new TypeReference<Taxonomy>(){}, "csa-json/taxonomy.json");
  }

  @Override
  public List<Action> getJiraActions(String idpEntityId) {
    List<Action> actions = (List<Action>) parseJsonData(new TypeReference<List<Action>>() {}, "csa-json/actions.json");
    actions.addAll(actionsCreated);
    return actions;
  }

  @Override
  public Action createAction(Action action) {
    action.setStatus(JiraTask.Status.OPEN);
    action.setJiraKey("TEST-"+System.currentTimeMillis());
    action.setId(System.currentTimeMillis());
    action.setIdpName("Mock IdP");
    action.setSpName("Mock SP");

    actionsCreated.add(action);
    return action;
  }

  @Override
  public List<InstitutionIdentityProvider> getInstitutionIdentityProviders(String identityProviderId) {
    return ( List<InstitutionIdentityProvider>) parseJsonData(new TypeReference<List<InstitutionIdentityProvider>>() { }, "csa-json/institution-identity-providers.json");
  }


  public Object parseJsonData(TypeReference<? extends Object> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}