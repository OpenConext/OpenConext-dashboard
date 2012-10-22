DROP TABLE IF EXISTS `compound_service_provider`;

CREATE TABLE `compound_service_provider` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `lmng_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  `service_provider_entity_id` varchar(255) COLLATE utf8_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `field_image`;

CREATE TABLE `field_image` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_key` int(11) DEFAULT NULL,
  `field_source` int(11) DEFAULT NULL,
  `field_image` longblob,
  `compound_service_provider_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK294389D6D151AC27` (`compound_service_provider_id`),
  CONSTRAINT `FK294389D6D151AC27` FOREIGN KEY (`compound_service_provider_id`) REFERENCES `compound_service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `field_string`;

CREATE TABLE `field_string` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_key` int(11) DEFAULT NULL,
  `field_source` int(11) DEFAULT NULL,
  `field_value` varchar(1023) COLLATE utf8_unicode_ci DEFAULT NULL,
  `compound_service_provider_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FK10A88EF6D151AC27` (`compound_service_provider_id`),
  CONSTRAINT `FK10A88EF6D151AC27` FOREIGN KEY (`compound_service_provider_id`) REFERENCES `compound_service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;

DROP TABLE IF EXISTS `screenshot`;

CREATE TABLE `screenshot` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `field_image` longblob,
  `compound_service_provider_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKE72D8566D151AC27` (`compound_service_provider_id`),
  CONSTRAINT `FKE72D8566D151AC27` FOREIGN KEY (`compound_service_provider_id`) REFERENCES `compound_service_provider` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;
