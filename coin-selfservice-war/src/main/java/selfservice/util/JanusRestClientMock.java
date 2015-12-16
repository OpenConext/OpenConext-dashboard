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
package selfservice.util;

import selfservice.domain.ARP;
import selfservice.janus.Janus;
import selfservice.janus.domain.EntityMetadata;
import selfservice.janus.domain.JanusEntity;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;

import java.util.*;
import java.util.stream.Collectors;

public class JanusRestClientMock implements Janus {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
          .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<EntityMetadata> spList;
  private List<EntityMetadata> idpList;
  private final List<EntityMetadata> all = new ArrayList<>();

  private final Map<String, List<String>> spsForIdp = new HashMap<>();

  private final static String SP_TYPE = "saml20-sp";
  private final static String IDP_TYPE = "saml20-idp";

  private final ARP NON_EMPTY_ARP;

  @SuppressWarnings("unchecked")
  public JanusRestClientMock() {
      TypeReference<List<EntityMetadata>> typeReference = new TypeReference<List<EntityMetadata>>() {
      };
      this.spList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/sp.json");
      this.idpList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/idp.json");
      all.addAll(spList);
      all.addAll(idpList);
      NON_EMPTY_ARP = (ARP) parseJsonData(new TypeReference<ARP>() {
      }, "janus-json/arp.json");
      spsForIdp.put("https://idp_with_all_but_one_sp", Arrays.asList(new String[]{
              "http://mock-sp",
              "https://nice_sp",
              "https://sp_state_acc"
      }));
      spsForIdp.put("http://mock-idp", Arrays.asList(new String[]{
              "http://mock-sp",
              "https://nice_sp",
              "https://sp_state_acc",
              "https://sp_idp_only"
      }));

  }

  @Override
  public EntityMetadata getMetadataByEntityId(String entityId) {
    for (EntityMetadata metadata : all) {
      if (metadata.getAppEntityId().equals(entityId)) {
        return metadata;
      }
    }
    return null;
  }

  @Override
  public List<String> getAllowedSps(String idpentityid) {
    List<String> allowedSps = spsForIdp.get(idpentityid);
    return allowedSps != null ? allowedSps : new ArrayList<>();
  }

  @Override
  public List<String> getAllowedSps(String idpentityid, String revision) {
    return getAllowedSps(idpentityid);
  }

  @Override
  public List<String> getAllowedIdps(String sppentityid) {
    return spsForIdp.keySet().stream().collect(Collectors.toList());
  }

  @Override
  public List<EntityMetadata> getSpList() {
    return spList;
  }

  @Override
  public List<EntityMetadata> getIdpList() {
    return idpList;
  }

  @Override
  public ARP getArp(String entityId) {
    return NON_EMPTY_ARP;
  }

  @Override
  public boolean isConnectionAllowed(String spEntityId, String idpEntityId) {
    return spsForIdp.get(idpEntityId).contains(spEntityId);

  }

  @Override
  public JanusEntity getEntity(String entityId) {
    EntityMetadata metadata = getMetadataByEntityId(entityId);
    if (metadata == null) {
      return null;
    }
    JanusEntity janusEntity = new JanusEntity(1, entityId);
    janusEntity.setAllowAll(!metadata.isIdpVisibleOnly());
    janusEntity.setPrettyName(metadata.getAppDescription());
    janusEntity.setType(getType(entityId));
    janusEntity.setWorkflowStatus("prodaccepted");
    return janusEntity;
  }

  private String getType(String entityId) {
    for (EntityMetadata metadata : spList) {
      if (metadata.getAppEntityId().equals(entityId)) {
        return SP_TYPE;
      }
    }
    for (EntityMetadata metadata : idpList) {
      if (metadata.getAppEntityId().equals(entityId)) {
        return IDP_TYPE;
      }
    }
    return null;
  }

  public Object parseJsonData(TypeReference<? extends Object> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
