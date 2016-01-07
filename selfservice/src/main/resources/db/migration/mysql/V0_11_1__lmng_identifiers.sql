drop table if exists ss_sp_lmng_identifiers;
create table ss_sp_lmng_identifiers (
  spId varchar(255) not null,
  lmngId varchar(255) not null,
  primary key (spId)
);
