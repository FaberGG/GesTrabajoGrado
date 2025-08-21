package co.unicauca.gestiontrabajogrado.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class PasswordHasher {
    /**
     * Genera el hash de una contraseña usando SHA-256.
     */
    public String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest(password.getBytes());
            return Base64.getEncoder().encodeToString(encodedHash);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al generar el hash", e);
        }
    }

    /**
     * Verifica si la contraseña ingresada coincide con la almacenada.
     */
    public boolean verify(String password, String hashedPassword) {
        String newHash = hash(password);
        return newHash.equals(hashedPassword);
    }
}
