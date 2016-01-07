
DELETE FROM ss_idp_lmng_identifiers;
DELETE FROM ss_sp_lmng_identifiers;
DELETE FROM field_image;
DELETE FROM field_string;
DELETE FROM screenshot;
DELETE FROM compound_service_provider;

ALTER TABLE `compound_service_provider` ADD UNIQUE (service_provider_entity_id);
