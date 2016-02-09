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
package selfservice.janus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;

import org.springframework.core.io.ClassPathResource;

import selfservice.domain.ARP;
import selfservice.janus.domain.EntityMetadata;
import selfservice.janus.domain.JanusEntity;

public class JanusRestClientMock implements Janus {

  private final static String SP_TYPE = "saml20-sp";
  private final static String IDP_TYPE = "saml20-idp";

  private final ObjectMapper objectMapper = new ObjectMapper()
     .setSerializationInclusion(Include.NON_NULL);

  private final List<EntityMetadata> spList;
  private final List<EntityMetadata> idpList;
  private final List<EntityMetadata> all = new ArrayList<>();

  private final Map<String, List<String>> spsForIdp = new HashMap<>();

  private final ARP NON_EMPTY_ARP;

  public JanusRestClientMock() {
      TypeReference<List<EntityMetadata>> typeReference = new TypeReference<List<EntityMetadata>>() {};

      this.spList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/sp.json");
      this.idpList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/idp.json");

      all.addAll(spList);
      all.addAll(idpList);

      NON_EMPTY_ARP = (ARP) parseJsonData(new TypeReference<ARP>() {}, "janus-json/arp.json");

      spsForIdp.put("https://idp_with_all_but_one_sp", ImmutableList.of(
          "http://mock-sp",
          "https://nice_sp",
          "https://sp_state_acc"
      ));

      spsForIdp.put("http://mock-idp", ImmutableList.of(
          "http://mock-sp",
          "https://nice_sp",
          "https://sp_state_acc",
          "https://sp_idp_only"
      ));
  }

  @Override
  public EntityMetadata getMetadataByEntityId(String entityId) {
    return all.stream()
        .filter(metadata -> metadata.getAppEntityId().equals(entityId))
        .findFirst().orElse(null);
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

  public <T> T parseJsonData(TypeReference<T> typeReference, String jsonFile) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw Throwables.propagate(e);
    }
  }

}
