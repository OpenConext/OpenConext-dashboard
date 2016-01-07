DROP TABLE IF EXISTS `facet`;
DROP TABLE IF EXISTS `facet_value`;
DROP TABLE IF EXISTS `facet_value_compound_service_provider`;

CREATE TABLE `facet` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `facet_parent_id` bigint(20) DEFAULT NULL,
  `name` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `facet_parent_id_foreign_key` (`facet_parent_id`),
  CONSTRAINT `facet_parent_id_foreign_key` FOREIGN KEY (`facet_parent_id`) REFERENCES `facet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `facet_value` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `facet_id` bigint(20) NOT NULL,
  `value` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  KEY `facet_id_foreign_key` (`facet_id`),
  CONSTRAINT `facet_id_foreign_key` FOREIGN KEY (`facet_id`) REFERENCES `facet` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

CREATE TABLE `facet_value_compound_service_provider` (
  `facet_value_id` bigint(20) NOT NULL,
  `compound_service_provider_id` bigint(20) NOT NULL,
  PRIMARY KEY (`facet_value_id`,`compound_service_provider_id`),
  KEY `facet_value_id_foreign_key` (`facet_value_id`),
  CONSTRAINT `facet_value_id_foreign_key` FOREIGN KEY (`facet_value_id`) REFERENCES `facet_value` (`id`),
  KEY `compound_service_provider_id_foreign_key` (`compound_service_provider_id`),
  CONSTRAINT `compound_service_provider_id_foreign_key` FOREIGN KEY (`compound_service_provider_id`) REFERENCES `compound_service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;


