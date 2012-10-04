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

package nl.surfnet.coin.selfservice.command;

import nl.surfnet.coin.selfservice.domain.ServiceProvider;

/**
 * Pojo defining a {@link ServiceProvider} and it's LMNG identifier
 *
 */
public class LmngServiceBinding {

  private String lmngIdentifier;
  
  private String serviceId;
  
  private String serviceName;
  
  private String serviceDescription;

  /**
   * Default constructor
   */
  public LmngServiceBinding(){
    
  }
  
  /**
   * Constructor with initial fields
   * 
   * @param lmngIdentifier
   * @param serviceId
   * @param serviceName
   * @param serviceDescription
   */
  public LmngServiceBinding(String lmngIdentifier, String serviceId, String serviceName, String serviceDescription) {
    super();
    this.lmngIdentifier = lmngIdentifier;
    this.serviceId = serviceId;
    this.serviceName = serviceName;
    this.serviceDescription = serviceDescription;
  }

  public String getLmngIdentifier() {
    return lmngIdentifier;
  }

  public void setLmngIdentifier(String lmngIdentifier) {
    this.lmngIdentifier = lmngIdentifier;
  }

  public String getServiceId() {
    return serviceId;
  }

  public void setServiceId(String serviceId) {
    this.serviceId = serviceId;
  }

  public String getServiceName() {
    return serviceName;
  }

  public void setServiceName(String serviceName) {
    this.serviceName = serviceName;
  }

  public String getServiceDescription() {
    return serviceDescription;
  }

  public void setServiceDescription(String serviceDescription) {
    this.serviceDescription = serviceDescription;
  }
  
  
}
