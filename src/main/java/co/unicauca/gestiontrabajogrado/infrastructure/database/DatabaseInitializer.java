package co.unicauca.gestiontrabajogrado.infrastructure.database;

import java.sql.Connection;
import java.sql.Statement;

public final class DatabaseInitializer {
    private DatabaseInitializer() {}

    public static void ensureCreated() {
        String sql = """
            -- Tabla de usuarios
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

            -- Tabla principal para proyectos de grado
            CREATE TABLE IF NOT EXISTS proyecto_grado (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                titulo VARCHAR(255) NOT NULL,
                modalidad VARCHAR(50) NOT NULL CHECK (modalidad IN ('INVESTIGACION', 'PRACTICA_PROFESIONAL')),
                fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                director_id INTEGER NOT NULL,
                codirector_id INTEGER,
                objetivo_general TEXT NOT NULL,
                objetivos_especificos TEXT NOT NULL,
                estudiante_id INTEGER,
                estado VARCHAR(50) NOT NULL DEFAULT 'EN_PROCESO' CHECK (estado IN ('EN_PROCESO', 'APROBADO', 'RECHAZADO', 'RECHAZADO_DEFINITIVO')),
                numero_intentos INTEGER NOT NULL DEFAULT 1,
                created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
                FOREIGN KEY (director_id) REFERENCES usuarios(id),
                FOREIGN KEY (codirector_id) REFERENCES usuarios(id),
                FOREIGN KEY (estudiante_id) REFERENCES usuarios(id)
            );

            -- Tabla para versiones del Formato A
            CREATE TABLE IF NOT EXISTS formato_a (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                proyecto_grado_id INTEGER NOT NULL,
                numero_intento INTEGER NOT NULL,
                ruta_archivo VARCHAR(500) NOT NULL,
                nombre_archivo VARCHAR(255) NOT NULL,
                fecha_carga DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                estado VARCHAR(50) NOT NULL DEFAULT 'PENDIENTE' CHECK (estado IN ('PENDIENTE', 'APROBADO', 'RECHAZADO')),
                observaciones TEXT,
                evaluado_por INTEGER,
                fecha_evaluacion DATETIME,
                FOREIGN KEY (proyecto_grado_id) REFERENCES proyecto_grado(id),
                FOREIGN KEY (evaluado_por) REFERENCES usuarios(id),
                UNIQUE (proyecto_grado_id, numero_intento)
            );
        """;

        try (Connection c = DatabaseConnection.get();
             Statement st = c.createStatement()) {
            st.executeUpdate(sql);
            System.out.println("Tablas 'usuarios', 'proyecto_grado' y 'formato_a' listas.");
        } catch (Exception e) {
            throw new RuntimeException("Error creando tablas: " + e.getMessage(), e);
        }
    }
}
