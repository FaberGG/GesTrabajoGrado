package co.unicauca.gestiontrabajogrado.infrastructure.database;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas de integración para DatabaseInitializer.
 * Estas pruebas garantizan que la tabla y el índice de la base de datos
 * se crean correctamente.
 */
class DatabaseInitializerTest {

    private static final String DB_FILE_NAME = "test_usuarios.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_FILE_NAME;
    private static final Path DB_PATH = Paths.get(DB_FILE_NAME);

    /**
     * Configura la conexión de la base de datos de prueba antes de todas las pruebas.
     * Esto asegura que las pruebas se ejecutan en un entorno limpio.
     */
    @BeforeAll
    static void setUpAll() {
        // Asigna la URL de la base de datos de prueba para DatabaseConnection
        System.setProperty("db.url", DB_URL);
    }

    /**
     * Elimina el archivo de la base de datos de prueba después de todas las pruebas.
     */
    @AfterAll
    static void tearDownAll() throws IOException {
        Files.deleteIfExists(DB_PATH);
    }

    /**
     * Prueba que el metodo ensureCreated() no lance una excepción y cree el archivo de la base de datos.
     */
    @Test
    void testEnsureCreatedCreatesDatabaseFile() {
        // Act
        DatabaseInitializer.ensureCreated();

        // Assert
        assertTrue(Files.exists(DB_PATH), "El archivo de la base de datos debe ser creado.");
    }

    /**
     * Prueba que el metodo ensureCreated() cree la tabla 'usuarios' correctamente.
     */
    @Test
    void testEnsureCreatedCreatesUsersTable() {
        // Arrange
        DatabaseInitializer.ensureCreated();

        try (Connection c = DatabaseConnection.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='table' AND name='usuarios'")) {

            // Assert
            assertTrue(rs.next(), "La tabla 'usuarios' debe existir en la base de datos.");

        } catch (SQLException e) {
            fail("No se esperaba una excepción al verificar la existencia de la tabla.", e);
        }
    }

    /**
     * Prueba que el metodo ensureCreated() cree el índice 'idx_usuarios_email' correctamente.
     */
    @Test
    void testEnsureCreatedCreatesEmailIndex() {
        // Arrange
        DatabaseInitializer.ensureCreated();

        try (Connection c = DatabaseConnection.get();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery("SELECT name FROM sqlite_master WHERE type='index' AND name='idx_usuarios_email'")) {

            // Assert
            assertTrue(rs.next(), "El índice 'idx_usuarios_email' debe existir.");

        } catch (SQLException e) {
            fail("No se esperaba una excepción al verificar la existencia del índice.", e);
        }
    }
}