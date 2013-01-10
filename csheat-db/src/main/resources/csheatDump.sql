CREATE DATABASE  IF NOT EXISTS `csheat` /*!40100 DEFAULT CHARACTER SET utf8 */;
USE `csheat`;
-- MySQL dump 10.13  Distrib 5.5.24, for osx10.5 (i386)
--
-- Host: localhost    Database: csheat
-- ------------------------------------------------------
-- Server version	5.5.29

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
-- Table structure for table `APP`
--

DROP TABLE IF EXISTS `APP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `APP` (
  `ID` bigint(20) NOT NULL,
  `CMDLINE` tinyint(1) DEFAULT '0',
  `GUI` tinyint(1) DEFAULT '0',
  `NAME` varchar(255) DEFAULT NULL,
  `VERSION` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `APP`
--

LOCK TABLES `APP` WRITE;
/*!40000 ALTER TABLE `APP` DISABLE KEYS */;
INSERT INTO `APP` VALUES (1,0,0,'ssh','*'),(2,0,0,'X11','*'),(3,0,0,'mount','*'),(4,0,0,'samba','*');
/*!40000 ALTER TABLE `APP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEAT`
--

DROP TABLE IF EXISTS `CHEAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEAT` (
  `ID` bigint(20) NOT NULL,
  `GUI` tinyint(1) DEFAULT '0',
  `APP_ID` bigint(20) DEFAULT NULL,
  `OS_ID` bigint(20) DEFAULT NULL,
  `DUPLICATEOF_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CHEAT_DUPLICATEOF_ID` (`DUPLICATEOF_ID`),
  KEY `FK_CHEAT_OS_ID` (`OS_ID`),
  KEY `FK_CHEAT_APP_ID` (`APP_ID`),
  CONSTRAINT `FK_CHEAT_APP_ID` FOREIGN KEY (`APP_ID`) REFERENCES `APP` (`ID`),
  CONSTRAINT `FK_CHEAT_DUPLICATEOF_ID` FOREIGN KEY (`DUPLICATEOF_ID`) REFERENCES `CHEAT` (`ID`),
  CONSTRAINT `FK_CHEAT_OS_ID` FOREIGN KEY (`OS_ID`) REFERENCES `OS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEAT`
--

LOCK TABLES `CHEAT` WRITE;
/*!40000 ALTER TABLE `CHEAT` DISABLE KEYS */;
INSERT INTO `CHEAT` VALUES (1,0,1,1,NULL),(2,0,2,2,NULL),(3,0,3,1,NULL),(4,0,3,2,NULL),(5,0,4,2,NULL);
/*!40000 ALTER TABLE `CHEAT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEATSHEET`
--

DROP TABLE IF EXISTS `CHEATSHEET`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEATSHEET` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `FORMAT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CHEATSHEET_FORMAT_ID` (`FORMAT_ID`),
  CONSTRAINT `FK_CHEATSHEET_FORMAT_ID` FOREIGN KEY (`FORMAT_ID`) REFERENCES `CHEATSHEETFORMAT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEATSHEET`
--

LOCK TABLES `CHEATSHEET` WRITE;
/*!40000 ALTER TABLE `CHEATSHEET` DISABLE KEYS */;
/*!40000 ALTER TABLE `CHEATSHEET` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEATSHEETFORMAT`
--

DROP TABLE IF EXISTS `CHEATSHEETFORMAT`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEATSHEETFORMAT` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEATSHEETFORMAT`
--

LOCK TABLES `CHEATSHEETFORMAT` WRITE;
/*!40000 ALTER TABLE `CHEATSHEETFORMAT` DISABLE KEYS */;
/*!40000 ALTER TABLE `CHEATSHEETFORMAT` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEATSHEET_CHEATVERSION`
--

DROP TABLE IF EXISTS `CHEATSHEET_CHEATVERSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEATSHEET_CHEATVERSION` (
  `cheatSheets_ID` bigint(20) NOT NULL,
  `cheats_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`cheatSheets_ID`,`cheats_ID`),
  KEY `FK_CHEATSHEET_CHEATVERSION_cheats_ID` (`cheats_ID`),
  CONSTRAINT `FK_CHEATSHEET_CHEATVERSION_cheatSheets_ID` FOREIGN KEY (`cheatSheets_ID`) REFERENCES `CHEATSHEET` (`ID`),
  CONSTRAINT `FK_CHEATSHEET_CHEATVERSION_cheats_ID` FOREIGN KEY (`cheats_ID`) REFERENCES `CHEATVERSION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEATSHEET_CHEATVERSION`
--

LOCK TABLES `CHEATSHEET_CHEATVERSION` WRITE;
/*!40000 ALTER TABLE `CHEATSHEET_CHEATVERSION` DISABLE KEYS */;
/*!40000 ALTER TABLE `CHEATSHEET_CHEATVERSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEATVERSION`
--

DROP TABLE IF EXISTS `CHEATVERSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEATVERSION` (
  `ID` bigint(20) NOT NULL,
  `CONTENT` varchar(255) DEFAULT NULL,
  `CREATED` date DEFAULT NULL,
  `DEPENDS` varchar(255) DEFAULT NULL,
  `DESCRIPTION` varchar(255) DEFAULT NULL,
  `PURPOSE` varchar(255) DEFAULT NULL,
  `CHEAT_ID` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `FK_CHEATVERSION_CHEAT_ID` (`CHEAT_ID`),
  CONSTRAINT `FK_CHEATVERSION_CHEAT_ID` FOREIGN KEY (`CHEAT_ID`) REFERENCES `CHEAT` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEATVERSION`
--

LOCK TABLES `CHEATVERSION` WRITE;
/*!40000 ALTER TABLE `CHEATVERSION` DISABLE KEYS */;
INSERT INTO `CHEATVERSION` VALUES (1,'sudo ssh -X 192.168.1.102 -l pi','2012-12-24','http://xquartz.macosforge.org','initial version','remote with X11',1),(2,'startlxde','0000-00-00',NULL,'initial version','remote start',2),(3,'mount -t smbfs //192.168.1.102/public/media/data/MOVIES /Users/ares/Movies/Pi/',NULL,NULL,'initial version','remote mounting via smbfs',3),(4,'sudo mount /dev/sda1 /media/data/ -o umask=002',NULL,NULL,'initial version','mounting ntfs for sharing over samba',4),(5,'sudo /etc/init.d/samba restart',NULL,'apt-get install samba','initial version','restart samba service',5);
/*!40000 ALTER TABLE `CHEATVERSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `CHEAT_CHEATVERSION`
--

DROP TABLE IF EXISTS `CHEAT_CHEATVERSION`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `CHEAT_CHEATVERSION` (
  `Cheat_ID` bigint(20) NOT NULL,
  `versions_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`Cheat_ID`,`versions_ID`),
  KEY `FK_CHEAT_CHEATVERSION_versions_ID` (`versions_ID`),
  CONSTRAINT `FK_CHEAT_CHEATVERSION_Cheat_ID` FOREIGN KEY (`Cheat_ID`) REFERENCES `CHEAT` (`ID`),
  CONSTRAINT `FK_CHEAT_CHEATVERSION_versions_ID` FOREIGN KEY (`versions_ID`) REFERENCES `CHEATVERSION` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `CHEAT_CHEATVERSION`
--

LOCK TABLES `CHEAT_CHEATVERSION` WRITE;
/*!40000 ALTER TABLE `CHEAT_CHEATVERSION` DISABLE KEYS */;
INSERT INTO `CHEAT_CHEATVERSION` VALUES (1,1),(2,2),(3,3),(4,4),(5,5);
/*!40000 ALTER TABLE `CHEAT_CHEATVERSION` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `OS`
--

DROP TABLE IF EXISTS `OS`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OS` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(255) DEFAULT NULL,
  `VERSION` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `OS`
--

LOCK TABLES `OS` WRITE;
/*!40000 ALTER TABLE `OS` DISABLE KEYS */;
INSERT INTO `OS` VALUES (1,'MacOS X','10.8'),(2,'wheezy Debian@Pi','3.1.9+');
/*!40000 ALTER TABLE `OS` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `OS_APP`
--

DROP TABLE IF EXISTS `OS_APP`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `OS_APP` (
  `apps_ID` bigint(20) NOT NULL,
  `os_ID` bigint(20) NOT NULL,
  PRIMARY KEY (`apps_ID`,`os_ID`),
  KEY `FK_OS_APP_os_ID` (`os_ID`),
  CONSTRAINT `FK_OS_APP_apps_ID` FOREIGN KEY (`apps_ID`) REFERENCES `APP` (`ID`),
  CONSTRAINT `FK_OS_APP_os_ID` FOREIGN KEY (`os_ID`) REFERENCES `OS` (`ID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `OS_APP`
--

LOCK TABLES `OS_APP` WRITE;
/*!40000 ALTER TABLE `OS_APP` DISABLE KEYS */;
INSERT INTO `OS_APP` VALUES (1,1),(3,1),(2,2),(3,2),(4,2);
/*!40000 ALTER TABLE `OS_APP` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `SEQUENCE`
--

DROP TABLE IF EXISTS `SEQUENCE`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `SEQUENCE` (
  `SEQ_NAME` varchar(50) NOT NULL,
  `SEQ_COUNT` decimal(38,0) DEFAULT NULL,
  PRIMARY KEY (`SEQ_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `SEQUENCE`
--

LOCK TABLES `SEQUENCE` WRITE;
/*!40000 ALTER TABLE `SEQUENCE` DISABLE KEYS */;
INSERT INTO `SEQUENCE` VALUES ('SEQ_GEN',0);
/*!40000 ALTER TABLE `SEQUENCE` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-12-26  2:55:00
