package co.unicauca.gestiontrabajogrado;


import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;
import co.unicauca.gestiontrabajogrado.presentation.auth.login.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.auth.login.LoginController;
import co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
// Asume que tienes una implementación concreta del repositorio
// import co.unicauca.gestiontrabajogrado.infrastructure.repository.impl.UserRepositoryImpl;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;

import javax.swing.*;
import java.util.Optional;

/**
 * Clase principal de la aplicación
 * Configura la inyección de dependencias y inicia la aplicación
 *
 * @author Lyz
 */
public class Main {

    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        configureLookAndFeel();

        // Inicializar la aplicación en el Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                handleStartupError(e);
            }
        });
    }

    /**
     * Configura el aspecto visual de la aplicación
     */
    private static void configureLookAndFeel() {
        try {
            // Usar el Look and Feel del sistema operativo para mejor integración
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

            // Configuraciones adicionales de UI si es necesario
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

        } catch (Exception e) {
            System.err.println("Warning: No se pudo configurar el Look and Feel del sistema: " + e.getMessage());
            // Continuar con el Look and Feel por defecto
        }
    }

    /**
     * Inicializa la aplicación configurando las dependencias
     */
    private static void initializeApplication() {
        // 1. Configurar dependencias (Inyección de Dependencias manual)
        IUserRepository userRepository = createUserRepository();
        PasswordHasher passwordHasher = new PasswordHasher();
        IAutenticacionService autenticacionService = new AutenticacionService(userRepository, passwordHasher);

        // 2. Crear la vista de login
        LoginView loginView = new LoginView();

        // 3. Crear el controller con las dependencias
        LoginController loginController = new LoginController(autenticacionService, loginView);

        // 4. Conectar la vista con el controller
        loginView.setController(loginController);

        // 5. Mostrar la ventana de login
        loginView.setVisible(true);

        System.out.println("Aplicación iniciada correctamente");
    }

    /**
     * Crea el repositorio de usuarios
     * NOTA: Necesitas implementar tu repositorio concreto
     */
    private static IUserRepository createUserRepository() {
        // Opción 1: Si tienes una implementación de base de datos
        // return new UserRepositoryImpl();

        // Opción 2: Implementación temporal en memoria para desarrollo
        return new UserRepository();
    }

    /**
     * Maneja errores durante el inicio de la aplicación
     */
    private static void handleStartupError(Exception e) {
        String errorMessage = "Error al iniciar la aplicación: " + e.getMessage();
        System.err.println(errorMessage);
        e.printStackTrace();

        // Mostrar mensaje de error al usuario
        JOptionPane.showMessageDialog(
                null,
                "No se pudo iniciar la aplicación.\n" +
                        "Error: " + e.getMessage() + "\n\n" +
                        "Por favor, contacta al administrador del sistema.",
                "Error de Inicio",
                JOptionPane.ERROR_MESSAGE
        );

        System.exit(1);
    }

}