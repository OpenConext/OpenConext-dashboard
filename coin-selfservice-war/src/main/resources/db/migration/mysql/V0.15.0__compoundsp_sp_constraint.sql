
truncate table ss_idp_lmng_identifiers;
truncate table ss_sp_lmng_identifiers;
truncate table field_image;
truncate table field_string;
truncate table screenshot;
truncate table compound_service_provider;

ALTER TABLE `compound_service_provider` ADD UNIQUE (service_provider_entity_id);
