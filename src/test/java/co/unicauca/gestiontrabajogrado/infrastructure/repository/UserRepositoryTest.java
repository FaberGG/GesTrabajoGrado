package co.unicauca.gestiontrabajogrado.infrastructure.repository;


import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;
import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Pruebas de integración para UserRepository.
 * Se utiliza una base de datos SQLite en memoria para asegurar la consistencia.
 */
class UserRepositoryTest {

    private IUserRepository userRepository;
    private static final String DB_FILE_NAME = "test_usuarios.db";
    private static final Path DB_PATH = Paths.get(DB_FILE_NAME);

    @BeforeAll
    static void setUpAll() {
        // Asegura que la clase de conexión use la base de datos de prueba
        System.setProperty("db.url", "jdbc:sqlite:" + DB_FILE_NAME);
        DatabaseInitializer.ensureCreated();
    }

    @BeforeEach
    void setUp() {
        // Reinicia el estado de la base de datos antes de cada prueba
        this.userRepository = new UserRepository();
        clearDatabase();
    }

    @AfterAll
    static void tearDownAll() throws IOException {
        Files.deleteIfExists(DB_PATH);
    }

    private void clearDatabase() {
        try (Connection c = DatabaseConnection.get();
             Statement st = c.createStatement()) {
            st.executeUpdate("DELETE FROM usuarios");
        } catch (SQLException e) {
            throw new RuntimeException("Error limpiando la base de datos: " + e.getMessage(), e);
        }
    }

    // --- Pruebas para emailExists() ---

    @Test
    void testEmailExists_WhenEmailExists_ReturnsTrue() throws SQLException {
        // Arrange
        User user = new User(null, "nombre1", "apellido1", "123456789", enumProgram.INGENIERIA_DE_SISTEMAS, enumRol.ESTUDIANTE, "test@unicauca.edu.co", "passwordHash");
        insertUser(user);

        // Act
        boolean exists = userRepository.emailExists("test@unicauca.edu.co");

        // Assert
        assertTrue(exists, "El email debe existir en la base de datos.");
    }

    @Test
    void testEmailExists_WhenEmailDoesNotExist_ReturnsFalse() {
        // Act
        boolean exists = userRepository.emailExists("nonexistent@unicauca.edu.co");

        // Assert
        assertFalse(exists, "El email no debe existir en la base de datos.");
    }

    // --- Pruebas para save() ---

    @Test
    void testSave_Success() {
        // Arrange
        User user = new User(null, "nombre2", "apellido2", "123456789", enumProgram.INGENIERIA_DE_SISTEMAS, enumRol.ESTUDIANTE, "save_test@unicauca.edu.co", "passwordHash");

        // Act
        User savedUser = userRepository.save(user);

        // Assert
        assertNotNull(savedUser.getId(), "El ID del usuario guardado no debe ser nulo.");
        Optional<User> foundUser = userRepository.findByEmail("save_test@unicauca.edu.co");
        assertTrue(foundUser.isPresent(), "El usuario guardado debe poder ser encontrado.");
    }

    @Test
    void testSave_EmailAlreadyExists_ThrowsException() throws SQLException {
        // Arrange
        User user1 = new User(null, "nombre3", "apellido3", "123456789", enumProgram.INGENIERIA_DE_SISTEMAS, enumRol.ESTUDIANTE, "exists@unicauca.edu.co", "passwordHash");
        insertUser(user1);
        User user2 = new User(null, "nombre4", "apellido4", "987654321", enumProgram.INGENIERIA_DE_SISTEMAS, enumRol.ESTUDIANTE, "exists@unicauca.edu.co", "passwordHash");

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            userRepository.save(user2);
        }, "Debería lanzar una excepción al intentar guardar un email duplicado.");
    }

    // --- Pruebas para findByEmail() ---

    @Test
    void testFindByEmail_Success() throws SQLException {
        // Arrange
        User user = new User(null, "nombre5", "apellido5", "123456789", enumProgram.INGENIERIA_DE_SISTEMAS, enumRol.ESTUDIANTE, "find_test@unicauca.edu.co", "passwordHash");
        insertUser(user);

        // Act
        Optional<User> foundUser = userRepository.findByEmail("find_test@unicauca.edu.co");

        // Assert
        assertTrue(foundUser.isPresent(), "Se debe encontrar el usuario.");
        assertEquals("nombre5", foundUser.get().getNombres());
        assertEquals("find_test@unicauca.edu.co", foundUser.get().getEmail());
    }

    @Test
    void testFindByEmail_UserDoesNotExist_ReturnsEmptyOptional() {
        // Act
        Optional<User> foundUser = userRepository.findByEmail("nonexistent@unicauca.edu.co");

        // Assert
        assertFalse(foundUser.isPresent(), "El usuario no debe ser encontrado.");
    }

    // --- Métodos de ayuda para las pruebas ---

    private void insertUser(User user) throws SQLException {
        String sql = "INSERT INTO usuarios(nombres, apellidos, celular, programa, rol, email, password) VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, user.getNombres());
            ps.setString(2, user.getApellidos());
            ps.setString(3, user.getCelular());
            ps.setString(4, user.getPrograma().name());
            ps.setString(5, user.getRol().name());
            ps.setString(6, user.getEmail());
            ps.setString(7, user.getPasswordHash());
            ps.executeUpdate();
        }
    }
}