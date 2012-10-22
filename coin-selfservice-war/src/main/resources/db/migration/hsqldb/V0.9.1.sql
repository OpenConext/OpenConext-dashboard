drop table if exists ss_idp_lmng_identifiers;
create table ss_idp_lmng_identifiers (
  idpId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (idpId)
);

INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('mock-institution-id', '{ED3207DC-1910-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('SURFnetGuests', '{268835E8-8E0F-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('idpidentity1', '{26E880E1-8E0F-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('string', '{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('http://mock-idp', '{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('SURFmarket', '7E7326CA-1A10-DC11-A6C7-0019B9DE3AA4');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('SURFnet', '837326CA-1A10-DC11-A6C7-0019B9DE3AA4');
