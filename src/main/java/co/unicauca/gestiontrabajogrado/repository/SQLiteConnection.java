package co.unicauca.gestiontrabajogrado.repository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class SQLiteConnection {
    private static final String URL = "jdbc:sqlite:usuarios.db";
    private SQLiteConnection() {}

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
