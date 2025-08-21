package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Clase de pruebas unitarias para AutenticacionService.
 * Utiliza Mockito para simular las dependencias de repositorio y utilidades.
 */
@ExtendWith(MockitoExtension.class)
class AuthenticationServiceTest {

    @Mock
    private IUserRepository userRepository;

    @Mock
    private PasswordHasher passwordHasher;

    @InjectMocks
    private AutenticacionService autenticacionService;

    private User testUser;
    private final String plainPassword = "TestPassword@1";
    private final String hashedPassword = "hashedPasswordValue";

    @BeforeEach
    void setUp() {
        // Configura un usuario de prueba reutilizable
        testUser = new User();
        testUser.setEmail("test@unicauca.edu.co");
        testUser.setPasswordHash(hashedPassword);
        testUser.setRol(enumRol.ESTUDIANTE);
        testUser.setPrograma(enumProgram.INGENIERIA_DE_SISTEMAS);
    }

    // --- Pruebas para el método REGISTER ---

    @Test
    void testRegister_Success() {
        // Arrange
        // Mockea las clases estáticas con Mockito.mockStatic()
        try (MockedStatic<EmailPolicy> mockedEmailPolicy = mockStatic(EmailPolicy.class);
             MockedStatic<PasswordPolicy> mockedPasswordPolicy = mockStatic(PasswordPolicy.class)) {

            mockedEmailPolicy.when(() -> EmailPolicy.isInstitutional(testUser.getEmail())).thenReturn(true);
            mockedPasswordPolicy.when(() -> PasswordPolicy.isValid(plainPassword)).thenReturn(true);
            when(userRepository.emailExists(testUser.getEmail())).thenReturn(false);
            when(passwordHasher.hash(plainPassword)).thenReturn(hashedPassword);
            when(userRepository.save(any(User.class))).thenReturn(testUser);

            // Act
            User registeredUser = autenticacionService.register(testUser, plainPassword);

            // Assert
            assertNotNull(registeredUser);
            assertEquals(testUser.getEmail(), registeredUser.getEmail());
            // Verifica que el password hash del usuario guardado es el correcto
            assertEquals(hashedPassword, registeredUser.getPasswordHash());

            // Verifica que los métodos esperados fueron llamados
            verify(userRepository).emailExists(testUser.getEmail());
            verify(passwordHasher).hash(plainPassword);
            verify(userRepository).save(testUser);
        }
    }

    @Test
    void testRegister_InvalidEmail() {
        // Arrange
        try (MockedStatic<EmailPolicy> mockedEmailPolicy = mockStatic(EmailPolicy.class)) {
            mockedEmailPolicy.when(() -> EmailPolicy.isInstitutional(testUser.getEmail())).thenReturn(false);

            // Act & Assert
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                autenticacionService.register(testUser, plainPassword);
            });
            assertEquals("El email debe ser institucional (@unicauca.edu.co)", thrown.getMessage());
            verify(userRepository, never()).emailExists(anyString()); // Verifica que no se llamó al repositorio
        }
    }

    @Test
    void testRegister_InvalidPassword() {
        // Arrange
        try (MockedStatic<EmailPolicy> mockedEmailPolicy = mockStatic(EmailPolicy.class);
             MockedStatic<PasswordPolicy> mockedPasswordPolicy = mockStatic(PasswordPolicy.class)) {

            mockedEmailPolicy.when(() -> EmailPolicy.isInstitutional(testUser.getEmail())).thenReturn(true);
            mockedPasswordPolicy.when(() -> PasswordPolicy.isValid(plainPassword)).thenReturn(false);

            // Act & Assert
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                autenticacionService.register(testUser, plainPassword);
            });
            assertEquals("La contraseña no cumple la política (min 6, 1 dígito, 1 mayúscula, 1 especial)", thrown.getMessage());
        }
    }

    @Test
    void testRegister_EmailAlreadyExists() {
        // Arrange
        try (MockedStatic<EmailPolicy> mockedEmailPolicy = mockStatic(EmailPolicy.class);
             MockedStatic<PasswordPolicy> mockedPasswordPolicy = mockStatic(PasswordPolicy.class)) {

            mockedEmailPolicy.when(() -> EmailPolicy.isInstitutional(testUser.getEmail())).thenReturn(true);
            mockedPasswordPolicy.when(() -> PasswordPolicy.isValid(plainPassword)).thenReturn(true);
            when(userRepository.emailExists(testUser.getEmail())).thenReturn(true);

            // Act & Assert
            IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
                autenticacionService.register(testUser, plainPassword);
            });
            assertEquals("El email ya está registrado", thrown.getMessage());
            verify(userRepository, never()).save(any(User.class)); // Verifica que el usuario no se guardó
        }
    }

    // --- Pruebas para el método LOGIN ---

    @Test
    void testLogin_Success() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(plainPassword, hashedPassword)).thenReturn(true);

        // Act
        User loggedInUser = autenticacionService.login(testUser.getEmail(), plainPassword);

        // Assert
        assertNotNull(loggedInUser);
        assertEquals(testUser.getEmail(), loggedInUser.getEmail());
        verify(userRepository).findByEmail(testUser.getEmail());
        verify(passwordHasher).verify(plainPassword, hashedPassword);
    }

    @Test
    void testLogin_UserNotFound() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            autenticacionService.login(testUser.getEmail(), plainPassword);
        });
        assertEquals("Credenciales inválidas", thrown.getMessage());
    }

    @Test
    void testLogin_IncorrectPassword() {
        // Arrange
        when(userRepository.findByEmail(testUser.getEmail())).thenReturn(Optional.of(testUser));
        when(passwordHasher.verify(plainPassword, hashedPassword)).thenReturn(false);

        // Act & Assert
        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () -> {
            autenticacionService.login(testUser.getEmail(), plainPassword);
        });
        assertEquals("Credenciales inválidas", thrown.getMessage());
    }
}
