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

package selfservice.command;

import com.google.common.base.MoreObjects;

import selfservice.domain.ServiceProvider;
import selfservice.domain.csa.CompoundServiceProvider;

public class ServiceBinding {

  private final ServiceProvider serviceProvider;
  private final CompoundServiceProvider compoundServiceProvider;

  public ServiceBinding(ServiceProvider serviceProvider, CompoundServiceProvider compoundServiceProvider) {
    this.serviceProvider = serviceProvider;
    this.compoundServiceProvider = compoundServiceProvider;
  }

  public ServiceProvider getServiceProvider() {
    return serviceProvider;
  }

  public CompoundServiceProvider getCompoundServiceProvider() {
    return compoundServiceProvider;
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(ServiceBinding.class)
        .add("serviceProvider", serviceProvider)
        .add("compoundServiceProvider", compoundServiceProvider)
        .toString();
  }

}
