package co.unicauca.gestiontrabajogrado.infrastructure.database;

import java.sql.Connection;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void ensureCreated() {
        String sql = """
            CREATE TABLE IF NOT EXISTS usuarios(
              id INTEGER PRIMARY KEY AUTOINCREMENT,
              nombres   TEXT NOT NULL,
              apellidos TEXT NOT NULL,
              celular   TEXT,
              enumProgram  TEXT NOT NULL,              -- Sistemas, Electronica y Tel., Automática Industrial, Tec. Telemática
              enumRol       TEXT NOT NULL,              -- ESTUDIANTE | DOCENTE | ADMIN (o los que usen)
              email     TEXT NOT NULL UNIQUE,
              password  TEXT NOT NULL               -- hash bcrypt
            );
            CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
        """;
        try (Connection c = DatabaseConnection.get();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("Tabla 'usuarios' lista.");
        } catch (Exception e) {
            throw new RuntimeException("Error creando tabla usuarios: " + e.getMessage(), e);
        }
    }
}
