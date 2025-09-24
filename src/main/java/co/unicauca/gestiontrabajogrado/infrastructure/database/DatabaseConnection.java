package co.unicauca.gestiontrabajogrado.infrastructure.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseConnection {
    public static Connection get() throws SQLException {
        String url = System.getProperty("db.url", "jdbc:sqlite:database.db");
        return DriverManager.getConnection(url);
    }

}
