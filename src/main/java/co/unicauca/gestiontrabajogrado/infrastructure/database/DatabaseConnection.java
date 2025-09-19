package co.unicauca.gestiontrabajogrado.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    private static final String URL = System.getProperty("db.url", "jdbc:sqlite:database.db");
    private DatabaseConnection() {}

    public static Connection get() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}
