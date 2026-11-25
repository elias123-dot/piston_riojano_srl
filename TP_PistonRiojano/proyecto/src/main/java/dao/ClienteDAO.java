package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Cliente;

/**
 * [ETIQUETA: PATRON DAO] Toda la SQL de clientes vive aca.
 * Las ventanas (vista) NO ejecutan SQL: llaman a los metodos de esta clase.
 * Todo usa PreparedStatement (exigido) y try-with-resources.
 */
public class ClienteDAO {

    /** Lista todos los clientes ordenados por apellido. */
    public List<Cliente> listar() throws SQLException {
        List<Cliente> lista = new ArrayList<>();
        String sql = "SELECT dni, nombre, apellido, telefono, email FROM cliente ORDER BY apellido";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Cliente(
                        rs.getString("dni"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("telefono"),
                        rs.getString("email")));
            }
        }
        return lista;
    }

    /** Alta de cliente. El DNI repetido lo detecta la PK (excepcion SQL). */
    public void insertar(Cliente cli) throws SQLException {
        String sql = "INSERT INTO cliente (dni, nombre, apellido, telefono, email) VALUES (?,?,?,?,?)";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cli.getDni());
            ps.setString(2, cli.getNombre());
            ps.setString(3, cli.getApellido());
            ps.setString(4, cli.getTelefono());
            ps.setString(5, cli.getEmail());
            ps.executeUpdate();
        }
    }

    /** Modificacion de cliente por DNI. */
    public void modificar(Cliente cli) throws SQLException {
        String sql = "UPDATE cliente SET nombre=?, apellido=?, telefono=?, email=? WHERE dni=?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, cli.getNombre());
            ps.setString(2, cli.getApellido());
            ps.setString(3, cli.getTelefono());
            ps.setString(4, cli.getEmail());
            ps.setString(5, cli.getDni());
            ps.executeUpdate();
        }
    }

    /** Baja de cliente por DNI (la ventana controla que no tenga vehiculos). */
    public void eliminar(String dni) throws SQLException {
        String sql = "DELETE FROM cliente WHERE dni = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            ps.executeUpdate();
        }
    }

    /**
     * [ETIQUETA: REGLA RF1] Dice si el cliente tiene vehiculos asociados.
     * La ventana NO puede dar de baja un cliente con vehiculos: antes de
     * borrar pregunta con este metodo (evita que la FK reviente).
     */
    public boolean tieneVehiculos(String dni) throws SQLException {
        String sql = "SELECT count(*) FROM vehiculo WHERE cliente_dni = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }
}
