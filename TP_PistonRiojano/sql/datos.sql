-- ============================================================
-- datos.sql: datos de prueba (mas de lo pedido para probar mejor)
-- 3 clientes, 4 vehiculos, 3 mecanicos, 10 repuestos
-- ============================================================

-- CLIENTES
INSERT INTO cliente (dni, nombre, apellido, telefono, email) VALUES
('31123456', 'Juan',   'Perez',    '3822-111111', 'jperez@mail.com'),
('29876543', 'Maria',  'Gomez',    '3822-222222', NULL),
('40112233', 'Carlos', 'Villagra', '3822-333333', 'cvillagra@mail.com');

-- VEHICULOS (2 autos y 2 motos)
INSERT INTO vehiculo (patente, marca, modelo, año, tipo, cliente_dni, cantidad_puertas, combustible, cilindrada) VALUES
('ABC123', 'Toyota',  'Corolla', 2015, 'AUTO', '31123456', 4, 'NAFTA',  NULL),
('DEF456', 'Toyota',  'Hilux',   2020, 'AUTO', '29876543', 4, 'DIESEL', NULL),
('GHI789', 'Honda',   'CB190',   2022, 'MOTO', '40112233', NULL, NULL, 190),
('JKL012', 'Yamaha',  'MT07',    2021, 'MOTO', '31123456', NULL, NULL, 700);

-- MECANICOS
INSERT INTO mecanico (legajo, nombre, apellido, especialidad, valor_hora) VALUES
(1, 'Luis',   'Funes',    'MOTOR',        12000.00),
(2, 'Pedro',  'Sosa',     'ELECTRICIDAD', 10000.00),
(3, 'Ana',    'Romero',   'FRENOS',        9500.00);

-- REPUESTOS (10)
INSERT INTO repuesto (codigo, descripcion, precio_unitario, stock) VALUES
('F-001', 'Filtro de aceite',        15000.00, 10),
('F-002', 'Filtro de aire',          12000.00, 8),
('R-001', 'Aceite 5W30 4L',          52000.00, 15),
('R-002', 'Aceite 10W40 1L',         18000.00, 20),
('B-001', 'Pastillas de freno',      38000.00, 12),
('B-002', 'Disco de freno',          45000.00, 6),
('E-001', 'Bateria 12V',             95000.00, 5),
('E-002', 'Bujia iridium',           11000.00, 24),
('M-001', 'Cadena de distribucion',  88000.00, 3),
('M-002', 'Kit de embrague',        120000.00, 2);

-- ORDENES de ejemplo (una por estado)
INSERT INTO orden_trabajo (fecha_ingreso, descripcion, estado, horas, patente, legajo) VALUES
('2026-11-10', 'Service completo y cambio de aceite', 'PENDIENTE', 2.0, 'ABC123', 1),
('2026-11-14', 'No arranca, revisar bateria y alternador', 'EN_REPARACION', 3.5, 'DEF456', 2),
('2026-11-15', 'Cambio de pastillas de freno delanteras', 'FINALIZADA', 1.5, 'GHI789', 3);

-- DETALLE: repuestos usados (precio_aplicado = precio de ese momento)
INSERT INTO orden_repuesto (orden_numero, repuesto_codigo, cantidad, precio_aplicado) VALUES
(1, 'F-001', 1, 15000.00),
(1, 'R-001', 1, 52000.00),
(2, 'E-001', 1, 95000.00),
(3, 'B-001', 2, 38000.00);
