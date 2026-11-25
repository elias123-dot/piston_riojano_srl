package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.DetalleRepuesto;
import modelo.EstadoOrden;
import modelo.Mecanico;
import modelo.OrdenTrabajo;
import modelo.StockInsuficienteException;
import modelo.Vehiculo;

/** DAO de ordenes de trabajo: alta transaccional, consulta y cambio de estado. */
public class OrdenDAO {

    private final VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private final MecanicoDAO mecanicoDAO = new MecanicoDAO();
    private final RepuestoDAO repuestoDAO = new RepuestoDAO();

    /**
     * [ETIQUETA: TRANSACCION - METODO MAS DIFICIL DEL TP]
     * Guarda en UNA SOLA transaccion:
     *   1) la orden, 2) el detalle de repuestos, 3) el descuento de stock.
     * Si algo falla (ej: stock insuficiente por una venta concurrente) se
     * hace ROLLBACK: no queda la orden sin detalle ni el stock mal descontado.
     * Autocommit = false arranca la transaccion; commit() la confirma.
     * try-with-resources garantiza el rollback y el cierre si hay excepcion.
     */
    public void insertar(OrdenTrabajo o) throws SQLException, StockInsuficienteException {

        try (Connection c = Conexion.getConnection()) {
            c.setAutoCommit(false);   // arranca la transaccion
            try {
                // 1) insertar la orden
                String sqlOrden = "INSERT INTO orden_trabajo "
                        + "(fecha_ingreso, descripcion, estado, horas, patente, legajo) "
                        + "VALUES (?,?,?,?,?,?) RETURNING numero";
                int numero;
                try (PreparedStatement ps = c.prepareStatement(sqlOrden)) {
                    ps.setObject(1, o.getFechaIngreso());
                    ps.setString(2, o.getDescripcion());
                    ps.setString(3, o.getEstado().name());
                    ps.setDouble(4, o.getHoras());
                    ps.setString(5, o.getVehiculo().getPatente());
                    ps.setInt(6, o.getMecanico().getLegajo());
                    try (ResultSet rs = ps.executeQuery()) {
                        rs.next();
                        numero = rs.getInt(1);
                    }
                }
                o.setNumero(numero);

                // 2) insertar el detalle y 3) descontar stock (por cada repuesto)
                String sqlDetalle = "INSERT INTO orden_repuesto "
                        + "(orden_numero, repuesto_codigo, cantidad, precio_aplicado) VALUES (?,?,?,?)";
                String sqlStock = "UPDATE repuesto SET stock = stock - ? WHERE codigo = ? AND stock >= ?";
                try (PreparedStatement psDetalle = c.prepareStatement(sqlDetalle);
                     PreparedStatement psStock = c.prepareStatement(sqlStock)) {
                    for (DetalleRepuesto d : o.getDetalles()) {
                        psDetalle.setInt(1, numero);
                        psDetalle.setString(2, d.getRepuesto().getCodigo());
                        psDetalle.setInt(3, d.getCantidad());
                        psDetalle.setDouble(4, d.getPrecioAplicado());
                        psDetalle.executeUpdate();

                        // AND stock >= ? garantiza que no quede stock negativo;
                        // si no actualiza ninguna fila, el stock no alcanzaba -> rollback
                        psStock.setInt(1, d.getCantidad());
                        psStock.setString(2, d.getRepuesto().getCodigo());
                        psStock.setInt(3, d.getCantidad());
                        if (psStock.executeUpdate() == 0) {
                            throw new StockInsuficienteException(
                                "Stock insuficiente de " + d.getRepuesto().getDescripcion());
                        }
                    }
                }
                c.commit();   // todo ok: se confirma la transaccion
            } catch (Exception e) {
                c.rollback(); // cualquier error: se deshace TODO
                if (e instanceof StockInsuficienteException) {
                    throw (StockInsuficienteException) e;
                }
                throw new SQLException("Error al guardar la orden: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Consulta de ordenes filtrando por estado (RF5). Devuelve filas
     * armadas para la tabla: numero, fecha, patente, mecanico y total.
     */
    public List<Object[]> listarPorEstado(EstadoOrden estado) throws SQLException {
        List<Object[]> filas = new ArrayList<>();
        String sql = "SELECT o.numero, o.fecha_ingreso, o.patente, "
                + "m.apellido || ', ' || m.nombre AS mecanico, o.horas, m.valor_hora, "
                + "v.tipo, v.combustible, v.cilindrada, "
                + "COALESCE(SUM(r.cantidad * r.precio_aplicado), 0) AS repuestos "
                + "FROM orden_trabajo o "
                + "JOIN mecanico m ON m.legajo = o.legajo "
                + "JOIN vehiculo v ON v.patente = o.patente "
                + "LEFT JOIN orden_repuesto r ON r.orden_numero = o.numero "
                + "WHERE o.estado = ? "
                + "GROUP BY o.numero, o.fecha_ingreso, o.patente, m.apellido, m.nombre, "
                + "o.horas, m.valor_hora, v.tipo, v.combustible, v.cilindrada "
                + "ORDER BY o.numero";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, estado.name());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // total = maño de obra x coeficiente + repuestos
                    double coeficiente = coeficienteDe(
                            rs.getString("tipo"),
                            rs.getString("combustible"),
                            rs.getInt("cilindrada"));
                    double total = rs.getDouble("horas") * rs.getDouble("valor_hora") * coeficiente
                                 + rs.getDouble("repuestos");
                    filas.add(new Object[]{
                        rs.getInt("numero"),
                        rs.getDate("fecha_ingreso"),
                        rs.getString("patente"),
                        rs.getString("mecanico"),
                        total
                    });
                }
            }
        }
        return filas;
    }

    /** Coeficiente por tipo de vehiculo (igual que en el modelo). */
    private double coeficienteDe(String tipo, String combustible, int cilindrada) {
        if ("MOTO".equals(tipo)) {
            return cilindrada <= 250 ? 0.80 : 1.00;
        }
        return "DIESEL".equals(combustible) ? 1.20 : 1.00;
    }

    /** Cambia el estado de una orden (RF5). */
    public void cambiarEstado(int numero, EstadoOrden nuevo) throws SQLException {
        String sql = "UPDATE orden_trabajo SET estado = ? WHERE numero = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, nuevo.name());
            ps.setInt(2, numero);
            ps.executeUpdate();
        }
    }
}
