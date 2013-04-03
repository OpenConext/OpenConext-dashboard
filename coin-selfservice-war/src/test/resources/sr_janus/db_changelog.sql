-- MySQL dump 10.13  Distrib 5.1.67, for redhat-linux-gnu (x86_64)
--
-- Host: db.surfconext.nl    Database: sr
-- ------------------------------------------------------
-- Server version	5.1.67-log

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `db_changelog`
--

DROP TABLE IF EXISTS `db_changelog`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `db_changelog` (
  `patch_number` int(11) NOT NULL,
  `branch` varchar(50) NOT NULL,
  `completed` int(11) NOT NULL,
  `filename` varchar(100) NOT NULL,
  `hash` varchar(32) NOT NULL,
  `description` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`patch_number`,`branch`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `db_changelog`
--

/*!40000 ALTER TABLE `db_changelog` DISABLE KEYS */;
INSERT INTO `db_changelog` VALUES (1,'default',1319553370,'patch-0001.sql','cc44f128','Create Tables for JANUS');
INSERT INTO `db_changelog` VALUES (2,'default',1319553370,'patch-0002.sql','c21bafcb','Initial users for JANUS');
INSERT INTO `db_changelog` VALUES (3,'default',1319553370,'patch-0003.php','ce9a2d0e','Add metadata_valid_until and metadata_cache_until fields for metadata refreshing.');
INSERT INTO `db_changelog` VALUES (4,'default',1319553371,'patch-0004.sql','b93a31b5','Turn off required signature validation for all SPs');
INSERT INTO `db_changelog` VALUES (5,'default',1319553371,'patch-0005.sql','d349d559','Primary key for JANUS entities (ported from manage/patch-002.sql)');
INSERT INTO `db_changelog` VALUES (6,'default',1322131998,'patch-0006.sql','82f24f5e','Add deleted column for deleting ARP rules in JANUS 1.10.0');
INSERT INTO `db_changelog` VALUES (7,'default',1322131998,'patch-0007.sql','2d5543cb','Set all entities to production workflow state');
INSERT INTO `db_changelog` VALUES (8,'default',1322131998,'patch-0008.php','c0943bec','Remove all usertypes other than technical and admin');
INSERT INTO `db_changelog` VALUES (9,'default',1324287820,'patch-0009.php','57f47b80','Update all NameIDFormat keys to urn:oasis:names:tc:SAML:2.0:nameid-format:unspecified');
INSERT INTO `db_changelog` VALUES (10,'default',1339665683,'patch-0010.sql','f832671a','');
INSERT INTO `db_changelog` VALUES (11,'default',1349409027,'patch-0011.sql','2da0be49','');
INSERT INTO `db_changelog` VALUES (12,'default',1349409027,'patch-0012.sql','3cf38d8c','');
INSERT INTO `db_changelog` VALUES (13,'default',1349409113,'patch-0013.php','9d981bbf','Convert allowed / blocked entities from remoteentityid to remoteeid (see BACKLOG-505)');
INSERT INTO `db_changelog` VALUES (14,'default',1362399481,'patch-0014.sql','64b09ad6','BACKLOG-675: Add manipulation field to entity');
INSERT INTO `db_changelog` VALUES (15,'default',1362479379,'patch-0015.php','e023ac79','Added persistent, transient and unspecified to all entities as valid NameIDFormats');
INSERT INTO `db_changelog` VALUES (16,'default',1363629727,'patch-0016.sql','6f93e1f8','Add index for allowed and blocked entities to improve performance (created by Geert vd Ploeg)');
/*!40000 ALTER TABLE `db_changelog` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-04-03  9:45:44
