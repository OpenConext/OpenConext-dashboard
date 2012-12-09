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

package nl.surfnet.coin.selfservice.dao;

import java.util.List;

import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProviderRepresenter;
import nl.surfnet.coin.selfservice.domain.IdentityProviderRepresenter;
import nl.surfnet.coin.selfservice.domain.ServiceProvider;

/**
 * Service for statistics
 */
public interface StatisticDao {

  /**
   * Makes a List of login data per Service Provider got the Identity Provider
   *
   * @param idpEntityId unique identifier of the Identity provider
   * @return List of {@link ChartSerie}
   */
  List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId);

  /**
   * Makes a List of login data per Service Provider for all IdP's 
   *
   * @return List of {@link ChartSerie}
   */
  List<ChartSerie> getLoginsPerSpPerDay();
  
  /**
   * Get all the idp's that have login records 
   * 
   */
  List<IdentityProviderRepresenter> getIdpLoginIdentifiers();

  /**
   * Get all existing {@link CompoundServiceProvider} id with the respective {@link ServiceProvider} entityIds
   */
  List<CompoundServiceProviderRepresenter> getCompoundServiceProviderSpLinks();
}

