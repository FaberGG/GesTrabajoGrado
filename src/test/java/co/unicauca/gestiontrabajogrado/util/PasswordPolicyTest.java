package co.unicauca.gestiontrabajogrado.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordPolicyTest {

    //Contraseña valida: Al menos 6 caracteres, una mayúscula, un dígito y un carácter especial
    @Test
    void testIsValid_CorrectPassword() {
        assertTrue(PasswordPolicy.isValid("Password@1"), "La contraseña 'Password@1' debería ser válida.");
    }
    // Contraseña inválida: Menos de 6 caracteres
    @Test
    void testIsValid_TooShort() {
        assertFalse(PasswordPolicy.isValid("Pwd@1"), "La contraseña 'Pwd@1' debería ser inválida (muy corta).");
    }
    // Contraseña inválida: Sin mayúsculas
    @Test
    void testIsValid_NoUpperCase() {
        assertFalse(PasswordPolicy.isValid("password@1"), "La contraseña 'password@1' debería ser inválida (sin mayúsculas).");
    }
    // Contraseña inválida: Sin dígitos
    @Test
    void testIsValid_NoDigit() {
        assertFalse(PasswordPolicy.isValid("Password@a"), "La contraseña 'Password@a' debería ser inválida (sin dígito).");
    }
    // Contraseña inválida: Sin caracteres especiales
    @Test
    void testIsValid_NoSpecialChar() {
        assertFalse(PasswordPolicy.isValid("Password123"), "La contraseña 'Password123' debería ser inválida (sin carácter especial).");
    }
    // Contraseña inválida: nula
    @Test
    void testIsValid_NullPassword() {
        assertFalse(PasswordPolicy.isValid(null), "La contraseña nula debería ser inválida.");
    }
    // Contraseña inválida: Vacía
    @Test
    void testIsValid_EmptyPassword() {
        assertFalse(PasswordPolicy.isValid(""), "La contraseña vacía debería ser inválida.");
    }

}