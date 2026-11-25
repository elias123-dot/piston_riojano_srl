# TP Parcial Final - Taller Mecánico "Pistón Riojano SRL"

**Integrante:** Emiliano Elias Cabrera — DNI 43675199 — Legajo ELSI1110
**Materia:** Paradigmas y Lenguajes III (UNLaR) — Ciclo lectivo 2026

## Versiones usadas

| Herramienta | Version |
|---|---|
| JDK | BellSoft Liberica 23 (compatible con JDK 17+) |
| PostgreSQL | 18.6 |
| NetBeans | 25 (proyecto Maven, tambien abre en cualquier IDE) |
| Driver JDBC | org.postgresql:postgresql:42.7.4 (incluido en el proyecto Maven y en `proyecto/lib/`) |

## Pasos para crear la base de datos

1. Abrir pgAdmin (o psql) y conectarse al servidor PostgreSQL local.
2. Crear la base **vacia** `taller_piston`:
   ```sql
   CREATE DATABASE taller_piston;
   ```
3. Ejecutar sobre esa base el script `sql/schema.sql` (crea las 6 tablas).
4. Ejecutar el script `sql/datos.sql` (datos de prueba: 3 clientes, 4 vehiculos,
   3 mecanicos, 10 repuestos, 3 ordenes de ejemplo).
5. (Opcional) Verificar con:
   ```sql
   SELECT * FROM orden_trabajo;
   SELECT * FROM orden_repuesto;
   ```

Ambos scripts se pueden ejecutar mas de una vez sin error (empiezan con
`DROP TABLE IF EXISTS`).

## Configurar la conexion

Los datos de conexion estan en **un unico lugar**: la clase
`src/main/java/dao/Conexion.java`. Si su PostgreSQL usa otro usuario o
contrasena, editar solo esas dos constantes:

```java
private static final String USUARIO = "postgres";
private static final String CONTRASENA = "123456";
```

## Compilar y ejecutar

### Opcion A: NetBeans (recomendada)
1. `Archivo -> Abrir proyecto` y elegir la carpeta `proyecto/`.
2. Boton derecho sobre el proyecto -> `Run` (o F6).
   Se abre la ventana principal con el menu **Operaciones**.

### Opcion B: consola con Maven (incluido en NetBeans)
```
cd proyecto
mvn compile
mvn exec:java
```

### Opcion C: sin Maven
```
javac -cp "lib/postgresql-42.7.4.jar" -d bin src/main/java/*/*.java
java -cp "bin;lib/postgresql-42.7.4.jar" vista.VentanaPrincipal
```

## Estructura del proyecto

```
proyecto/
  pom.xml                    <- Maven: Java 17 + driver PostgreSQL
  src/main/java/
    modelo/                  <- clases del dominio (fiel al diagrama UML)
      Vehiculo.java          <- abstracta, coeficiente() abstracto
      Auto.java / Moto.java  <- subclases, redefinen coeficiente()
      Cliente.java, Mecanico.java, Repuesto.java
      OrdenTrabajo.java      <- agregarRepuesto() y total()
      DetalleRepuesto.java   <- linea de repuesto con precio aplicado
      EstadoOrden.java       <- enumeracion de estados
      StockInsuficienteException.java <- excepcion propia
    dao/                     <- acceso a datos (JDBC)
      Conexion.java          <- UNICA clase con URL/usuario/contrasena
      ClienteDAO, VehiculoDAO, MecanicoDAO, RepuestoDAO, OrdenDAO
    vista/                   <- ventanas Swing (MDI)
      VentanaPrincipal.java  <- JFrame + JDesktopPane + JMenu
      ClienteFrame.java      <- RF1 (ABM con JTable)
      VehiculoFrame.java     <- RF2 (campos dinamicos Auto/Moto)
      RepuestoFrame.java     <- RF3 (precio y stock)
      OrdenFrame.java        <- RF4 (nueva orden + detalle + total)
      ConsultaOrdenesFrame.java <- RF5 (filtro por estado)
diseno/
  diagrama_clases.png        <- diagrama de clases UML
  modelo_relacional.png      <- modelo relacional (notacion pata de cuervo)
  capturas/
    pgadmin_base.png         <- las 6 tablas de la base en pgAdmin
    psql_datos.png           <- datos y totales verificados en PostgreSQL
```

## Mapeo de la herencia (decision de diseño)

Una **sola tabla `vehiculo`** con columna discriminadora `tipo` (AUTO/MOTO)
y columnas opcionales para cada tipo (`cantidad_puertas`/`combustible` para
autos, `cilindrada` para motos). Un `CHECK` garantiza coherencia: si es AUTO,
sus columnas no pueden ser NULL y la de la moto si, y viceversa. Se eligio
esta estrategia (tabla por jerarquia) porque con solo 2 subclases y 3 columnas
de diferencia es la mas simple, evita joins y es facil de explicar. El DAO lee
`tipo` y construye un `Auto` o una `Moto` (polimorfismo).

## Requerimientos

| RF | Donde esta |
|---|---|
| RF1 ABM clientes | `vista.ClienteFrame` + `dao.ClienteDAO` (baja bloqueada si tiene vehiculos) |
| RF2 Vehiculos por cliente | `vista.VehiculoFrame` (JComboBox de clientes + campos dinamicos) |
| RF3 Repuestos | `vista.RepuestoFrame` + `dao.RepuestoDAO` |
| RF4 Nueva orden | `vista.OrdenFrame` + `dao.OrdenDAO.insertar()` (**transaccion** con commit/rollback) |
| RF5 Consulta de ordenes | `vista.ConsultaOrdenesFrame` + filtro por estado + cambio de estado |

## Limitaciones conocidas

- El ABM de mecanicos no esta pedido por el enunciado: se cargan por `datos.sql`.
- La ventana de vehiculos lista todos los vehiculos; el filtro por cliente se
  usa al momento de dar de alta (RF2 pide alta por cliente y listado general).
- La excepcion de stock tambien se valida a nivel SQL (`AND stock >= ?`) para
  cubrir ventas concurrentes.
