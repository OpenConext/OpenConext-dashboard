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

import java.util.List;

import nl.surfnet.coin.selfservice.dao.StatisticDao;
import nl.surfnet.coin.selfservice.domain.ChartSerie;
import nl.surfnet.coin.selfservice.domain.IdentityProviderRepresenter;

import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Repository;

/**
 * SQL implementation for the statistic service
 */
@Repository
@SuppressWarnings("unchecked")
public class MockStatisticDaoImpl implements StatisticDao {

  private ObjectMapper objectMapper = new ObjectMapper().enable(DeserializationConfig.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
      .setSerializationInclusion(JsonSerialize.Inclusion.NON_NULL);

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay(String idpEntityId) {
    return (List<ChartSerie>) parseJsonData("stat-json/stats_mockidp.json");
  }

  @Override
  public List<ChartSerie> getLoginsPerSpPerDay() {
    return (List<ChartSerie>) parseJsonData("stat-json/stats.json");
  }

  @Override
  public List<IdentityProviderRepresenter> getIdpLoginIdentifiers() {
    return (List<IdentityProviderRepresenter>) parseJsonData("stat-json/stats-idps.json",
        new TypeReference<List<IdentityProviderRepresenter>>() {
        });
  }

  private Object parseJsonData(String jsonFile) {
    return parseJsonData(jsonFile, new TypeReference<List<ChartSerie>>() {
    });
  }

  @SuppressWarnings("rawtypes")
  private Object parseJsonData(String jsonFile,  TypeReference typeReference) {
    try {
      return objectMapper.readValue(new ClassPathResource(jsonFile).getInputStream(), typeReference);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

}
