-- ============================================================
-- VERIFICACION BASE taller_piston (todo en orden)
-- ============================================================

-- 1) Las 6 tablas existen
SELECT table_name, 
       (SELECT count(*) FROM information_schema.columns c 
        WHERE c.table_name = t.table_name) AS columnas
FROM information_schema.tables t
WHERE table_schema = 'public'
ORDER BY table_name;

-- 2) Datos de prueba cargados
SELECT 'cliente' AS tabla, count(*) FROM cliente
UNION ALL SELECT 'vehiculo', count(*) FROM vehiculo
UNION ALL SELECT 'mecanico', count(*) FROM mecanico
UNION ALL SELECT 'repuesto', count(*) FROM repuesto
UNION ALL SELECT 'orden_trabajo', count(*) FROM orden_trabajo
UNION ALL SELECT 'orden_repuesto', count(*) FROM orden_repuesto;

-- 3) Totales de las ordenes (verificar contra el enunciado)
-- Orden 2 (diesel): 3.5 h x 10000 x 1.20 + 95000 = 137.000
-- Orden 3 (moto 190cc): 1.5 h x 9500 x 0.80 + 76000 = 87.400
-- Orden 1 (nafta): 2 h x 12000 x 1.00 + 15000 + 52000 = 91.000
SELECT o.numero, o.estado, o.patente,
       sum(d.cantidad * d.precio_aplicado) + 
       o.horas * m.valor_hora *
         CASE WHEN v.tipo='MOTO' AND v.cilindrada<=250 THEN 0.8
              WHEN v.tipo='MOTO' THEN 1.0
              WHEN v.combustible='DIESEL' THEN 1.2
              ELSE 1.0 END
       AS total
FROM orden_trabajo o
JOIN mecanico m ON m.legajo = o.legajo
JOIN vehiculo v ON v.patente = o.patente
LEFT JOIN orden_repuesto d ON d.orden_numero = o.numero
GROUP BY o.numero, o.estado, o.patente, o.horas, m.valor_hora, v.tipo, v.cilindrada, v.combustible
ORDER BY o.numero;
