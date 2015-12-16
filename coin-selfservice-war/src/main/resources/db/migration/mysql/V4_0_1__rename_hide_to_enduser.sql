ALTER TABLE `compound_service_provider` CHANGE COLUMN `hide_in_public_csa` `available_for_end_user` boolean;
ALTER TABLE `compound_service_provider` DROP COLUMN `hide_in_protected_csa`;
UPDATE `compound_service_provider` SET `available_for_end_user` = NOT(`available_for_end_user`);