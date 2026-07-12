-- V1__Initial_Schema.sql
-- Creación de las tablas iniciales con sus llaves primarias, foráneas e índices.

-- 1. Tabla PLANS
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- 2. Tabla CLIENTS
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

-- 3. Tabla HISTORICAL_PLANS
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;

-- 4. Tabla PAYMENTS
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
  UNIQUE KEY `uc_client_period` (`document_id`,`period`),
  CONSTRAINT `FKbg5a1ippx6sq4nr4w0ekfokco` FOREIGN KEY (`document_id`) REFERENCES `clients` (`document_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_spanish2_ci;
