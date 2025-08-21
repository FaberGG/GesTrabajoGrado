package co.unicauca.gestiontrabajogrado.util;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class EmailPolicyTest{
    /**
     * Prueba con un correo institucional válido.
     */
    @Test
    void testIsInstitutional_ValidEmail() {
        // Arrange
        String validEmail = "usuario@unicauca.edu.co";

        // Act & Assert
        assertTrue(EmailPolicy.isInstitutional(validEmail), "El email institucional debe ser válido.");
    }

    /**
     * Prueba con un correo institucional que contiene letras mayúsculas.
     */
    @Test
    void testIsInstitutional_ValidEmailWithUpperCase() {
        // Arrange
        String validEmail = "Usuario@Unicauca.edu.co";

        // Act & Assert
        assertTrue(EmailPolicy.isInstitutional(validEmail), "El email con mayúsculas debe ser válido.");
    }

    /**
     * Prueba con un correo de dominio incorrecto.
     */
    @Test
    void testIsInstitutional_IncorrectDomain() {
        // Arrange
        String incorrectDomainEmail = "usuario@gmail.com";

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(incorrectDomainEmail), "El email con dominio incorrecto debe ser inválido.");
    }

    /**
     * Prueba con un correo con un dominio similar pero incorrecto.
     */
    @Test
    void testIsInstitutional_SimilarButIncorrectDomain() {
        // Arrange
        String similarDomainEmail = "usuario@unicauca.com.co";

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(similarDomainEmail), "El email con un dominio similar debe ser inválido.");
    }

    /**
     * Prueba con una cadena nula.
     */
    @Test
    void testIsInstitutional_NullEmail() {
        // Arrange
        String nullEmail = null;

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(nullEmail), "Un email nulo debe ser inválido.");
    }

    /**
     * Prueba con una cadena vacía.
     */
    @Test
    void testIsInstitutional_EmptyEmail() {
        // Arrange
        String emptyEmail = "";

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(emptyEmail), "Un email vacío debe ser inválido.");
    }

    /**
     * Prueba con un email que no contiene el símbolo '@'.
     */
    @Test
    void testIsInstitutional_NoAtSymbol() {
        // Arrange
        String noAtSymbolEmail = "usuariounicauca.edu.co";

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(noAtSymbolEmail), "Un email sin '@' debe ser inválido.");
    }

    /**
     * Prueba con un email que tiene espacios en blanco al inicio o al final.
     */
    @Test
    void testIsInstitutional_WhitespaceEmail() {
        // Arrange
        String emailWithSpaces = "  usuario@unicauca.edu.co  ";

        // Act & Assert
        assertFalse(EmailPolicy.isInstitutional(emailWithSpaces), "Un email con espacios al inicio o final debe ser inválido.");
    }
}