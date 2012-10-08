drop table if exists ss_idp_lmng_identifiers;
create table ss_idp_lmng_identifiers (
  idpId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (idpId)
);

INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('testId', 'lmng_organisationid');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('SURFnetGuests', '{268835E8-8E0F-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('idpentity1', '{26E880E1-8E0F-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('idpentity3', '{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}');
INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('http://mock-idp', '{AF1F54D8-1B10-DC11-A6C7-0019B9DE3AA4}');