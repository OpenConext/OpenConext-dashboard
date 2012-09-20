drop table if exists ss_idp_lmng_identifiers;
create table ss_idp_lmng_identifiers (
  idpId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (idpId)
);

INSERT INTO ss_idp_lmng_identifiers (idpId,lmngId)
VALUES
  ('testId', 'lmng_organisationid');
