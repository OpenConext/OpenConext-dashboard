drop table if exists ss_sp_lmng_identifiers;
create table ss_sp_lmng_identifiers (
  spId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (spId)
);


INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://www.google.com', '{8833CEAE-960C-E211-B6B9-005056950050}');
INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('google.com/a/apps.surfnet.nl', '{41D136D1-3819-E211-B687-005056950050}');
INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://vomanage.dev.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp', '{26FF7404-970C-E211-B6B9-005056950050}');
INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://rave.beta.surfnet.nl', '{26FF7404-970C-E211-B6B9-005056950050}');
