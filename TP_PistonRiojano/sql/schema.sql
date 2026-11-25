-- ============================================================
-- TP PARCIAL FINAL - Taller Mecánico "Pistón Riojano SRL"
-- schema.sql: crea las tablas en la base taller_piston
-- Se puede ejecutar mas de una vez (DROP TABLE IF EXISTS)
-- ============================================================

DROP TABLE IF EXISTS orden_repuesto;
DROP TABLE IF EXISTS orden_trabajo;
DROP TABLE IF EXISTS vehiculo;
DROP TABLE IF EXISTS repuesto;
DROP TABLE IF EXISTS mecanico;
DROP TABLE IF EXISTS cliente;

-- ------------------------------------------------------------
-- CLIENTE: DNI unico, nombre, apellido, telefono, email opcional
-- ------------------------------------------------------------
CREATE TABLE cliente (
    dni       char(8)      PRIMARY KEY,
    nombre    varchar(50)  NOT NULL,
    apellido  varchar(50)  NOT NULL,
    telefono  varchar(20)  NOT NULL,
    email     varchar(80)              -- opcional (puede ser NULL)
);

-- ------------------------------------------------------------
-- VEHICULO: herencia mapeada en una sola tabla (ver informe)
-- tipo = 'AUTO' o 'MOTO' (discriminador)
-- AUTO usa: cantidad_puertas, combustible
-- MOTO usa: cilindrada
-- CHECK garantiza coherencia segun el tipo
-- ------------------------------------------------------------
CREATE TABLE vehiculo (
    patente         varchar(7)   PRIMARY KEY,
    marca           varchar(30)  NOT NULL,
    modelo          varchar(30)  NOT NULL,
    "año"           int          NOT NULL CHECK ("año" BETWEEN 1950 AND 2100),
    tipo            varchar(4)   NOT NULL CHECK (tipo IN ('AUTO','MOTO')),
    cliente_dni     char(8)      NOT NULL REFERENCES cliente(dni),

    -- columnas de AUTO (NULL si es moto)
    cantidad_puertas int         CHECK (cantidad_puertas >= 0),
    combustible      varchar(6)  CHECK (combustible IN ('NAFTA','DIESEL','GNC')),

    -- columna de MOTO (NULL si es auto)
    cilindrada       int         CHECK (cilindrada > 0),

    -- coherencia: un AUTO debe tener puertas y combustible; una MOTO cilindrada
    CONSTRAINT chk_auto_completo CHECK (
        (tipo = 'AUTO' AND cantidad_puertas IS NOT NULL AND combustible IS NOT NULL AND cilindrada IS NULL)
        OR
        (tipo = 'MOTO' AND cilindrada IS NOT NULL AND cantidad_puertas IS NULL AND combustible IS NULL)
    )
);

-- ------------------------------------------------------------
-- MECANICO: legajo, especialidad y valor por hora
-- ------------------------------------------------------------
CREATE TABLE mecanico (
    legajo       int          PRIMARY KEY,
    nombre       varchar(50)  NOT NULL,
    apellido     varchar(50)  NOT NULL,
    especialidad varchar(15)  NOT NULL CHECK (especialidad IN ('MOTOR','ELECTRICIDAD','FRENOS','GENERAL')),
    valor_hora   numeric(10,2) NOT NULL CHECK (valor_hora >= 0)
);

-- ------------------------------------------------------------
-- REPUESTO: codigo alfanumerico, precio actual y stock
-- ------------------------------------------------------------
CREATE TABLE repuesto (
    codigo      varchar(10)   PRIMARY KEY,
    descripcion varchar(100)  NOT NULL,
    precio_unitario numeric(12,2) NOT NULL CHECK (precio_unitario >= 0),
    stock       int           NOT NULL CHECK (stock >= 0)
);

-- ------------------------------------------------------------
-- ORDEN DE TRABAJO: numero correlativo (SERIAL = autonumerico),
-- estados: PENDIENTE -> EN_REPARACION -> FINALIZADA -> ENTREGADA
-- horas de mano de obra y mecánico responsable unico
-- ------------------------------------------------------------
CREATE TABLE orden_trabajo (
    numero     serial        PRIMARY KEY,
    fecha_ingreso date       NOT NULL DEFAULT current_date,
    descripcion varchar(200)  NOT NULL,
    estado     varchar(15)   NOT NULL DEFAULT 'PENDIENTE'
               CHECK (estado IN ('PENDIENTE','EN_REPARACION','FINALIZADA','ENTREGADA')),
    horas      numeric(5,1)  NOT NULL CHECK (horas >= 0),
    patente    varchar(7)    NOT NULL REFERENCES vehiculo(patente),
    legajo     int           NOT NULL REFERENCES mecanico(legajo)
);

-- ------------------------------------------------------------
-- ORDEN_REPUESTO: relacion N:M entre orden y repuesto
-- guarda cantidad y el PRECIO APLICADO (el precio de ese momento,
-- no el precio actual del repuesto)
-- PK compuesta: una misma orden no puede usar 2 veces el mismo repuesto
-- ------------------------------------------------------------
CREATE TABLE orden_repuesto (
    orden_numero    int NOT NULL REFERENCES orden_trabajo(numero),
    repuesto_codigo varchar(10) NOT NULL REFERENCES repuesto(codigo),
    cantidad        int NOT NULL CHECK (cantidad > 0),
    precio_aplicado numeric(12,2) NOT NULL CHECK (precio_aplicado >= 0),
    PRIMARY KEY (orden_numero, repuesto_codigo)
);
