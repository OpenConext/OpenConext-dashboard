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
package nl.surfnet.coin.selfservice.command;

import nl.surfnet.coin.csa.model.JiraTask;

public abstract class AbstractAction {

  private JiraTask.Type type;

  private String serviceProviderId;

  private String serviceName;

  private Long serviceId;

  public JiraTask.Type getType() {
    return type;
  }

  public void setType(JiraTask.Type type) {
    this.type = type;
  }

  public String getServiceProviderId() {
    return serviceProviderId;
  }

  public void setServiceProviderId(String serviceProviderId) {
    this.serviceProviderId = serviceProviderId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public Long getServiceId() {
    return serviceId;
  }

  public void setServiceId(Long serviceId) {
    this.serviceId = serviceId;
  }


}
