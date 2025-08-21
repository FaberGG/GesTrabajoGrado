package co.unicauca.gestiontrabajogrado.util;

public final class EmailPolicy {
    private EmailPolicy() {}
    public static boolean isInstitutional(String email) {
        return email != null && email.toLowerCase().endsWith("@unicauca.edu.co");
    }
}
