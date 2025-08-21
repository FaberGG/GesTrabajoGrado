package co.unicauca.gestiontrabajogrado.util;

import java.util.regex.Pattern;

public final class PasswordPolicy {
    // min 6, al menos 1 dígito, 1 mayúscula y 1 carácter especial
    private static final Pattern RE =
            Pattern.compile("^(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{6,}$");

    private PasswordPolicy() {}

    public static boolean isValid(String pwd) {
        return pwd != null && RE.matcher(pwd).matches();
    }
}
