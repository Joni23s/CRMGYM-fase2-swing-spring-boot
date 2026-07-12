-- V2__Default_Data.sql
-- Datos iniciales (semilla) para planes, clientes, histórico de planes y pagos.

-- 1. Inserción de Planes por defecto
INSERT INTO `plans` (`id_plan`, `days_enabled`, `hours_enabled`, `status`, `name_plan`, `notes`, `value`) VALUES
(1, 0, 0, 1, 'Sin Plan', 'Para cambios de planes o clientes sin asignar', 0.00),
(2, 1, 2, 1, 'El día', 'Registro por el día', 5000.00),
(3, 2, 4, 0, 'Basico', 'Plan inicial', 10000.00),
(4, 5, 10, 1, 'Premium', 'Con instructor y rutina', 45000.00);

-- 2. Inserción de Clientes iniciales
INSERT INTO `clients` (`document_id`, `email`, `is_active`, `last_name`, `name`, `phone_number`, `current_plan_id`) VALUES
(25588665, 'manu.mail@mail.com', 1, 'Gonzales', 'Manuel', '3455889952', 4),
(35687745, 'joni.mail@mail.com', 1, 'Araujo', 'Jonathan', '2634587995', 1),
(39456789, 'clau_lopez@mail.com', 1, 'Lopez', 'Claudio', '2364879654', 1),
(43256987, 'mati@mail.com', 1, 'Ortega', 'Mateo', '12547899555', 2),
(398745852, 'fran@mail.com', 0, 'Gimenéz', 'Francisco', '21024458889', 4);

-- 3. Inserción de Histórico de Planes
INSERT INTO `historical_plans` (`id_historical`, `end_date`, `is_active`, `start_date`, `document_id`, `id_plan`) VALUES
(1, '2025-10-13', 0, '2025-08-12', 39456789, 2),
(2, '2025-08-14', 0, '2025-08-14', 43256987, 3),
(3, '2025-10-15', 0, '2025-08-14', 43256987, 1),
(4, NULL, 1, '2025-08-19', 398745852, 4),
(5, '2025-10-13', 0, '2025-10-13', 35687745, 2),
(6, NULL, 1, '2025-10-13', 35687745, 1),
(7, NULL, 1, '2025-10-13', 39456789, 1),
(8, NULL, 1, '2025-10-15', 25588665, 4),
(9, NULL, 1, '2025-10-15', 43256987, 2);

-- 4. Inserción de Pagos iniciales
INSERT INTO `payments` (`payment_id`, `base_amount`, `discount_applied`, `final_amount`, `payment_date`, `payment_method`, `payment_status`, `period`, `document_id`) VALUES
(1, 15000.00, 0.00, 15000.00, '2025-10-13', 'EFECTIVO', 'CONFIRMADO', '2025-10-13', 39456789),
(2, 13500.00, 0.00, 13500.00, '2025-10-13', 'TRANSFERENCIA', 'PENDIENTE', '2025-10-01', 43256987),
(3, 0.00, 0.00, 0.00, '2025-10-13', 'EFECTIVO', 'PENDIENTE', '2025-10-01', 35687745),
(4, 15000.00, 0.00, 15000.00, '2025-10-15', 'EFECTIVO', 'CONFIRMADO', '2025-10-01', 25588665);
