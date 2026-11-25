package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelo.Mecanico;

/** DAO de mecanicos: solo listado (el enunciado no pide ABM de mecanicos). */
public class MecanicoDAO {

    public List<Mecanico> listar() throws SQLException {
        List<Mecanico> lista = new ArrayList<>();
        String sql = "SELECT legajo, nombre, apellido, especialidad, valor_hora "
                   + "FROM mecanico ORDER BY legajo";
        try (Connection c = Conexion.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Mecanico(
                        rs.getInt("legajo"),
                        rs.getString("nombre"),
                        rs.getString("apellido"),
                        rs.getString("especialidad"),
                        rs.getDouble("valor_hora")));
            }
        }
        return lista;
    }
}
