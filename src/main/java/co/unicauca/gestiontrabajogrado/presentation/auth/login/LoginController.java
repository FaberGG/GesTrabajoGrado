package co.unicauca.gestiontrabajogrado.presentation.auth.login;


import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;

import javax.swing.*;

/**
 * Controlador para manejar la lógica de autenticación del login
 * Separa la lógica de negocio de la vista
 *
 * @author Lyz
 */
public class LoginController {

    private final IAutenticacionService autenticacionService;
    private final LoginView loginView;

    public LoginController(IAutenticacionService autenticacionService, LoginView loginView) {
        this.autenticacionService = autenticacionService;
        this.loginView = loginView;
    }

    /**
     * Maneja el proceso de autenticación
     * @param email Email ingresado por el usuario
     * @param password Contraseña ingresada por el usuario
     * @param rememberMe Si el usuario marcó "recordarme"
     */
    public void handleLogin(String email, String password, boolean rememberMe) {
        try {
            // Validaciones básicas en el controlador
            if (email == null || email.trim().isEmpty()) {
                showError("Por favor, ingresa tu correo electrónico.");
                return;
            }

            if (password == null || password.isEmpty()) {
                showError("Por favor, ingresa tu contraseña.");
                return;
            }

            // Intentar autenticar usando el servicio
            User authenticatedUser = autenticacionService.login(email.trim(), password);

            // Manejo del "recordarme" si es necesario
            if (rememberMe) {
                handleRememberMe(email);
            }

            // Login exitoso - mostrar mensaje y redirigir
            showSuccess("Inicio de sesión exitoso. ¡Bienvenido, " + authenticatedUser.getNombres() + "!");

            // Redirigir según el tipo de usuario
            redirectToDashboard(authenticatedUser);

        } catch (IllegalArgumentException e) {
            // Errores de validación o credenciales inválidas
            showError("Error: " + e.getMessage());
        } catch (Exception e) {
            // Errores inesperados
            showError("Ha ocurrido un error inesperado. Por favor, intenta nuevamente.");
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Redirige al dashboard apropiado según el tipo de usuario
     * @param user Usuario autenticado
     */
    private void redirectToDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cerrar la ventana de login
                loginView.dispose();

                // Abrir dashboard según el rol del usuario
                switch (user.getRol()) {
                    case DOCENTE:
                        new DocenteView(user).setVisible(true);
                        break;

                    case ESTUDIANTE:
                        new EstudianteView(user).setVisible(true);
                        break;

                    case ADMIN:
                        new DocenteView(user).setVisible(true); // Por ahora usa docente
                        break;

                    default:
                        showError("Tipo de usuario no reconocido: " + user.getRol());
                        // Reabrir login si hay error
                        new LoginView().setVisible(true);
                        break;
                }

            } catch (Exception e) {
                showError("Error al abrir el dashboard. Por favor, intenta nuevamente.");
                System.err.println("Error al abrir dashboard: " + e.getMessage());
                e.printStackTrace();

                // Reabrir login si hay error
                new LoginView().setVisible(true);
            }
        });
    }

    /**
     * Maneja la funcionalidad "recordarme"
     * @param email Email a recordar
     */
    private void handleRememberMe(String email) {
        // Aquí puedes implementar la lógica para recordar credenciales
        // Por ejemplo, guardar en un archivo de configuración (NO la contraseña)
        // o usar Java Preferences API

        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
            prefs.put("rememberedEmail", email);
            prefs.putBoolean("rememberEmail", true);
        } catch (Exception e) {
            System.err.println("Error al guardar email recordado: " + e.getMessage());
        }
    }

    /**
     * Obtiene el email recordado si existe
     * @return Email recordado o cadena vacía
     */
    public String getRememberedEmail() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
            if (prefs.getBoolean("rememberEmail", false)) {
                return prefs.get("rememberedEmail", "");
            }
        } catch (Exception e) {
            System.err.println("Error al obtener email recordado: " + e.getMessage());
        }
        return "";
    }

    /**
     * Limpia los datos recordados
     */
    public void clearRememberedData() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
            prefs.remove("rememberedEmail");
            prefs.putBoolean("rememberEmail", false);
        } catch (Exception e) {
            System.err.println("Error al limpiar datos recordados: " + e.getMessage());
        }
    }

    /**
     * Maneja la funcionalidad "Registrarse"
     */
    public void handleRegistrarse() {
        JOptionPane.showMessageDialog(
                loginView,
                "Funcionalidad de registrar en desarrollo.\n" +
                        "Por favor, contacta al administrador del sistema.",
                "Registrarse",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    // Métodos de utilidad para mostrar mensajes
    private void showError(String message) {
        JOptionPane.showMessageDialog(loginView, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(loginView, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}