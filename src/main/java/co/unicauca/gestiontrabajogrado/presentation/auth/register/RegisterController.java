package co.unicauca.gestiontrabajogrado.presentation.auth.register;

import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.presentation.auth.login.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.auth.login.LoginController;

import javax.swing.*;
import java.util.regex.Pattern;

/**
 * Controlador para manejar la lógica de registro de usuarios
 * Separa la lógica de negocio de la vista
 *
 * @author Lyz
 */
public class RegisterController {

    private final IAutenticacionService autenticacionService;
    private final RegisterView registerView;
    private final LoginController loginController; // Referencia al controlador de login

    // Patrón para validar email institucional (ejemplo)
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9._%+-]+@unicauca\\.edu\\.co$"
    );

    // Patrón para validar número de celular (opcional, formato colombiano)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
            "^[0-9]{10}$"
    );

    public RegisterController(IAutenticacionService autenticacionService, RegisterView registerView, LoginController loginController) {
        this.autenticacionService = autenticacionService;
        this.registerView = registerView;
        this.loginController = loginController;
    }

    /**
     * Maneja el proceso de registro de usuario
     */
    public void handleRegister(String nombres, String apellidos, String celular,
                               enumProgram programa, enumRol rol, String email,
                               String password, String confirmPassword) {
        try {
            // Validar campos obligatorios
            validateRequiredFields(nombres, apellidos, programa, rol, email, password, confirmPassword);

            // Validar formato de email
            validateEmail(email);

            // Validar contraseña
            validatePassword(password, confirmPassword);

            // Validar celular si se proporciona
            if (celular != null && !celular.isEmpty()) {
                validateCelular(celular);
            }

            // Crear objeto User
            User newUser = new User(
                    null, // ID será asignado por la base de datos
                    nombres,
                    apellidos,
                    celular.isEmpty() ? null : celular,
                    programa,
                    rol,
                    email,
                    null // passwordHash será generado por el servicio
            );

            // Registrar usuario usando el servicio
            User registeredUser = autenticacionService.register(newUser, password);

            // Preparar nombre completo para el mensaje
            String nombreCompleto = registeredUser.getNombres() + " " + registeredUser.getApellidos();

            // Mostrar mensaje de éxito breve
            showSuccess("¡Registro exitoso!");

            // Redirigir al login con mensaje de bienvenida
            redirectToLoginWithSuccess(nombreCompleto);

        } catch (IllegalArgumentException e) {
            // Errores de validación
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            // Errores inesperados
            showError("Ha ocurrido un error inesperado durante el registro. Por favor, intenta nuevamente.");
            System.err.println("Error en registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida que todos los campos obligatorios estén completos
     */
    private void validateRequiredFields(String nombres, String apellidos, enumProgram programa,
                                        enumRol rol, String email, String password, String confirmPassword) {
        if (nombres == null || nombres.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Nombres' es obligatorio.");
        }

        if (apellidos == null || apellidos.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Apellidos' es obligatorio.");
        }

        if (programa == null) {
            throw new IllegalArgumentException("Debe seleccionar un programa académico.");
        }

        if (rol == null) {
            throw new IllegalArgumentException("Debe seleccionar un rol.");
        }

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El campo 'Email Institucional' es obligatorio.");
        }

        if (password == null || password.isEmpty()) {
            throw new IllegalArgumentException("El campo 'Contraseña' es obligatorio.");
        }

        if (confirmPassword == null || confirmPassword.isEmpty()) {
            throw new IllegalArgumentException("Debe confirmar su contraseña.");
        }
    }

    /**
     * Valida el formato del email institucional
     */
    private void validateEmail(String email) {
        if (!EMAIL_PATTERN.matcher(email).matches()) {
            throw new IllegalArgumentException("Debe usar un email institucional válido (@unicauca.edu.co).");
        }
    }

    /**
     * Valida la contraseña y su confirmación
     */
    private void validatePassword(String password, String confirmPassword) {
        if (password.length() < 6) {
            throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres.");
        }

        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Las contraseñas no coinciden.");
        }

        // Validaciones adicionales de seguridad (opcional)
        if (!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra mayúscula.");
        }

        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos una letra minúscula.");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new IllegalArgumentException("La contraseña debe contener al menos un número.");
        }
    }

    /**
     * Valida el formato del número de celular
     */
    private void validateCelular(String celular) {
        if (!PHONE_PATTERN.matcher(celular).matches()) {
            throw new IllegalArgumentException("El número de celular debe tener 10 dígitos (formato colombiano).");
        }
    }

    /**
     * Valida que los nombres solo contengan letras y espacios
     */
    private void validateNombres(String nombres) {
        if (!nombres.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("Los nombres solo pueden contener letras y espacios.");
        }

        if (nombres.trim().length() < 2) {
            throw new IllegalArgumentException("Los nombres deben tener al menos 2 caracteres.");
        }
    }

    /**
     * Valida que los apellidos solo contengan letras y espacios
     */
    private void validateApellidos(String apellidos) {
        if (!apellidos.matches("^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+$")) {
            throw new IllegalArgumentException("Los apellidos solo pueden contener letras y espacios.");
        }

        if (apellidos.trim().length() < 2) {
            throw new IllegalArgumentException("Los apellidos deben tener al menos 2 caracteres.");
        }
    }

    /**
     * Redirige al login después del registro exitoso
     */
    private void redirectToLogin() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cerrar la ventana de registro
                registerView.dispose();

                // Usar el LoginController para manejar el retorno
                if (loginController != null) {
                    loginController.handleVolverDesdeRegistro();
                } else {
                    // Fallback si no hay referencia al LoginController
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                }

            } catch (Exception e) {
                showError("Error al abrir la ventana de login. Por favor, reinicia la aplicación.");
                System.err.println("Error al abrir LoginView: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Redirige al login después del registro exitoso con mensaje de bienvenida
     */
    private void redirectToLoginWithSuccess(String nombreCompleto) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cerrar la ventana de registro
                registerView.dispose();

                // Usar el LoginController para manejar el retorno exitoso
                if (loginController != null) {
                    loginController.handleRegistroExitoso(nombreCompleto);
                } else {
                    // Fallback si no hay referencia al LoginController
                    LoginView loginView = new LoginView();
                    loginView.setVisible(true);
                    SwingUtilities.invokeLater(() -> {
                        loginView.showSuccess("¡Registro completado! Ya puedes iniciar sesión.");
                    });
                }

            } catch (Exception e) {
                showError("Error al abrir la ventana de login. Por favor, reinicia la aplicación.");
                System.err.println("Error al abrir LoginView: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Maneja la funcionalidad "Volver al Login"
     */
    public void handleVolverLogin() {
        int option = JOptionPane.showConfirmDialog(
                registerView,
                "¿Estás seguro de que quieres volver al login?\n" +
                        "Se perderá la información ingresada.",
                "Confirmar",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            SwingUtilities.invokeLater(() -> {
                try {
                    // Cerrar la ventana de registro
                    registerView.dispose();

                    // Usar el LoginController para manejar el retorno
                    if (loginController != null) {
                        loginController.handleVolverDesdeRegistro();
                    } else {
                        // Fallback si no hay referencia al LoginController
                        LoginView loginView = new LoginView();
                        loginView.setVisible(true);
                    }

                } catch (Exception e) {
                    showError("Error al volver al login. Por favor, reinicia la aplicación.");
                    System.err.println("Error al volver a LoginView: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        }
    }

    /**
     * Limpia todos los campos del formulario
     */
    public void clearForm() {
        SwingUtilities.invokeLater(() -> {
            // Nota: Este método requeriría métodos adicionales en RegisterView
            // para limpiar los campos, o podría implementarse directamente en la vista
        });
    }

    /**
     * Valida el dominio del email para asegurar que sea institucional
     */
    private void validateEmailDomain(String email) {
        String[] validDomains = {"@unicauca.edu.co", "@estudiante.unicauca.edu.co"};
        boolean validDomain = false;

        for (String domain : validDomains) {
            if (email.toLowerCase().endsWith(domain)) {
                validDomain = true;
                break;
            }
        }

        if (!validDomain) {
            throw new IllegalArgumentException("Debe usar un email institucional de la Universidad del Cauca.");
        }
    }

    /**
     * Valida que el rol seleccionado sea apropiado según el email
     * Por ejemplo, emails de estudiante podrían tener restricciones
     */
    private void validateRolByEmail(String email, enumRol rol) {
        // Esta validación es opcional y depende de las reglas de negocio
        if (email.contains("estudiante") && rol == enumRol.DOCENTE) {
            throw new IllegalArgumentException("No puedes registrarte como docente con un email de estudiante.");
        }
    }

    /**
     * Método mejorado de handleRegister con validaciones adicionales
     */
    public void handleRegisterWithExtraValidations(String nombres, String apellidos, String celular,
                                                   enumProgram programa, enumRol rol, String email,
                                                   String password, String confirmPassword) {
        try {
            // Validaciones básicas
            validateRequiredFields(nombres, apellidos, programa, rol, email, password, confirmPassword);

            // Validaciones adicionales de formato
            validateNombres(nombres);
            validateApellidos(apellidos);
            validateEmailDomain(email);
            validatePassword(password, confirmPassword);
            validateRolByEmail(email, rol);

            // Validar celular si se proporciona
            if (celular != null && !celular.isEmpty()) {
                validateCelular(celular);
            }

            // Crear objeto User
            User newUser = new User(
                    null,
                    nombres.trim(),
                    apellidos.trim(),
                    celular == null || celular.trim().isEmpty() ? null : celular.trim(),
                    programa,
                    rol,
                    email.trim().toLowerCase(),
                    null
            );

            // Registrar usuario
            User registeredUser = autenticacionService.register(newUser, password);

            // Mostrar mensaje de éxito
            showSuccess("¡Registro exitoso!\n" +
                    "Bienvenido, " + registeredUser.getNombres() + " " + registeredUser.getApellidos() + ".\n" +
                    "Ya puedes iniciar sesión con tu cuenta.");

            // Redirigir al login
            redirectToLogin();

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Ha ocurrido un error durante el registro. Por favor, intenta nuevamente.");
            System.err.println("Error en registro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Métodos de utilidad para mostrar mensajes
    private void showError(String message) {
        JOptionPane.showMessageDialog(registerView, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(registerView, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showWarning(String message) {
        JOptionPane.showMessageDialog(registerView, message, "Advertencia", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Método para validar disponibilidad de email (requiere implementación en el servicio)
     */
    private void validateEmailAvailability(String email) {
        // Esta funcionalidad requeriría un método adicional en IAutenticacionService
        // como: boolean isEmailAvailable(String email)

        // Por ahora se deja como comentario para implementación futura
        /*
        try {
            if (!autenticacionService.isEmailAvailable(email)) {
                throw new IllegalArgumentException("Este email ya está registrado en el sistema.");
            }
        } catch (Exception e) {
            System.err.println("Error al validar disponibilidad de email: " + e.getMessage());
            // En caso de error en la validación, permitir continuar
        }
        */
    }
}
// No se requiere cambio, cumple SRP y DIP.
