alter table compound_service_provider add column license_status_char varchar(100);

update compound_service_provider set license_status_char = 'HAS_LICENSE_SURFMARKET' where license_status = 0;
update compound_service_provider set license_status_char = 'HAS_LICENSE_SP' where license_status = 1;
update compound_service_provider set license_status_char = 'NOT_NEEDED' where license_status = 2;
update compound_service_provider set license_status_char = 'NOT_NEEDED' where license_status = 3;
update compound_service_provider set license_status_char = 'UNKNOWN' where license_status = 4;

alter table compound_service_provider drop column license_status;
alter table compound_service_provider change column license_status_char license_status varchar(100) not null;