package co.unicauca.gestiontrabajogrado.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PasswordHasherTest {

    private final PasswordHasher hasher = new PasswordHasher();

    /**
     * Prueba el metodo hash()
     */
    @Test
    void testHash() {
        // Arrange
        String password = "password123";

        // Act
        String hashedPassword = hasher.hash(password);

        // Assert
        assertNotNull(hashedPassword, "El hash no debería ser nulo");
        assertNotEquals(password, hashedPassword, "El hash debe ser diferente de la contraseña original");
        assertFalse(hashedPassword.isEmpty(), "El hash no debería estar vacío");
    }

    /**
     * Prueba que el metodo hash() siempre produce el mismo hash para la misma contraseña.
     */
    @Test
    void testHashConsistency() {
        // Arrange
        String password = "ConSisTen7_pa55w0rd";

        // Act
        // Generamos el hash dos veces
        String hash1 = hasher.hash(password);
        String hash2 = hasher.hash(password);

        // Assert
        assertEquals(hash1, hash2, "El hash debería ser consistente para la misma entrada");
    }

    /**
     * Prueba que el metodo hash() produce diferentes hashes para diferentes contraseñas.
     */
    @Test
    void testHashForDifferentPasswords() {
        // Arrange
        String passwordA = "passwordA";
        String passwordB = "passwordB";

        // Act
        String hashA = hasher.hash(passwordA);
        String hashB = hasher.hash(passwordB);

        // Assert
        assertNotEquals(hashA, hashB, "Diferentes contraseñas deben tener diferentes hashes");
    }

    /**
     * Prueba el metodo verify() con una contraseña correcta.
     */
    @Test
    void testVerifySuccess() {
        // Arrange
        String password = "correct_password";
        String hashedPassword = hasher.hash(password);

        // Act & Assert
        assertTrue(hasher.verify(password, hashedPassword), "La verificación debe ser exitosa con la contraseña correcta");
    }

    /**
     * Prueba el metodo verify() con una contraseña incorrecta.
     */
    @Test
    void testVerifyFailure() {
        // Arrange
        String password = "correct_password";
        String wrongPassword = "incorrect_password";
        String hashedPassword = hasher.hash(password);

        // Act & Assert
        assertFalse(hasher.verify(wrongPassword, hashedPassword), "La verificacion debe fallar con una contraseña incorrecta");
    }

    /**
     * Prueba el metodo verify() con una contraseña incorrecta que tiene espacios en blanco.
     */
    @Test
    void testVerifyWithIncorrectPasswordAndWhitespace() {
        // Arrange
        String password = "incorrect_password";
        String wrongPassword = " incorrect_password ";
        String hashedPassword = hasher.hash(password);

        // Act & Assert
        assertFalse(hasher.verify(wrongPassword, hashedPassword), "La verificacion debe fallar con una contraseña incorrecta incluso con espacios en blanco");
    }
}