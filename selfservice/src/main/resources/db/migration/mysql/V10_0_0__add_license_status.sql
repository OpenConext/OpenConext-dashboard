ALTER TABLE `compound_service_provider` ADD COLUMN license_status INT NOT NULL DEFAULT 3;
UPDATE `compound_service_provider` SET `license_status` = 0 WHERE  `lmng_id` IS NOT NULL;
