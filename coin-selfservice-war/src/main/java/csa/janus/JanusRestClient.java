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

package csa.janus;

import csa.janus.domain.ARP;
import csa.janus.domain.EntityMetadata;
import csa.janus.domain.JanusEntity;
import org.apache.commons.codec.binary.Hex;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * REST client implementation for Janus.
 */
public class JanusRestClient implements Janus {

  private static Logger LOG = LoggerFactory.getLogger(JanusRestClient.class);

  private RestTemplate restTemplate;

  private final URI janusUri;

  private final String user;

  private final String secret;

  private final ObjectMapper objectMapper = new ObjectMapper();

  public JanusRestClient(URI uri, String user, String secret) {
    this.janusUri = uri;
    this.user = user;
    this.secret = secret;

    this.restTemplate = new RestTemplate();
//    restTemplate
//      .setMessageConverters(Arrays.<HttpMessageConverter<?>>asList(new MappingJackson2HttpMessageConverter()));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public EntityMetadata getMetadataByEntityId(String entityId) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("entityid", entityId);

    final String keys = Arrays.asList(Metadata.values()).stream().map(md -> md.val()).collect(Collectors.joining(","));
    parameters.put("keys", keys);

    URI signedUri;
    try {
      signedUri = sign("getMetadata", parameters);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }

      @SuppressWarnings("unchecked")
      final Map<String, Object> restResponse = restTemplate.getForObject(signedUri, Map.class);
      Assert.notNull(restResponse, "Rest response from Janus should not be null");

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      final EntityMetadata entityMetadata = EntityMetadata.fromMetadataMap(restResponse);

      entityMetadata.setAppEntityId(entityId);
      return entityMetadata;
    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
      throw new RuntimeException(e);

    }
  }

  @Override
  public List<String> getAllowedSps(String idpentityid) {
    return getAllowedSps(idpentityid, null);
  }

  @Override
  public List<String> getAllowedSps(String idpentityid, String revision) {
    Assert.hasText(idpentityid, "idpentityid is a required parameter");
    Map<String, String> parameters = new HashMap<>();
    parameters.put("idpentityid", idpentityid);
    if (StringUtils.hasLength(revision)) {
      parameters.put("idprevision", revision);
    }

    URI signedUri;
    try {
      signedUri = sign("getAllowedSps", parameters);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }

      @SuppressWarnings("unchecked")
      final List<String> restResponse = restTemplate.getForObject(signedUri, List.class);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      return restResponse;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
      throw new RuntimeException(e);

    }
  }

  @Override
  public List<String> getAllowedIdps(String spentityid) {
    Assert.hasText(spentityid, "spentityid is a required parameter");
    Map<String, String> parameters = new HashMap<>();
    parameters.put("spentityid", spentityid);

    URI signedUri;
    try {
      signedUri = sign("getAllowedIdps", parameters);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }

      @SuppressWarnings("unchecked")
      final List<String> restResponse = restTemplate.getForObject(signedUri, List.class);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      return restResponse;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
      throw new RuntimeException(e);

    }
  }

  @Override
  public List<EntityMetadata> getSpList() {

    Map<String, String> parameters = new HashMap<String, String>();

    final String keys = Arrays.asList(Metadata.values()).stream().map(md -> md.val()).collect(Collectors.joining(","));
    parameters.put("keys", keys);

    URI signedUri;
    try {
      signedUri = sign("getSpList", parameters);

      LOG.trace("Signed Janus-request is: {}", signedUri);

      String json = restTemplate.getForObject(signedUri, String.class);
      @SuppressWarnings("unchecked")
      final Map<String, Map<String, Object>> restResponse = objectMapper.readValue(json, Map.class);//restTemplate.getForObject(signedUri, Map.class);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      List<EntityMetadata> entities = new ArrayList<EntityMetadata>();
      for (Map.Entry<String, Map<String, Object>> entry : restResponse.entrySet()) {
        String entityId = entry.getKey();
        final EntityMetadata e = EntityMetadata.fromMetadataMap(entry.getValue());
        e.setAppEntityId(entityId);
        entities.add(e);
      }

      return entities;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
      throw new RuntimeException(e);

    }
  }

  @Override
  public List<EntityMetadata> getIdpList() {

    Map<String, String> parameters = new HashMap<>();

    final String keys = Arrays.asList(Metadata.values()).stream().map(md -> md.val()).collect(Collectors.joining(","));
    parameters.put("keys", keys);

    URI signedUri;
    try {
      signedUri = sign("getIdpList", parameters);

      LOG.trace("Signed Janus-request is: {}", signedUri);

      @SuppressWarnings("unchecked")
      final Map<String, Map<String, Object>> restResponse = restTemplate.getForObject(signedUri, Map.class);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      List<EntityMetadata> entities = new ArrayList<EntityMetadata>();
      for (Map.Entry<String, Map<String, Object>> entry : restResponse.entrySet()) {
        String entityId = entry.getKey();
        final EntityMetadata e = EntityMetadata.fromMetadataMap(entry.getValue());
        e.setAppEntityId(entityId);
        entities.add(e);
      }

      return entities;

    } catch (IOException e) {
      LOG.error("While doing Janus-request", e);
      throw new RuntimeException(e);

    }
  }

  @Override
  public ARP getArp(String entityId) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("entityid", entityId);
    URI signedUri = null;
    try {
      signedUri = sign("arp", parameters);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }
      final Map restResponse = restTemplate.getForObject(signedUri, Map.class);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      return (restResponse == null) ? null : ARP.fromRestResponse(restResponse);
    } catch (IOException e) {
      LOG.error("Could not do ARP request to Janus", e);
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean isConnectionAllowed(String spEntityId, String idpEntityId) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("spentityid", spEntityId);
    parameters.put("idpentityid", idpEntityId);
    URI signedUri = null;
    try {
      signedUri = sign("isConnectionAllowed", parameters);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }
      final List restResponse = restTemplate.getForObject(signedUri, List.class);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }
      return restResponse != null && restResponse.size() > 0 ? (Boolean) restResponse.get(0) : false;
    } catch (IOException e) {
      LOG.error("Could not do isConnectionAllowed request to Janus", e);
      throw new RuntimeException(e);
    }

  }

  @Override
  public JanusEntity getEntity(String entityId) {
    Map<String, String> parameters = new HashMap<>();
    parameters.put("entityid", entityId);
    URI signedUri = null;
    try {
      signedUri = sign("getEntity", parameters);
      if (LOG.isTraceEnabled()) {
        LOG.trace("Signed Janus-request is: {}", signedUri);
      }
      @SuppressWarnings("unchecked")
      final Map<String, Object> restResponse = restTemplate.getForObject(signedUri, Map.class);

      if (LOG.isTraceEnabled()) {
        LOG.trace("Janus-request returned: {}", objectMapper.writeValueAsString(restResponse));
      }

      return restResponse == null ? null : JanusEntity.fromJanusResponse(restResponse);
    } catch (IOException e) {
      LOG.error("Could not do getEntity request to Janus", e);
      throw new RuntimeException(e);
    }

  }

  /**
   * Sign the given method call.
   *
   * @param method     the name of the method to call
   * @param parameters additional parameters that need to be passed to Janus
   * @return URI with parameters janus_sig and janus_key
   * @throws IOException
   */
  private URI sign(String method, Map<String, String> parameters) throws IOException {
    Map<String, String> keys = new TreeMap<String, String>();
    keys.put("janus_key", user);
    keys.put("method", method);

    keys.putAll(parameters);

    keys.put("rest", "1");
    keys.put("userid", user);
    Set<String> keySet = keys.keySet();
    StringBuilder toSign = new StringBuilder(secret);
    for (String key : keySet) {
      toSign.append(key);
      toSign.append(keys.get(key));
    }

    MessageDigest digest;
    try {
      digest = MessageDigest.getInstance("SHA-512");
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("Cannot use algorithm SHA-512", e);
    }
    digest.reset();
    final String charsetName = "UTF-8";
    byte[] input = digest.digest(toSign.toString().getBytes(charsetName));
    char[] value = Hex.encodeHex(input);
    String janus_sig = new String(value);
    keys.put("janus_sig", janus_sig);

    StringBuilder url = new StringBuilder();
    keySet = keys.keySet();
    for (String key : keySet) {
      if (url.length() > 0) {
        url.append('&');
      }
      url.append(key).append('=').append(URLEncoder.encode(keys.get(key), charsetName));
    }
    String uri = url.toString();
    return URI.create(janusUri + "?" + uri);
  }

}
