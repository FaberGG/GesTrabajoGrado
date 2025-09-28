package co.unicauca.gestiontrabajogrado.controller;

import javax.swing.SwingUtilities;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.presentation.auth.LoginView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;

/** Implementación concreta del router entre pantallas. */
public class DashboardNavigator implements IDashBoardController {

    private final IAutenticacionService authService;
    private final IProyectoGradoService proyectoService;

    public DashboardNavigator(IAutenticacionService authService,
                              IProyectoGradoService proyectoService) {
        this.authService = authService;
        this.proyectoService = proyectoService;
    }

    @Override
    public void openLogin() {
        SwingUtilities.invokeLater(() -> {
            // Creamos controller sin vista, luego vista, y se la inyectamos
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
            controller.inicializarVista();

            // Hooks de la vista
            view.setOnLogout(controller::handleCerrarSesion);
            view.setOnDescargarPlantilla(() -> {
                // Aquí conectas tu descarga real (ya dijiste que la tienen lista)
                // p.ej: plantillaService.descargarFormatoA();
            });

            view.setVisible(true);
        });
    }

    @Override
    public void openEstudiante(User user) {
        // Si ya tienen EstudianteView:
        // SwingUtilities.invokeLater(() -> new EstudianteView(user).setVisible(true));
        // Por ahora, para no romper flujo:
        openLogin();
    }

    @Override
    public void openAdmin(User user) {
        // Si usarán el panel de admin, llévenlo acá. Por ahora reusamos docente:
        openDocente(user);
    }
}
