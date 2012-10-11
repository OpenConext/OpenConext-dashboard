drop table if exists ss_sp_lmng_identifiers;
create table ss_sp_lmng_identifiers (
  spId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (spId)
);


INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('testId', 'lmng_serviceid1');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('testId2', 'lmng_serviceid2');
  
INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://rave.beta.surfnet.nl', 'lmng_serviceid2');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://localhost:3000/saml2', 'lmng_serviceid3');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://profile.dev.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/diy-sp', 'lmng_serviceid4');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://example.com/sp-entity-id', 'lmng_serviceid5');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('string2', 'lmng_serviceid6');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('sp3', 'lmng_serviceid7');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('Foodle External Groups', 'lmng_serviceid8');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://beta.foodl.org/gadget/activity.xml', 'lmng_serviceid8');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://gadgets.jasha.eu', 'lmng_serviceid9');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('google.com/a/apps.surfnet.nl', 'lmng_serviceid10');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://grouper.dom.loc/shibboleth', 'lmng_serviceid11');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('LinkedIn', 'lmng_serviceid12');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://fkooman.pagekite.me/client', 'lmng_serviceid13');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('SNlabs', 'lmng_serviceid14');

INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('https://engine.dev.surfconext.nl/authentication/sp/metadata', 'lmng_serviceid15');
  
INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://www.google.com', '8833CEAE-960C-E211-B6B9-005056950050');

  