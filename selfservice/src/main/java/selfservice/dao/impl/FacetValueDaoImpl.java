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
package selfservice.dao.impl;

import static java.util.stream.Collectors.toList;

import java.util.List;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import selfservice.dao.FacetValueDaoCustom;
import selfservice.domain.MultilingualString;
import selfservice.domain.csa.InUseFacetValue;

@Repository
@Transactional
public class FacetValueDaoImpl implements FacetValueDaoCustom {

  @Autowired
  private EntityManager entityManager;

  @Override
  public void linkCspToFacetValue(long compoundProviderServiceId, long facetValueId) {
    performLinkAction("insert into facet_value_compound_service_provider (compound_service_provider_id ,facet_value_id ) VALUES (:compoundProviderServiceId, :facetValueId)", compoundProviderServiceId, facetValueId);
  }

  @Override
  public void unlinkCspFromFacetValue(long compoundProviderServiceId, long facetValueId) {
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
    String sql = "select compound_service_provider.service_provider_entity_id, localized_string.value from compound_service_provider " +
      "inner join facet_value_compound_service_provider on compound_service_provider.id = facet_value_compound_service_provider.compound_service_provider_id " +
      "inner join facet_value on facet_value_compound_service_provider.facet_value_id = facet_value.id " +
      "inner join multilingual_string on facet_value.multilingual_string_id = multilingual_string.id " +
      "inner join localized_string on multilingual_string.id = localized_string.multilingual_string_id " +
      "where facet_value.id = :identifier and localized_string.locale = :locale order by facet_value.id";
    return doFindInUseFacetValue(sql, facetValueId);
  }

  @Override
  public List<InUseFacetValue> findInUseFacet(long facetId) {
    String sql = "select compound_service_provider.service_provider_entity_id, localized_string.value from compound_service_provider " +
      "inner join facet_value_compound_service_provider on compound_service_provider.id = facet_value_compound_service_provider.compound_service_provider_id " +
      "inner join facet_value on facet_value_compound_service_provider.facet_value_id = facet_value.id " +
      "inner join multilingual_string on facet_value.multilingual_string_id = multilingual_string.id " +
      "inner join localized_string on multilingual_string.id = localized_string.multilingual_string_id " +
      "inner join facet on facet_value.facet_id = facet.id " +
      "where facet.id = :identifier and localized_string.locale = :locale order by facet_value.id";
    return doFindInUseFacetValue(sql, facetId);
  }

  private List<InUseFacetValue> doFindInUseFacetValue(String sql, long identifier) {
    @SuppressWarnings("unchecked")
    List<Object[]> dbResult = entityManager.createNativeQuery(sql)
      .setParameter("identifier", identifier)
      .setParameter("locale", MultilingualString.defaultLocale.toString()).getResultList();

    return dbResult.stream().map(s -> new InUseFacetValue((String) s[0], (String) s[1])).collect(toList());
  }

  private int performLinkAction(String sql, long compoundProviderServiceId, long facetValueId) {
    return entityManager.createNativeQuery(sql).
      setParameter("compoundProviderServiceId", compoundProviderServiceId).
      setParameter("facetValueId", facetValueId).
      executeUpdate();
  }

  private int performMassUnLinkAction(String sql, long identifier) {
    return entityManager.createNativeQuery(sql).
      setParameter("identifier", identifier).
      executeUpdate();
  }

}
