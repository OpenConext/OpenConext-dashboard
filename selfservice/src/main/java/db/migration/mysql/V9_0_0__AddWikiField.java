package db.migration.mysql;

import java.math.BigInteger;
import java.util.List;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import selfservice.domain.csa.Field;

public class V9_0_0__AddWikiField implements SpringJdbcMigration {

  @Override
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    ;
    String query = "select id from compound_service_provider"; 
    List<BigInteger> cspIds = jdbcTemplate.queryForList(query, BigInteger.class);
    for (BigInteger id : cspIds) {
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
      		"values (?, ?, NULL, ?)", Field.Key.WIKI_URL_EN.ordinal(), Field.Source.DISTRIBUTIONCHANNEL.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.WIKI_URL_NL.ordinal(), Field.Source.DISTRIBUTIONCHANNEL.ordinal(), id);
    }
  }
}
