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
package nl.surfnet.coin.selfservice.dao.impl;

import nl.surfnet.coin.selfservice.dao.CompoundServiceProviderDao;
import nl.surfnet.coin.selfservice.domain.CompoundServiceProvider;
import nl.surfnet.coin.shared.service.GenericServiceHibernateImpl;

/**
 * CompoundServiceProviderHibernateDaoImpl.java
 * 
 */
public class CompoundServiceProviderHibernateDaoImpl extends GenericServiceHibernateImpl<CompoundServiceProvider> implements
    CompoundServiceProviderDao {

  /**
   * @param type
   */
  public CompoundServiceProviderHibernateDaoImpl(Class<CompoundServiceProvider> type) {
    super(type);
  }

}
