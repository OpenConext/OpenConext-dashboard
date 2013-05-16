DROP TABLE IF EXISTS `localized_string`;
DROP TABLE IF EXISTS `multilingual_string`;

CREATE TABLE `multilingual_string` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `localized_string` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `multilingual_string_id` bigint(20) NOT NULL,
  `value` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `locale` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `multilingual_string_id_ls_foreign_key` (`multilingual_string_id`),
  CONSTRAINT `multilingual_string_id_ls_foreign_key` FOREIGN KEY (`multilingual_string_id`) REFERENCES `multilingual_string` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

TRUNCATE TABLE `facet_value_compound_service_provider`;
TRUNCATE TABLE `facet_value`;
ALTER TABLE `facet_value` DROP `value`;
ALTER TABLE `facet_value` ADD `multilingual_string_id` bigint(20) NOT NULL;
ALTER TABLE `facet_value` ADD KEY `multilingual_string_id_fv_foreign_key` (`multilingual_string_id`);
ALTER TABLE `facet_value` ADD CONSTRAINT `multilingual_string_id_fv_foreign_key` FOREIGN KEY (`multilingual_string_id`) REFERENCES `multilingual_string` (`id`);

TRUNCATE TABLE `facet`;
ALTER TABLE `facet` DROP `name`;
ALTER TABLE `facet` ADD `multilingual_string_id` bigint(20) NOT NULL;
ALTER TABLE `facet` ADD KEY `multilingual_string_id_f_foreign_key` (`multilingual_string_id`);
ALTER TABLE `facet` ADD CONSTRAINT `multilingual_string_f_id_foreign_key` FOREIGN KEY (`multilingual_string_id`) REFERENCES `multilingual_string` (`id`);


