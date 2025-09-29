package co.unicauca.gestiontrabajogrado.controller;

import javax.swing.SwingUtilities;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.presentation.auth.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview.CoordinadorView;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IFormatoARepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.ProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.FormatoARepository;
import co.unicauca.gestiontrabajogrado.domain.service.IUserService;

/** Implementación concreta del router entre pantallas. */
public class DashboardNavigator implements IDashBoardController {

    private final IAutenticacionService authService;
    private final IProyectoGradoService proyectoService;
    private final IUserService userService;
    private final IProyectoGradoRepository proyectoRepo;
    private final IFormatoARepository formatoRepo;

    public DashboardNavigator(IAutenticacionService authService,
                              IProyectoGradoService proyectoService, IUserService userService) {
        this.authService = authService;
        this.proyectoService = proyectoService;
        this.userService = userService;
        // Instanciar repositorios necesarios para el coordinador
        this.proyectoRepo = new ProyectoGradoRepository();
        this.formatoRepo = new FormatoARepository();
    }

    @Override
    public void openLogin() {
        SwingUtilities.invokeLater(() -> {
            LoginController loginController = new LoginController(authService, null);
            loginController.setNavigator(this);

            LoginView loginView = new LoginView(loginController);
            loginController.setLoginView(loginView);

            loginView.setVisible(true);
        });
    }

    @Override
    public void openDocente(User user) {
        SwingUtilities.invokeLater(() -> {
            // View
            DocenteView view = new DocenteView(user);

            // Controller
            DocenteController controller = new DocenteController(proyectoService, user);
            controller.setNavigator(this);
            controller.setDocenteView(view);
            view.setController(controller);
            controller.inicializarVista();

            // Hooks de la vista
            view.setOnLogout(controller::handleCerrarSesion);
            view.setOnDescargarPlantilla(() -> {
                // Aquí conectas tu descarga real
                System.out.println("Descargando plantilla Formato A...");
            });

            view.setVisible(true);
        });
    }

    @Override
    public void openEstudiante(User user) {
        SwingUtilities.invokeLater(() -> {
            // View sin parámetros
            EstudianteView view = new EstudianteView();

            // Controller
            EstudianteController controller = new EstudianteController(view, user, proyectoService, userService);
            controller.setNavigator(this);

            // Configurar la vista
            view.setUser(user);
            view.setController(controller);

            // Cargar datos del trabajo de grado
            controller.cargarDatosTrabajoGrado();

            view.setVisible(true);

            System.out.println("Vista de Estudiante abierta para: " + user.getNombres());
        });
    }

    @Override
    public void openAdmin(User user) {
        SwingUtilities.invokeLater(() -> {
            // View sin controller

            // Controller
            CoordinadorController controller = new CoordinadorController(
                    proyectoRepo,
                    formatoRepo,
                    proyectoService
            );
            controller.setNavigator(this);
            CoordinadorView view = new CoordinadorView(controller);

            // Conectar vista y controller
            view.setController(controller);

            // Cargar datos iniciales
            view.cargarPropuestas(false);

            view.setVisible(true);

            System.out.println("Vista de Coordinador abierta para: " + user.getNombres());
        });
    }
}