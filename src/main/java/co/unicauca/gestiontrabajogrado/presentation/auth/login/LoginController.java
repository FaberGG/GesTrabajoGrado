package co.unicauca.gestiontrabajogrado.presentation.auth.login;


import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;
import co.unicauca.gestiontrabajogrado.presentation.auth.register.RegisterView;
import co.unicauca.gestiontrabajogrado.presentation.auth.register.RegisterController;

import javax.swing.*;

/**
 * Controlador para manejar la lógica de autenticación del login
 * Separa la lógica de negocio de la vista
 *
 * @author Lyz
 */
public class LoginController {

    private final IAutenticacionService autenticacionService;
    private LoginView loginView;

    public LoginController(IAutenticacionService autenticacionService, LoginView loginView) {
        this.autenticacionService = autenticacionService;
        this.loginView = loginView;
    }

    /**
     * Permite actualizar la referencia de la vista (útil cuando se vuelve del registro)
     */
    public void setLoginView(LoginView loginView) {
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
     * Maneja la navegación hacia la vista de registro
     * Cierra la vista actual y abre la vista de registro
     */
    public void handleRegistrarse() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Cerrar la ventana de login
                loginView.dispose();

                // Crear la vista de registro
                RegisterView registerView = new RegisterView();

                // Crear el controlador de registro con una referencia a este controlador
                RegisterController registerController = new RegisterController(
                        autenticacionService,
                        registerView,
                        this  // Pasar referencia del LoginController
                );

                registerView.setController(registerController);
                registerView.setVisible(true);

            } catch (Exception e) {
                showError("Error al abrir la ventana de registro. Por favor, intenta nuevamente.");
                System.err.println("Error al abrir RegisterView: " + e.getMessage());
                e.printStackTrace();

                // Si hay error, mantener la ventana de login abierta
                if (loginView != null && !loginView.isDisplayable()) {
                    loginView = new LoginView(this);
                    loginView.setVisible(true);
                }
            }
        });
    }

    /**
     * Maneja el retorno desde la vista de registro al login
     * Este método será llamado desde RegisterController
     */
    public void handleVolverDesdeRegistro() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear nueva instancia de LoginView
                loginView = new LoginView(this);
                loginView.setVisible(true);

            } catch (Exception e) {
                showError("Error al volver al login. Por favor, reinicia la aplicación.");
                System.err.println("Error al volver a LoginView: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * Maneja el retorno exitoso desde registro
     * Muestra el login con un mensaje de éxito
     */
    public void handleRegistroExitoso(String nombreUsuario) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Crear nueva instancia de LoginView
                loginView = new LoginView(this);
                loginView.setVisible(true);

                // Mostrar mensaje de bienvenida
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
                        handleVolverDesdeRegistro();
                        break;
                }

            } catch (Exception e) {
                showError("Error al abrir el dashboard. Por favor, intenta nuevamente.");
                System.err.println("Error al abrir dashboard: " + e.getMessage());
                e.printStackTrace();

                // Reabrir login si hay error
                handleVolverDesdeRegistro();
            }
        });
    }

    /**
     * Maneja la funcionalidad "recordarme"
     * @param email Email a recordar
     */
    private void handleRememberMe(String email) {
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
     * Obtiene la instancia del servicio de autenticación
     * Útil para pasarlo al RegisterController
     */
    public IAutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    // Métodos de utilidad para mostrar mensajes
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
// No se requiere cambio, cumple SRP y DIP.
