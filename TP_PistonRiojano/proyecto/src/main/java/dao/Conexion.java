package dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * [ETIQUETA: CONEXION] Unica clase donde se configura la base de datos
 * (el enunciado exige que la conexion este en UN solo lugar).
 * Cambiar usuario/contrasena aca si la base usa otros datos.
 */
public class Conexion {

    private static final String URL =
        "jdbc:postgresql://localhost:5432/taller_piston";
    private static final String USUARIO = "postgres";
    private static final String CONTRASENA = "123456";

    /** Devuelve una conexion nueva a la base (el que la pide la cierra). */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USUARIO, CONTRASENA);
    }
}
