package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Repuesto;

/** DAO de repuestos: listado y actualizacion de precio/stock (RF3). */
public class RepuestoDAO {

    public List<Repuesto> listar() throws SQLException {
        List<Repuesto> lista = new ArrayList<>();
        String sql = "SELECT codigo, descripcion, precio_unitario, stock FROM repuesto ORDER BY codigo";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Repuesto(
                        rs.getString("codigo"),
                        rs.getString("descripcion"),
                        rs.getDouble("precio_unitario"),
                        rs.getInt("stock")));
            }
        }
        return lista;
    }

    /** Busca un repuesto por codigo (lo usa la nueva orden). */
    public Repuesto buscar(String codigo) throws SQLException {
        String sql = "SELECT codigo, descripcion, precio_unitario, stock FROM repuesto WHERE codigo = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, codigo);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Repuesto(
                            rs.getString("codigo"),
                            rs.getString("descripcion"),
                            rs.getDouble("precio_unitario"),
                            rs.getInt("stock"));
                }
                return null;
            }
        }
    }

    /** Actualiza precio y stock de un repuesto (RF3). */
    public void actualizar(String codigo, double precio, int stock) throws SQLException {
        String sql = "UPDATE repuesto SET precio_unitario = ?, stock = ? WHERE codigo = ?";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setDouble(1, precio);
            ps.setInt(2, stock);
            ps.setString(3, codigo);
            ps.executeUpdate();
        }
    }
}