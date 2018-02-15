DELETE FROM field_image WHERE field_source = 0;
DELETE FROM field_string WHERE field_source = 0;
ALTER TABLE compound_service_provider DROP COLUMN lmng_id;
DROP TABLE ss_idp_lmng_identifiers;
DROP TABLE ss_sp_lmng_identifiers;
