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

package csa.janus.domain;

import java.util.Map;

/**
 * Subset of the response you get for "getEntity" in the Janus Rest API
 * <p/>
 * Full response:
 * <pre>Entity {eid=1087, entityid=http://mujina-sp-1087, revision=0, parent=null,
 * revisionnote=No revision note, type=saml20-sp, allowedall=no, workflow=prodaccepted,
 * metadataurl=null, prettyname=http://mujina-sp-1087, arp=3184, user=0}</pre>
 */
public class JanusEntity {
  private String entityId;
  private int revision;
  private String workflowStatus;
  private String type;
  private boolean allowAll;
  private int eid;
  private String prettyName;

  private JanusEntity() {

  }

  public JanusEntity(int eid, int revision) {
    this.eid = eid;
    this.revision = revision;
  }

  public JanusEntity(int eid, String entityId) {
    super();
    this.eid = eid;
    this.entityId = entityId;
  }

  public JanusEntity(int eid, int revision, String entityId) {
    this.eid = eid;
    this.revision = revision;
    this.entityId = entityId;
  }

  public static JanusEntity fromJanusResponse(Map<String, Object> janusResponse) {
    JanusEntity janusEntity = new JanusEntity();
    janusEntity.setEntityId((String) janusResponse.get("entityid"));
    janusEntity.setRevision((String) janusResponse.get("revision"));
    janusEntity.setType((String) janusResponse.get("type"));
    janusEntity.setWorkflowStatus((String) janusResponse.get("workflow"));
    janusEntity.setAllowAll((String) janusResponse.get("allowedall"));
    janusEntity.setEid((String) janusResponse.get("eid"));
    janusEntity.setPrettyName((String) janusResponse.get("prettyname"));

    return janusEntity;
  }

  public String getEntityId() {
    return entityId;
  }

  public void setEntityId(String entityId) {
    this.entityId = entityId;
  }

  public int getRevision() {
    return revision;
  }

  public void setRevision(int revision) {
    this.revision = revision;
  }

  private void setRevision(String revision) {
    this.revision = Integer.parseInt(revision);
  }

  public String getWorkflowStatus() {
    return workflowStatus;
  }

  public void setWorkflowStatus(String workflowStatus) {
    this.workflowStatus = workflowStatus;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public boolean isAllowAll() {
    return allowAll;
  }

  public void setAllowAll(boolean allowAll) {
    this.allowAll = allowAll;
  }

  private void setAllowAll(String allowedall) {
    this.allowAll = "yes".equals(allowedall);
  }

  public int getEid() {
    return eid;
  }

  public void setEid(int eid) {
    this.eid = eid;
  }

  private void setEid(String eid) {
    this.eid = Integer.parseInt(eid);
  }

  public String getPrettyName() {
    return prettyName;
  }

  public void setPrettyName(String prettyName) {
    this.prettyName = prettyName;
  }
}
