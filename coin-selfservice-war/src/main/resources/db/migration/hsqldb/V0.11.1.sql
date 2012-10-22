drop table if exists ss_sp_lmng_identifiers;
create table ss_sp_lmng_identifiers (
  spId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (spId)
);


INSERT INTO ss_sp_lmng_identifiers (spId,lmngId)
VALUES
  ('http://www.google.com', '8833CEAE-960C-E211-B6B9-005056950050');

  