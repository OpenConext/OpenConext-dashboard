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
package nl.surfnet.coin.selfservice.domain;

import java.io.Serializable;

/**
 * CompoundServiceProviderRepresenter.java
 * 
 */
@SuppressWarnings("serial")
public class CompoundServiceProviderRepresenter implements Serializable {
  private long compoundServiceProviderId;
  private String spEntityId;

  public CompoundServiceProviderRepresenter() {
    super();
  }

  public CompoundServiceProviderRepresenter(long compoundServiceProviderId, String spEntityId) {
    super();
    this.compoundServiceProviderId = compoundServiceProviderId;
    this.spEntityId = spEntityId;
  }

  public long getCompoundServiceProviderId() {
    return compoundServiceProviderId;
  }

  public void setCompoundServiceProviderId(long compoundServiceProviderId) {
    this.compoundServiceProviderId = compoundServiceProviderId;
  }

  public String getSpEntityId() {
    return spEntityId;
  }

  public void setSpEntityId(String spEntityId) {
    this.spEntityId = spEntityId;
  }
}
