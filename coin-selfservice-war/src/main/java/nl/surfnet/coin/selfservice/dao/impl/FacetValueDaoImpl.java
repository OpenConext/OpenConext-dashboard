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
package nl.surfnet.coin.selfservice.dao.impl;

import nl.surfnet.coin.selfservice.dao.FacetValueDao;
import nl.surfnet.coin.selfservice.domain.FacetValue;
import nl.surfnet.coin.shared.service.GenericServiceHibernateImpl;
import org.hibernate.jdbc.Work;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

@Repository
public class FacetValueDaoImpl extends GenericServiceHibernateImpl<FacetValue> implements FacetValueDao {

  public FacetValueDaoImpl() {
    super(FacetValue.class);
  }

  @Override
  public void linkCompoundProviderServiceToFacetValue(final long compoundProviderServiceId, final long facetValueId) {
    super.getSession().doWork(doExecute("INSERT INTO facet_value_compound_service_provider (compound_service_provider_id ,facet_value_id ) VALUES (?, ?)", compoundProviderServiceId, facetValueId));
  }

  @Override
  public void unlinkCompoundProviderServiceToFacetValue(final long compoundProviderServiceId, final long facetValueId) {
    super.getSession().doWork(doExecute("DELETE FROM facet_value_compound_service_provider WHERE compound_service_provider_id = ? AND facet_value_id = ?", compoundProviderServiceId, facetValueId));
  }

  private Work doExecute(final String sql, final long compoundProviderServiceId, final long facetValueId) {
    return new Work() {
      @Override
      public void execute(Connection connection) throws SQLException {
        PreparedStatement ps = connection.prepareStatement(sql);
        ps.setLong(1, compoundProviderServiceId);
        ps.setLong(2, facetValueId);
        ps.executeUpdate();
      }
    };
  }


}
