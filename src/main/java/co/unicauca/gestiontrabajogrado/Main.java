package co.unicauca.gestiontrabajogrado;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;
import co.unicauca.gestiontrabajogrado.presentation.auth.LoginView;
import co.unicauca.gestiontrabajogrado.controller.LoginController;
import co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;
import co.unicauca.gestiontrabajogrado.domain.service.*;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;
import co.unicauca.gestiontrabajogrado.util.IEmailPolicy;
import co.unicauca.gestiontrabajogrado.util.IPasswordPolicy;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;

import javax.swing.*;

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
        // 1. Inicializar la base de datos
        DatabaseInitializer.ensureCreated();
        // 2. Crear repositorios (usar las implementaciones existentes)
        IUserRepository userRepository = createUserRepository();
        IProyectoGradoRepository proyectoGradoRepository = createProyectoGradoRepository();
        IFormatoARepository formatoARepository = createFormatoARepository();

        // 3. Crear servicios auxiliares
        PasswordHasher passwordHasher = new PasswordHasher();
        IEmailPolicy emailPolicy = EmailPolicy.getInstance();
        IPasswordPolicy passwordPolicy = PasswordPolicy.getInstance();
        IArchivoService archivoService = createArchivoService();

        // 4. Crear servicios principales
        IAutenticacionService autenticacionService = new AutenticacionService(
                userRepository, passwordHasher, emailPolicy, passwordPolicy
        );

        IProyectoGradoService proyectoGradoService = new ProyectoGradoService(
                proyectoGradoRepository,
                formatoARepository,
                archivoService,
                userRepository
        );

        // 5. Configurar ServiceLocator con todas las dependencias
        ServiceLocator.getInstance().configure(
                userRepository,
                proyectoGradoRepository,
                formatoARepository,
                autenticacionService,
                proyectoGradoService,
                archivoService
        );

        // 6. Crear y configurar la vista de login
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(autenticacionService, loginView);
        loginView.setController(loginController);

        // 7. Mostrar la aplicación
        loginView.setVisible(true);
    }

    /**
     * Crea el repositorio de usuarios
     */
    private static IUserRepository createUserRepository() {
        // Usar tu implementación existente
        return new UserRepository();
    }

    /**
     * Crea el repositorio de proyectos de grado
     */
    private static IProyectoGradoRepository createProyectoGradoRepository() {
        // Usar tu implementación existente
        return new ProyectoGradoRepository();
    }

    /**
     * Crea el repositorio de formato A
     */
    private static IFormatoARepository createFormatoARepository() {
        // Usar tu implementación existente
        return new FormatoARepository();
    }

    /**
     * Crea el servicio de archivos
     */
    private static IArchivoService createArchivoService() {
        // Usar tu implementación existente o crear una básica
        return new ArchivoService();
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