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

package selfservice.dao.impl;

import selfservice.dao.LmngIdentifierDao;
import selfservice.domain.csa.MappingEntry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

/**
 * Implementation for the DAO that stores identifiers for identityproviders for both local and LMNG scope.
 *
 */
@Repository
public class LmngIdentifierDaoImpl implements LmngIdentifierDao {

  private static final Logger log = LoggerFactory.getLogger(LmngIdentifierDaoImpl.class);

  private JdbcOperations jdbcTemplate;

  @Resource(name = "dataSource")
  public void setDataSource(DataSource dataSource) {
    jdbcTemplate = new JdbcTemplate(dataSource);
  }

  @Override
  public String getLmngIdForIdentityProviderId(String identityProviderId) {
    List<String> result = jdbcTemplate.query("SELECT lmngId FROM ss_idp_lmng_identifiers WHERE idpId = ?", new RowMapper<String>() {
      @Override
      public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
        return resultSet.getString("lmngId");
      }
    }, identityProviderId);
    if (result == null || result.size() == 0) {
      //log.debug("No LMNG ID found for IdP " + identityProviderId);
      return null;
    }
    log.debug("Got LMNG GUID '" + result.get(0) + "' for IdP " + identityProviderId);
    return result.get(0);
  }

  @Override
  public String getLmngIdForServiceProviderId(String spId) {
    List<String> result = jdbcTemplate.query("SELECT lmngId FROM ss_sp_lmng_identifiers WHERE spId = ?", new RowMapper<String>() {
      @Override
      public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
        return resultSet.getString("lmngId");
      }
    }, spId);
    if (result == null || result.size() == 0) {
      //log.debug("No LMNG ID found for SP " + spId);
      return null;
    }
    log.debug("Got LMNG GUID '" + result.get(0) + "' for SP " + spId);
    return result.get(0);
  }

  @Override
  public void saveOrUpdateLmngIdForServiceProviderId(String spId, String lmngId) {
    if (getLmngIdForServiceProviderId(spId)==null) {
      if (lmngId == null) {
        log.debug("No spId and lmngId passed. nothing to do");
      } else {
        jdbcTemplate.update(
            "INSERT INTO ss_sp_lmng_identifiers (spId,lmngId) VALUES(" +
                "?, ?)", spId, lmngId);
      }
    } else {
      if (lmngId == null) {
        jdbcTemplate.update(
            "DELETE from ss_sp_lmng_identifiers WHERE spId = ?", spId);
      } else {
        jdbcTemplate.update(
            "UPDATE ss_sp_lmng_identifiers SET lmngId = ? WHERE spId = ?", lmngId, spId);
      }
    }
  }

  @Override
  public void saveOrUpdateLmngIdForIdentityProviderId(String idpId, String lmngId) {
    if (getLmngIdForIdentityProviderId(idpId)==null) {
      if (lmngId == null) {
        log.debug("No idpId and lmngId passed. nothing to do");
      } else {
        jdbcTemplate.update(
            "INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId) VALUES(" +
                "?, ?)", idpId, lmngId);
      }
    } else {
      if (lmngId == null) {
        jdbcTemplate.update(
            "DELETE from ss_idp_lmng_identifiers WHERE idpId = ?", idpId);
      } else {
        jdbcTemplate.update(
            "UPDATE ss_idp_lmng_identifiers SET lmngId = ? WHERE idpId = ?", lmngId, idpId);
      }
    }
  }

  @Override
  public List<MappingEntry> findAllIdentityProviders() {
    List<MappingEntry> result = jdbcTemplate.query("SELECT idpId, lmngId FROM ss_idp_lmng_identifiers", new RowMapper<MappingEntry>() {

      @Override
      public MappingEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MappingEntry(rs.getString("idpId"), rs.getString("lmngId"));
      }
    });
    if (result == null) {
      result = Collections.emptyList();
    }
    log.debug("Got {} results when finding all identity providers", result.size());
    return result;

  }

  @Override
  public List<MappingEntry> findAllServiceProviders() {
    List<MappingEntry> result = jdbcTemplate.query("SELECT spId, lmngId FROM ss_sp_lmng_identifiers", new RowMapper<MappingEntry>() {

      @Override
      public MappingEntry mapRow(ResultSet rs, int rowNum) throws SQLException {
        return new MappingEntry(rs.getString("spId"), rs.getString("lmngId"));
      }
    });
    if (result == null) {
      result = Collections.emptyList();
    }
    log.debug("Got {} results when finding all service providers", result.size());
    return result;

  }

  /**
   * Construct a simple Map from a List of 'Map.Entry''s
   *
   * @param list the list to use as input
   * @param <K> Type of the key of the map
   * @param <V> Type of the value of the map
   */
  public static <K,V> Map<K,V> listOfEntriesToMap(List<Map.Entry<K, V>> list) {
    Map<K,V> map = new HashMap<K,V>();
    for (Map.Entry<K,V> e : list) {
      map.put(e.getKey(), e.getValue());
    }
    return map;
  }
}
