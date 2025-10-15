CREATE DATABASE  IF NOT EXISTS `crmgym_fase2` /*!40100 DEFAULT CHARACTER SET utf8 COLLATE utf8_spanish2_ci */;
USE `crmgym_fase2`;
-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: localhost    Database: crmgym_fase2
-- ------------------------------------------------------
-- Server version	5.5.5-10.4.28-MariaDB

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `clients`
--

DROP TABLE IF EXISTS `clients`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `clients` (
  `document_id` int(11) NOT NULL,
  `email` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `phone_number` varchar(255) NOT NULL,
  `current_plan_id` int(11) DEFAULT NULL,
  PRIMARY KEY (`document_id`),
  UNIQUE KEY `UKsrv16ica2c1csub334bxjjb59` (`email`),
  KEY `FK2osgio9nx8apphkbiaqnnr8jk` (`current_plan_id`),
  CONSTRAINT `FK2osgio9nx8apphkbiaqnnr8jk` FOREIGN KEY (`current_plan_id`) REFERENCES `plans` (`id_plan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clients`
--

LOCK TABLES `clients` WRITE;
/*!40000 ALTER TABLE `clients` DISABLE KEYS */;
INSERT INTO `clients` VALUES (25588665,'manu.mail@mail.com',_binary '','Gonzales','Manuel','3455889952',4),(35687745,'joni.mail@mail.com',_binary '','Araujo','Jonathan','2634587995',1),(39456789,'clau_lopez@mail.com',_binary '','Lopez','Claudio','2364879654',1),(43256987,'mati@mail.com',_binary '','Ortega','Mateo','12547899555',2),(398745852,'fran@mail.com',_binary '\0','Gimenéz','Francisco','21024458889',4);
/*!40000 ALTER TABLE `clients` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historical_plans`
--

DROP TABLE IF EXISTS `historical_plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `historical_plans` (
  `id_historical` bigint(20) NOT NULL AUTO_INCREMENT,
  `end_date` date DEFAULT NULL,
  `is_active` bit(1) NOT NULL,
  `start_date` date NOT NULL,
  `document_id` int(11) NOT NULL,
  `id_plan` int(11) NOT NULL,
  PRIMARY KEY (`id_historical`),
  KEY `FK58gerebcsp9qlheo9yhwt7drr` (`document_id`),
  KEY `FKtn0gt2yg7hmdostvlrn651jsc` (`id_plan`),
  CONSTRAINT `FK58gerebcsp9qlheo9yhwt7drr` FOREIGN KEY (`document_id`) REFERENCES `clients` (`document_id`),
  CONSTRAINT `FKtn0gt2yg7hmdostvlrn651jsc` FOREIGN KEY (`id_plan`) REFERENCES `plans` (`id_plan`)
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historical_plans`
--

LOCK TABLES `historical_plans` WRITE;
/*!40000 ALTER TABLE `historical_plans` DISABLE KEYS */;
INSERT INTO `historical_plans` VALUES (1,'2025-10-13',_binary '\0','2025-08-12',39456789,2),(2,'2025-08-14',_binary '\0','2025-08-14',43256987,3),(3,'2025-10-15',_binary '\0','2025-08-14',43256987,1),(4,NULL,_binary '','2025-08-19',398745852,4),(5,'2025-10-13',_binary '\0','2025-10-13',35687745,2),(6,NULL,_binary '','2025-10-13',35687745,1),(7,NULL,_binary '','2025-10-13',39456789,1),(8,NULL,_binary '','2025-10-15',25588665,4),(9,NULL,_binary '','2025-10-15',43256987,2);
/*!40000 ALTER TABLE `historical_plans` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `payments`
--

DROP TABLE IF EXISTS `payments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `payments` (
  `payment_id` bigint(20) NOT NULL AUTO_INCREMENT,
  `base_amount` decimal(10,2) NOT NULL,
  `discount_applied` decimal(10,2) DEFAULT NULL,
  `final_amount` decimal(10,2) NOT NULL,
  `payment_date` date NOT NULL,
  `payment_method` enum('EFECTIVO','MERCADO_PAGO','PAYPAL','TARJETA_CREDITO','TARJETA_DEBITO','TRANSFERENCIA') NOT NULL,
  `payment_status` enum('CANCELADO','CONFIRMADO','PAGADO_VENCIDO','PENDIENTE','VENCIDO') NOT NULL,
  `period` date NOT NULL,
  `document_id` int(11) NOT NULL,
  PRIMARY KEY (`payment_id`),
  KEY `FKbg5a1ippx6sq4nr4w0ekfokco` (`document_id`),
  CONSTRAINT `FKbg5a1ippx6sq4nr4w0ekfokco` FOREIGN KEY (`document_id`) REFERENCES `clients` (`document_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `payments`
--

LOCK TABLES `payments` WRITE;
/*!40000 ALTER TABLE `payments` DISABLE KEYS */;
INSERT INTO `payments` VALUES (1,15000.00,0.00,15000.00,'2025-10-13','EFECTIVO','CONFIRMADO','2025-10-13',39456789),(2,13500.00,0.00,13500.00,'2025-10-13','TRANSFERENCIA','PENDIENTE','2025-10-01',43256987),(3,0.00,0.00,0.00,'2025-10-13','EFECTIVO','PENDIENTE','2025-10-01',35687745),(4,15000.00,0.00,15000.00,'2025-10-15','EFECTIVO','CONFIRMADO','2025-10-01',25588665);
/*!40000 ALTER TABLE `payments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `plans`
--

DROP TABLE IF EXISTS `plans`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `plans` (
  `id_plan` int(11) NOT NULL AUTO_INCREMENT,
  `days_enabled` int(11) NOT NULL,
  `hours_enabled` int(11) NOT NULL,
  `status` bit(1) DEFAULT NULL,
  `name_plan` varchar(255) NOT NULL,
  `notes` varchar(255) DEFAULT NULL,
  `value` decimal(38,2) NOT NULL,
  PRIMARY KEY (`id_plan`),
  UNIQUE KEY `UK6rnlq5lwwmf7hqed190yxumbo` (`name_plan`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `plans`
--

LOCK TABLES `plans` WRITE;
/*!40000 ALTER TABLE `plans` DISABLE KEYS */;
INSERT INTO `plans` VALUES (1,0,0,_binary '','Sin Plan','Para cambios de planes o clientes sin asignar',0.00),(2,1,2,_binary '','El día','Registro por el día',5000.00),(3,2,4,_binary '\0','Basico','Plan inicial',10000.00),(4,5,10,_binary '','Premium','Con instructor y rutina',45000.00);
/*!40000 ALTER TABLE `plans` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-10-15 14:41:14
