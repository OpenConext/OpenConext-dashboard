package db.migration.mysql;

import java.math.BigInteger;
import java.util.List;

import org.flywaydb.core.api.migration.spring.SpringJdbcMigration;
import org.springframework.jdbc.core.JdbcTemplate;

import selfservice.domain.csa.Field;

public class V15_0_0__AddInterfederationFields implements SpringJdbcMigration {

  @Override
  public void migrate(JdbcTemplate jdbcTemplate) throws Exception {
    ;
    String query = "select id from compound_service_provider"; 
    List<BigInteger> cspIds = jdbcTemplate.queryForList(query, BigInteger.class);
    for (BigInteger id : cspIds) {
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
      		"values (?, ?, NULL, ?)", Field.Key.INTERFED_SOURCE.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.PRIVACY_STATEMENT_URL_EN.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.PRIVACY_STATEMENT_URL_NL.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.REGISTRATION_INFO_URL.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.REGISTRATION_POLICY_URL_EN.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
          "values (?, ?, NULL, ?)", Field.Key.REGISTRATION_POLICY_URL_NL.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
              "values (?, ?, NULL, ?)", Field.Key.ENTITY_CATEGORIES_1.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
              "values (?, ?, NULL, ?)", Field.Key.ENTITY_CATEGORIES_2.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
      jdbcTemplate.update("insert into field_string (field_key, field_source, field_value, compound_service_provider_id)" +
              "values (?, ?, NULL, ?)", Field.Key.PUBLISH_IN_EDUGAIN_DATE.ordinal(), Field.Source.SURFCONEXT.ordinal(), id);
    }
  }
}
