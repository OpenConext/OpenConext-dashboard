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
package dashboard.domain;

import java.io.Serializable;

import com.google.common.base.MoreObjects;

@SuppressWarnings("serial")
public class InstitutionIdentityProvider implements Serializable {

  private String id;
  private String name;
  private String nameNl;
  private String institutionId;
  private String state;
  private String logoUrl;

  public InstitutionIdentityProvider() {
  }


  public InstitutionIdentityProvider(String id, String name, String nameNl, String institutionId, String state, String logoUrl) {
    this.id = id;
    this.name = name;
    this.nameNl = nameNl;
    this.institutionId = institutionId;
    this.logoUrl = logoUrl;
    this.state = state;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getInstitutionId() {
    return institutionId;
  }

  public void setInstitutionId(String institutionId) {
    this.institutionId = institutionId;
  }

  public String getState() {
    return state;
  }

  public void setState(String state) {
    this.state = state;
  }

  public String getLogoUrl() {
    return logoUrl;
  }

  public void setLogoUrl(String logoUrl) {
    this.logoUrl = logoUrl;
  }

  public String getNameNl() {
    return nameNl;
  }

  public void setNameNl(String nameNl) {
    this.nameNl = nameNl;
  }


  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("id", id).add("name", name).toString();
  }

}
