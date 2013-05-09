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
import nl.surfnet.coin.selfservice.domain.InUseFacetValue;
import nl.surfnet.coin.shared.service.GenericServiceHibernateImpl;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FacetValueDaoImpl extends GenericServiceHibernateImpl<FacetValue> implements FacetValueDao {

  public FacetValueDaoImpl() {
    super(FacetValue.class);
  }

  @Override
  public void linkCspToFacetValue(final long compoundProviderServiceId, final long facetValueId) {
    performLinkAction("insert into facet_value_compound_service_provider (compound_service_provider_id ,facet_value_id ) VALUES (:compoundProviderServiceId, :facetValueId)", compoundProviderServiceId, facetValueId);
  }

  @Override
  public void unlinkCspFromFacetValue(final long compoundProviderServiceId, final long facetValueId) {
    performLinkAction("delete from facet_value_compound_service_provider where compound_service_provider_id = :compoundProviderServiceId AND facet_value_id = :facetValueId", compoundProviderServiceId, facetValueId);
  }

  @Override
  public void unlinkAllCspFromFacetValue(long facetValueId) {
    performMassUnLinkAction("delete from facet_value_compound_service_provider where facet_value_id = :identifier", facetValueId);
  }

  @Override
  public void unlinkAllCspFromFacet(long facetId) {
    performMassUnLinkAction("delete from facet_value_compound_service_provider where facet_value_id in (select id from facet_value where facet_id = :identifier)", facetId);
  }

  @Override
  public List<InUseFacetValue> findInUseFacetValues(long facetValueId) {
    String sql = "select compound_service_provider.service_provider_entity_id, facet_value.value from compound_service_provider " +
            "inner join facet_value_compound_service_provider on compound_service_provider.id = facet_value_compound_service_provider.compound_service_provider_id " +
            "inner join facet_value on facet_value_compound_service_provider.facet_value_id = facet_value.id " +
            "where facet_value.id = :identifier order by facet_value.id";
    return doFindInUseFacetValue(sql, facetValueId);
  }

  @Override
  public List<InUseFacetValue> findInUseFacet(long facetId) {
    String sql = "select compound_service_provider.service_provider_entity_id, facet_value.value from compound_service_provider " +
            "inner join facet_value_compound_service_provider on compound_service_provider.id = facet_value_compound_service_provider.compound_service_provider_id " +
            "inner join facet_value on facet_value_compound_service_provider.facet_value_id = facet_value.id " +
            "inner join facet on facet_value.facet_id = facet.id " +
            "where facet.id = :identifier order by facet_value.id";
    return doFindInUseFacetValue(sql, facetId);
  }

  private List<InUseFacetValue> doFindInUseFacetValue(String sql, long identifier) {
    List<Object[]> dbResult = getSession().createSQLQuery(
            sql).setLong("identifier", identifier).list();
    List<InUseFacetValue> result = new ArrayList<InUseFacetValue>();
    for (Object[] s : dbResult) {
      result.add(new InUseFacetValue((String) s[0], (String) s[1]));
    }
    return result;
  }

  private int performLinkAction(String sql, long compoundProviderServiceId, long facetValueId) {
    return getSession().createSQLQuery(sql).
            setLong("compoundProviderServiceId", compoundProviderServiceId).
            setLong("facetValueId", facetValueId).
            executeUpdate();
  }

  private int performMassUnLinkAction(String sql, long identifier) {
    return getSession().createSQLQuery(sql).
            setLong("identifier", identifier).
            executeUpdate();
  }

}
