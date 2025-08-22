package co.unicauca.gestiontrabajogrado.util;

public final class EmailPolicy implements IEmailPolicy {
    private EmailPolicy() {}
    @Override
    public boolean isInstitutional(String email) {
        return email != null && email.toLowerCase().endsWith("@unicauca.edu.co");
    }
    public static EmailPolicy getInstance() {
        return new EmailPolicy();
    }
}
