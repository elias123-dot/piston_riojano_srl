package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Auto;
import modelo.Cliente;
import modelo.Moto;
import modelo.Vehiculo;

/**
 * [ETIQUETA: MAPEO DE HERENCIA] Los vehiculos se guardan en UNA sola tabla
 * con la columna 'tipo' (AUTO/MOTO) como discriminador. Al leer cada fila,
 * segun 'tipo' se construye un Auto o una Moto (polimorfismo).
 */
public class VehiculoDAO {

    /** Lista todos los vehiculos (de todos los clientes). */
    public List<Vehiculo> listarTodos() throws SQLException {
        return ejecutarListado("SELECT * FROM vehiculo ORDER BY patente", null);
    }

    /** Lista los vehiculos de un cliente dado (RF2). */
    public List<Vehiculo> listarPorCliente(String dni) throws SQLException {
        return ejecutarListado(
                "SELECT * FROM vehiculo WHERE cliente_dni = ? ORDER BY patente", dni);
    }

    /** Consulta reutilizable: arma el SQL, lo ejecuta y crea los objetos. */
    private List<Vehiculo> ejecutarListado(String sql, String dni) throws SQLException {
        List<Vehiculo> lista = new ArrayList<>();
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (dni != null) {
                ps.setString(1, dni);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapear(rs));
                }
            }
        }
        return lista;
    }

    /**
     * [ETIQUETA: METODO DIFICIL] Convierte una fila de la tabla vehiculo
     * en un objeto Auto o Moto segun el valor de la columna 'tipo'.
     * Este es el "mapeo" entre la base y el diagrama de clases.
     */
    private Vehiculo mapear(ResultSet rs) throws SQLException {
        Cliente cliente = new Cliente(rs.getString("cliente_dni"), "", "", "", null);
        String tipo = rs.getString("tipo");
        Vehiculo v;
        if ("AUTO".equals(tipo)) {
            v = new Auto(
                    rs.getString("patente"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("año"),
                    rs.getInt("cantidad_puertas"),
                    rs.getString("combustible"));
        } else {
            v = new Moto(
                    rs.getString("patente"),
                    rs.getString("marca"),
                    rs.getString("modelo"),
                    rs.getInt("año"),
                    rs.getInt("cilindrada"));
        }
        v.setCliente(cliente);
        return v;
    }

    /** Alta de vehiculo (auto o moto). */
    public void insertar(Vehiculo v) throws SQLException {
        String sql = "INSERT INTO vehiculo (patente, marca, modelo, año, tipo, cliente_dni, "
                + "cantidad_puertas, combustible, cilindrada) VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, v.getPatente());
            ps.setString(2, v.getMarca());
            ps.setString(3, v.getModelo());
            ps.setInt(4, v.getAño());
            ps.setString(5, v.getTipo());
            ps.setString(6, v.getCliente().getDni());
            if (v instanceof Auto) {           // unico instanceof permitido: para llenar columnas por tipo
                Auto a = (Auto) v;
                ps.setInt(7, a.getCantidadPuertas());
                ps.setString(8, a.getCombustible());
                ps.setNull(9, java.sql.Types.INTEGER);
            } else {
                Moto m = (Moto) v;
                ps.setNull(7, java.sql.Types.INTEGER);
                ps.setNull(8, java.sql.Types.VARCHAR);
                ps.setInt(9, m.getCilindrada());
            }
            ps.executeUpdate();
        }
    }
}
