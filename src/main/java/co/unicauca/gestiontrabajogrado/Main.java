package co.unicauca.gestiontrabajogrado;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JOptionPane;

import co.unicauca.gestiontrabajogrado.controller.DashboardNavigator;
import co.unicauca.gestiontrabajogrado.controller.IDashBoardController;
import co.unicauca.gestiontrabajogrado.controller.LoginController;

import co.unicauca.gestiontrabajogrado.presentation.auth.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.common.ServiceManager;

import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;

// >>> Repositorios para proyectos / formato A
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IFormatoARepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.ProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.FormatoARepository;

// >>> Servicios de dominio
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService;

import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.domain.service.ProyectoGradoService;

import co.unicauca.gestiontrabajogrado.domain.service.IArchivoService;
import co.unicauca.gestiontrabajogrado.domain.service.ArchivoService;

// Utils
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.IEmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;
import co.unicauca.gestiontrabajogrado.util.IPasswordPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;

public class Main {

    public static void main(String[] args) {
        configureLookAndFeel();

        SwingUtilities.invokeLater(() -> {
            try {
                initializeApplication();
            } catch (Exception e) {
                handleStartupError(e);
            }
        });
    }

    private static void configureLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");
        } catch (Exception e) {
            System.err.println("Warning: Look&Feel no aplicado: " + e.getMessage());
        }
    }

    private static void initializeApplication() {
        // 1) DB
        DatabaseInitializer.ensureCreated();

        // 2) Autenticación (lo que ya tenías)
        IUserRepository userRepository = createUserRepository();
        PasswordHasher passwordHasher = new PasswordHasher();
        IEmailPolicy emailPolicy = EmailPolicy.getInstance();
        IPasswordPolicy passwordPolicy = PasswordPolicy.getInstance();
        IAutenticacionService autenticacionService = new AutenticacionService(
                userRepository, passwordHasher, emailPolicy, passwordPolicy
        );

        // Exponerlo si lo usas globalmente
        ServiceManager.getInstance().setAutenticacionService(autenticacionService);

        // 3) *** NUEVO *** Dependencias para proyectos (constructor requiere 3)
        IProyectoGradoRepository proyectoRepo = new ProyectoGradoRepository();
        IFormatoARepository formatoARepo     = new FormatoARepository();
        IArchivoService archivoService       = new ArchivoService();


        IProyectoGradoService proyectoService =
                new ProyectoGradoService(proyectoRepo, formatoARepo, archivoService, userRepository);

        // 4) *** NUEVO *** Navigator que usará el LoginController para redirigir
        IDashBoardController navigator = new DashboardNavigator(autenticacionService, proyectoService);

        // 5) Login (igual que antes) pero inyectando navigator
        LoginView loginView = new LoginView();
        LoginController loginController = new LoginController(autenticacionService, loginView);
        loginController.setNavigator(navigator);   // <<< IMPORTANTE
        loginView.setController(loginController);
        loginView.setVisible(true);

        System.out.println("Aplicación iniciada correctamente");
    }

    private static IUserRepository createUserRepository() {
        return new UserRepository(); // tu implementación en memoria / BD
    }

    private static void handleStartupError(Exception e) {
        String msg = "Error al iniciar la aplicación: " + e.getMessage();
        System.err.println(msg);
        e.printStackTrace();
        JOptionPane.showMessageDialog(
                null,
                "No se pudo iniciar la aplicación.\nError: " + e.getMessage(),
                "Error de Inicio",
                JOptionPane.ERROR_MESSAGE
        );
        System.exit(1);
    }
}
