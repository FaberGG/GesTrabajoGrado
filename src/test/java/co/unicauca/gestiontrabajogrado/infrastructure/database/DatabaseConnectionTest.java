package co.unicauca.gestiontrabajogrado.infrastructure.database;

import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;
import org.junit.jupiter.api.Test;
import java.sql.Connection;
import java.sql.SQLException;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Clase de pruebas de integración para DatabaseConnection.
 * Estas pruebas requieren que una base de datos real esté configurada
 * y accesible para su ejecución.
 */
class DatabaseConnectionTest {

    /**
     * Prueba que la conexión a la base de datos no es nula.
     */
    @Test
    void testGetConnectionNotNull() {
        Connection connection = null;
        try {
            // Act
            connection = DatabaseConnection.get();

            // Assert
            assertNotNull(connection, "La conexión a la base de datos no debe ser nula.");
        } catch (SQLException e) {
            fail("Se esperaba una conexión válida, pero se lanzó una excepción: " + e.getMessage());
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Prueba que la conexión a la base de datos es válida y está abierta.
     * Utiliza el metodo isValid() para verificar el estado de la conexión.
     */
    @Test
    void testGetConnectionIsValid() {
        // Arrange
        Connection connection = null;
        try {
            // Act
            connection = DatabaseConnection.get();
            boolean isValid = connection.isValid(5); // timeout de 5 segundos

            // Assert
            assertTrue(isValid, "La conexión a la base de datos debe ser válida.");

        } catch (SQLException e) {
            fail("No se esperaba una excepción de SQL al probar la conexión válida.", e);
        } finally {
            // Cierra la conexión en el bloque finally para asegurar la limpieza
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    System.err.println("Error al cerrar la conexión: " + e.getMessage());
                }
            }
        }
    }

    /**
     * Prueba que la conexión puede ser cerrada correctamente.
     */
    @Test
    void testConnectionCanBeClosed() {
        // Arrange
        Connection connection = null;
        try {
            connection = DatabaseConnection.get();
            assertNotNull(connection, "La conexión inicial no debe ser nula.");

            // Act
            connection.close();

            // Assert
            assertTrue(connection.isClosed(), "La conexión debe estar cerrada después de llamar a close().");

        } catch (SQLException e) {
            fail("No se esperaba una excepción de SQL al cerrar la conexión.", e);
        }
    }
}