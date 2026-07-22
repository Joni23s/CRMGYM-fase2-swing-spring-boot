-- V3__Refactor_Entities_Schema.sql
-- Refactorización del esquema de base de datos para CRMGYM:
-- 1. Introducción de clave primaria autogenerada (id_client) en la tabla 'clients'.
-- 2. Modificación de 'document_id' (DNI) como índice único en 'clients'.
-- 3. Actualización de relaciones e índices foráneos en 'historical_plans' y 'payments'.
-- 4. Adición de la relación directa 'plan_id' en la tabla 'payments' para auditoría de facturación.

-- 1. Quitar temporalmente las restricciones de clave foránea existentes
ALTER TABLE `clients` DROP FOREIGN KEY `FK2osgio9nx8apphkbiaqnnr8jk`;
ALTER TABLE `historical_plans` DROP FOREIGN KEY `FK58gerebcsp9qlheo9yhwt7drr`;
ALTER TABLE `payments` DROP FOREIGN KEY `FKbg5a1ippx6sq4nr4w0ekfokco`;

-- 2. Modificar tabla CLIENTS: agregar id_client autoincremental como nueva Clave Primaria
ALTER TABLE `clients` DROP PRIMARY KEY;
ALTER TABLE `clients` ADD COLUMN `id_client` BIGINT NOT NULL AUTO_INCREMENT FIRST, ADD PRIMARY KEY (`id_client`);
ALTER TABLE `clients` ADD CONSTRAINT `UK_clients_document_id` UNIQUE (`document_id`);
ALTER TABLE `clients` ADD CONSTRAINT `FK2osgio9nx8apphkbiaqnnr8jk` FOREIGN KEY (`current_plan_id`) REFERENCES `plans` (`id_plan`);

-- 3. Modificar tabla HISTORICAL_PLANS: migrar la clave foránea del cliente a client_id
ALTER TABLE `historical_plans` ADD COLUMN `client_id` BIGINT DEFAULT NULL AFTER `id_historical`;
UPDATE `historical_plans` hp JOIN `clients` c ON hp.`document_id` = c.`document_id` SET hp.`client_id` = c.`id_client`;
ALTER TABLE `historical_plans` MODIFY COLUMN `client_id` BIGINT NOT NULL;
ALTER TABLE `historical_plans` DROP COLUMN `document_id`;
ALTER TABLE `historical_plans` ADD CONSTRAINT `FK_historical_plans_client` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id_client`);

-- 4. Modificar tabla PAYMENTS: migrar a client_id y vincular plan_id del momento del cobro
ALTER TABLE `payments` DROP KEY `uc_client_period`;
ALTER TABLE `payments` ADD COLUMN `client_id` BIGINT DEFAULT NULL AFTER `payment_id`;
ALTER TABLE `payments` ADD COLUMN `plan_id` INT(11) DEFAULT NULL AFTER `client_id`;

UPDATE `payments` p JOIN `clients` c ON p.`document_id` = c.`document_id` SET p.`client_id` = c.`id_client`, p.`plan_id` = c.`current_plan_id`;
ALTER TABLE `payments` MODIFY COLUMN `client_id` BIGINT NOT NULL;
ALTER TABLE `payments` DROP COLUMN `document_id`;

ALTER TABLE `payments` ADD CONSTRAINT `FK_payments_client` FOREIGN KEY (`client_id`) REFERENCES `clients` (`id_client`);
ALTER TABLE `payments` ADD CONSTRAINT `FK_payments_plan` FOREIGN KEY (`plan_id`) REFERENCES `plans` (`id_plan`);
ALTER TABLE `payments` ADD CONSTRAINT `uc_client_period` UNIQUE (`client_id`, `period`);
