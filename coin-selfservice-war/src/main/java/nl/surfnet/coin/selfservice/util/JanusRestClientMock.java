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
package nl.surfnet.coin.selfservice.util;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import static nl.surfnet.coin.janus.Janus.Metadata.*;
import nl.surfnet.coin.janus.Janus;
import nl.surfnet.coin.janus.Janus.Metadata;
import nl.surfnet.coin.janus.domain.ARP;
import nl.surfnet.coin.janus.domain.EntityMetadata;
import nl.surfnet.coin.janus.domain.JanusEntity;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StringUtils;

/**
 * JanusRestClientMock.java
 * 
 */
public class JanusRestClientMock implements Janus {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  private List<EntityMetadata> spList;
  private List<EntityMetadata> idpList;
  private List<EntityMetadata> all;

  private final static String SP_TYPE = "saml20-sp";
  private final static String IDP_TYPE = "saml20-idp";

  private final ARP EMPTY_ARP = null;
  private final ARP NON_EMPTY_ARP;

  @SuppressWarnings("unchecked")
  public JanusRestClientMock() {
    try {
      TypeReference<List<EntityMetadata>> typeReference = new TypeReference<List<EntityMetadata>>() {
      };
      this.spList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/sp.json");
      this.idpList = (List<EntityMetadata>) parseJsonData(typeReference, "janus-json/idp.json");
      all = new ArrayList<EntityMetadata>();
      all.addAll(spList);
      all.addAll(idpList);
      NON_EMPTY_ARP = (ARP) parseJsonData(new TypeReference<ARP>() {
      }, "janus-json/arp.json");
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getMetadataByEntityId(java.lang.String)
   */
  @Override
  public EntityMetadata getMetadataByEntityId(String entityId) {
    for (EntityMetadata metadata : all) {
      if (metadata.getAppEntityId().equals(entityId)) {
        return metadata;
      }
    }
    return null;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * nl.surfnet.coin.janus.Janus#getEntityIdsByMetaData(nl.surfnet.coin.janus
   * .Janus.Metadata, java.lang.String)
   */
  @Override
  public List<String> getEntityIdsByMetaData(Metadata key, String value) {
    // Two known cases: Janus.Metadata.OAUTH_CONSUMERKEY &
    // Janus.Metadata.INSITUTION_ID
    List<String> results = new ArrayList<String>();
    switch (key) {
    case OAUTH_CONSUMERKEY:
      for (EntityMetadata metadata : spList) {
        String consumerKey = metadata.getOauthConsumerKey();
        if (StringUtils.hasText(consumerKey) && consumerKey.matches(value)) {
          results.add(metadata.getAppEntityId());
        }
      }
      return results;
    case INSITUTION_ID:
      for (EntityMetadata metadata : idpList) {
        String institutionId = metadata.getInstutionId();
        if (StringUtils.hasText(institutionId) && institutionId.equalsIgnoreCase(value)) {
          results.add(metadata.getAppEntityId());
        }
      }
      return results;
    default:
      throw new RuntimeException("Only supported Janus.MetaData types are : " + Janus.Metadata.INSITUTION_ID + ","
          + Janus.Metadata.OAUTH_CONSUMERKEY);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getAllowedSps(java.lang.String)
   */
  @Override
  public List<String> getAllowedSps(String idpentityid) {
    List<String> results = new ArrayList<String>();
    for (int i = 0; i < spList.size(); i++) {
      EntityMetadata metadata = spList.get(i);
      if (i % 2 == 0) {
        results.add(metadata.getAppEntityId());
      }
    } 
    return results;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getAllowedSps(java.lang.String,
   * java.lang.String)
   */
  @Override
  public List<String> getAllowedSps(String idpentityid, String revision) {
    return getAllowedSps(idpentityid);
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getSpList()
   */
  @Override
  public List<EntityMetadata> getSpList() {
    return spList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getIdpList()
   */
  @Override
  public List<EntityMetadata> getIdpList() {
    return idpList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getArp(java.lang.String)
   */
  @Override
  public ARP getArp(String entityId) {
    if (entityId != null && entityId.contains("surf")) {
      return EMPTY_ARP;
    }
    return NON_EMPTY_ARP;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#isConnectionAllowed(java.lang.String,
   * java.lang.String)
   */
  @Override
  public boolean isConnectionAllowed(String spEntityId, String idpEntityId) {
    return true;
  }

  /*
   * (non-Javadoc)
   * 
   * @see nl.surfnet.coin.janus.Janus#getEntity(java.lang.String)
   */
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

  /*
   * Following methods are to be compatible with the real janus configuration
   */
  public void setJanusUri(URI janusUri) {
  }

  public void setUser(String user) {
  }

  public void setSecret(String secret) {
  }

}
