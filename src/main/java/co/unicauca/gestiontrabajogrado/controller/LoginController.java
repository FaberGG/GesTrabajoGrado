package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.presentation.auth.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;
import co.unicauca.gestiontrabajogrado.presentation.auth.RegisterView;

import javax.swing.*;

/**
 * Controlador para manejar la lógica de autenticación del login.
 * Separa la lógica de negocio de la vista.
 */
public class LoginController {

    private final IAutenticacionService autenticacionService;
    private LoginView loginView;

    // >>> NUEVO: navegador central
    private IDashBoardController navigator;
    public void setNavigator(IDashBoardController navigator) { this.navigator = navigator; }

    public LoginController(IAutenticacionService autenticacionService, LoginView loginView) {
        this.autenticacionService = autenticacionService;
        this.loginView = loginView;
    }

    /** Permite actualizar la referencia de la vista (útil cuando se vuelve del registro) */
    public void setLoginView(LoginView loginView) {
        this.loginView = loginView;
    }

    /** Maneja el proceso de autenticación */
    public void handleLogin(String email, String password, boolean rememberMe) {
        try {
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

            if (rememberMe) {
                handleRememberMe(email);
            }

            showSuccess("Inicio de sesión exitoso. ¡Bienvenido, " + authenticatedUser.getNombres() + "!");
            redirectToDashboard(authenticatedUser);

        } catch (IllegalArgumentException e) {
            showError("Error: " + e.getMessage());
        } catch (Exception e) {
            showError("Ha ocurrido un error inesperado. Por favor, intenta nuevamente.");
            System.err.println("Error en login: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Navegación hacia la vista de registro */
    public void handleRegistrarse() {
        SwingUtilities.invokeLater(() -> {
            try {
                if (loginView != null) loginView.dispose();

                RegisterView registerView = new RegisterView();

                RegisterController registerController = new RegisterController(
                        autenticacionService,
                        registerView,
                        this
                );

                registerView.setController(registerController);
                registerView.setVisible(true);

            } catch (Exception e) {
                showError("Error al abrir la ventana de registro. Por favor, intenta nuevamente.");
                System.err.println("Error al abrir RegisterView: " + e.getMessage());
                e.printStackTrace();

                if (loginView != null && !loginView.isDisplayable()) {
                    loginView = new LoginView(this);
                    loginView.setVisible(true);
                }
            }
        });
    }

    /** Maneja el retorno desde la vista de registro al login */
    public void handleVolverDesdeRegistro() {
        SwingUtilities.invokeLater(() -> {
            try {
                loginView = new LoginView(this);
                loginView.setVisible(true);
            } catch (Exception e) {
                showError("Error al volver al login. Por favor, reinicia la aplicación.");
                System.err.println("Error al volver a LoginView: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /** Retorno exitoso desde registro */
    public void handleRegistroExitoso(String nombreUsuario) {
        SwingUtilities.invokeLater(() -> {
            try {
                loginView = new LoginView(this);
                loginView.setVisible(true);

                SwingUtilities.invokeLater(() -> {
                    loginView.showSuccess(
                            "¡Registro completado exitosamente!\n" +
                            "Bienvenido, " + nombreUsuario + ".\n" +
                            "Ya puedes iniciar sesión con tu nueva cuenta."
                    );
                });

            } catch (Exception e) {
                showError("Error al volver al login. Por favor, reinicia la aplicación.");
                System.err.println("Error al volver a LoginView después de registro: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /** Redirige al dashboard apropiado según el tipo de usuario (usa el navegador si está configurado) */
    private void redirectToDashboard(User user) {
        SwingUtilities.invokeLater(() -> {
            try {
                if (loginView != null) loginView.dispose();

                if (navigator != null) {
                    switch (user.getRol()) {
                        case DOCENTE:    navigator.openDocente(user);    break;
                        case ESTUDIANTE: navigator.openEstudiante(user); break;
                        case ADMIN:      navigator.openAdmin(user);      break;
                        default:
                            showError("Tipo de usuario no reconocido: " + user.getRol());
                            handleVolverDesdeRegistro();
                    }
                    return;
                }

                // Fallback si aún no configuraste el navegador (para no romper flujo)
                switch (user.getRol()) {
                    case DOCENTE:
                        new DocenteView(user).setVisible(true);
                        break;
                    case ESTUDIANTE:
                        new EstudianteView(user).setVisible(true);
                        break;
                    case ADMIN:
                        new DocenteView(user).setVisible(true);
                        break;
                    default:
                        showError("Tipo de usuario no reconocido: " + user.getRol());
                        handleVolverDesdeRegistro();
                        break;
                }

            } catch (Exception e) {
                showError("Error al abrir el dashboard. Por favor, intenta nuevamente.");
                System.err.println("Error al abrir dashboard: " + e.getMessage());
                e.printStackTrace();
                handleVolverDesdeRegistro();
            }
        });
    }

    /** "Recordarme" */
    private void handleRememberMe(String email) {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
            prefs.put("rememberedEmail", email);
            prefs.putBoolean("rememberEmail", true);
        } catch (Exception e) {
            System.err.println("Error al guardar email recordado: " + e.getMessage());
        }
    }

    /** Obtener email recordado */
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

    /** Limpiar recordatorio */
    public void clearRememberedData() {
        try {
            java.util.prefs.Preferences prefs = java.util.prefs.Preferences.userNodeForPackage(LoginController.class);
            prefs.remove("rememberedEmail");
            prefs.putBoolean("rememberEmail", false);
        } catch (Exception e) {
            System.err.println("Error al limpiar datos recordados: " + e.getMessage());
        }
    }

    public IAutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    // Utilidades UI
    private void showError(String message) {
        if (loginView != null) {
            JOptionPane.showMessageDialog(loginView, message, "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void showSuccess(String message) {
        if (loginView != null) {
            JOptionPane.showMessageDialog(loginView, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
