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
-- Table structure for table `janus__blockedEntity`
--

DROP TABLE IF EXISTS `janus__blockedEntity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `janus__blockedEntity` (
  `eid` int(11) NOT NULL,
  `revisionid` int(11) NOT NULL,
  `remoteentityid` text NOT NULL,
  `remoteeid` int(11) NOT NULL,
  `created` char(25) NOT NULL,
  `ip` char(15) NOT NULL,
  KEY `remoteeid` (`remoteeid`),
  KEY `eid_revision` (`eid`,`revisionid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `janus__blockedEntity`
--

/*!40000 ALTER TABLE `janus__blockedEntity` DISABLE KEYS */;
INSERT INTO `janus__blockedEntity` VALUES (75,10,'https://www.surfmedia.nl/app/serviceprovider/metadata',0,'2011-07-13T13:08:51+02:00','2001:610:508:10');
INSERT INTO `janus__blockedEntity` VALUES (75,10,'http://m.surfmedia.nl/login/metadata/',0,'2011-07-13T13:08:51+02:00','2001:610:508:10');
INSERT INTO `janus__blockedEntity` VALUES (73,9,'https://www.surfmedia.nl/app/serviceprovider/metadata',0,'2011-07-13T13:09:35+02:00','2001:610:508:10');
INSERT INTO `janus__blockedEntity` VALUES (73,9,'http://m.surfmedia.nl/login/metadata/',0,'2011-07-13T13:09:35+02:00','2001:610:508:10');
INSERT INTO `janus__blockedEntity` VALUES (75,11,'https://www.surfmedia.nl/app/serviceprovider/metadata',0,'2011-08-16T14:33:00+02:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (75,11,'http://m.surfmedia.nl/login/metadata/',0,'2011-08-16T14:33:00+02:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (75,12,'https://www.surfmedia.nl/app/serviceprovider/metadata',70,'2011-08-16T14:34:30+02:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (75,12,'http://m.surfmedia.nl/login/metadata/',71,'2011-08-16T14:34:30+02:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (73,10,'https://www.surfmedia.nl/app/serviceprovider/metadata',0,'2011-10-31T12:37:06+01:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (73,10,'http://m.surfmedia.nl/login/metadata/',0,'2011-10-31T12:37:06+01:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (73,11,'https://www.surfmedia.nl/app/serviceprovider/metadata',70,'2011-10-31T12:37:53+01:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (73,11,'http://m.surfmedia.nl/login/metadata/',71,'2011-10-31T12:37:53+01:00','2001:610:188:43');
INSERT INTO `janus__blockedEntity` VALUES (149,5,'https://profile.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp',0,'2011-12-10T01:03:57+01:00','195.169.126.123');
INSERT INTO `janus__blockedEntity` VALUES (149,6,'https://profile.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp',0,'2011-12-10T11:31:55+01:00','195.169.126.112');
INSERT INTO `janus__blockedEntity` VALUES (149,7,'https://profile.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp',0,'2011-12-10T11:33:41+01:00','195.169.126.112');
INSERT INTO `janus__blockedEntity` VALUES (149,8,'https://profile.surfconext.nl/simplesaml/module.php/saml/sp/metadata.php/default-sp',69,'2011-12-10T11:35:21+01:00','195.169.126.112');
INSERT INTO `janus__blockedEntity` VALUES (81,10,'google.com/a/sigs.surfnet.nl',0,'2012-02-01T11:45:07+01:00','195.169.126.103');
INSERT INTO `janus__blockedEntity` VALUES (81,11,'google.com/a/sigs.surfnet.nl',140,'2012-02-01T11:45:23+01:00','195.169.126.103');
INSERT INTO `janus__blockedEntity` VALUES (175,3,'https://teams.surfconext.nl/shibboleth',2,'2012-03-09T15:05:41+01:00','192.87.109.175');
INSERT INTO `janus__blockedEntity` VALUES (170,5,'http://sts-test.windesheim.nl/adfs/services/trust',183,'2012-04-13T11:27:14+02:00','192.87.109.69');
/*!40000 ALTER TABLE `janus__blockedEntity` ENABLE KEYS */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2013-04-03  9:45:45
