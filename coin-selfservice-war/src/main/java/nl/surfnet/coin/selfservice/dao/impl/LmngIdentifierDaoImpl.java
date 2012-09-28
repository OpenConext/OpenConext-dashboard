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

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import nl.surfnet.coin.selfservice.dao.LmngIdentifierDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

/**
 * Implementation for the DAO that stores identifiers for identityproviders for both local and LMNG scope.
 *
 */
@Repository
public class LmngIdentifierDaoImpl implements LmngIdentifierDao {
  
  private static final Logger log = LoggerFactory.getLogger(LmngIdentifierDaoImpl.class);

  private JdbcOperations jdbcTemplate;

  @Resource(name = "selfserviceDataSource")
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
      log.debug("No LMNG results found for IdP " + identityProviderId);
      return null;
    }
    return result.get(0);
  }

  @Override
  public String getIdentityProviderIdForLmngId(String lnmgId) {
    List<String> result = jdbcTemplate.query("SELECT idpId FROM ss_idp_lmng_identifiers WHERE lmngId = ?", new RowMapper<String>() {
      @Override
      public String mapRow(final ResultSet resultSet, final int i) throws SQLException {
        return resultSet.getString("idpId");
      }
    }, lnmgId);
    if (result == null || result.size() == 0) {
      log.debug("No identityProviderId found for LmngId " + lnmgId);
      return null;
    }
    return result.get(0);
  }

}
