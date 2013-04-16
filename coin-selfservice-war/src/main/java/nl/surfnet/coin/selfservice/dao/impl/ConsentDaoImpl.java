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

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import nl.surfnet.coin.selfservice.dao.ConsentDao;

/**
SQL implementation of ConsentDao
 */
@Repository
public class ConsentDaoImpl implements ConsentDao {

  @Autowired
  private JdbcTemplate ebJdbcTemplate;

  @Override
  public Boolean mayHaveGivenConsent(String uuid, String spEntityId) {
    final String uuidSha1 = DigestUtils.shaHex(uuid);

    Object [] args = {uuidSha1, spEntityId};
    String sql = "SELECT count(*) FROM consent WHERE hashed_user_id = ? AND service_id = ?";
    try{
      final int i = this.ebJdbcTemplate.queryForInt(sql, args);
      if (i > 0) {
        return Boolean.TRUE;
      }
    } catch (EmptyResultDataAccessException e) {
      // move along, nothing to see here
    }
    return null;
  }



}
