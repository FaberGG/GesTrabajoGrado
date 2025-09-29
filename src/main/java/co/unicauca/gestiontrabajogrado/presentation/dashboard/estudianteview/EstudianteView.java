package co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.presentation.common.HeaderPanel;
import co.unicauca.gestiontrabajogrado.presentation.common.UIConstants;
import co.unicauca.gestiontrabajogrado.controller.EstudianteController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class EstudianteView extends JFrame {
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private EstudianteController controller;
    private User currentUser;

    // Nombres de las vistas
    public static final String DASHBOARD_VIEW = "dashboard";
    public static final String TRABAJO_GRADO_VIEW = "trabajo_grado";

    public EstudianteView(User user) {
        super("Panel del Estudiante");
        this.currentUser = user;
        this.controller = new EstudianteController(this, user);

        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(1100, 700));
        setLocationRelativeTo(null);

        // Configurar CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
    }

    private void setupLayout() {
        // Root panel
        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(UIConstants.BG_APP);
        setContentPane(root);

        // Encabezado
        HeaderPanel header = new HeaderPanel();
        root.add(header, BorderLayout.NORTH);

        // Cuerpo principal
        JPanel body = new JPanel(new BorderLayout());
        body.setBorder(new EmptyBorder(18, 18, 18, 18));
        body.setBackground(UIConstants.BG_APP);
        root.add(body, BorderLayout.CENTER);

        // Agregar las vistas al CardLayout
        contentPanel.add(new EstudianteDashboardPanel(currentUser, this), DASHBOARD_VIEW);
        contentPanel.add(new EstudianteTrabajoGradoPanel(controller, this), TRABAJO_GRADO_VIEW);

        body.add(contentPanel, BorderLayout.CENTER);

        // Mostrar vista inicial
        showView(DASHBOARD_VIEW);
    }

    public void showView(String viewName) {
        cardLayout.show(contentPanel, viewName);
    }

    public void showTrabajoGradoView() {
        // Cargar datos del trabajo de grado antes de mostrar la vista
        System.out.println("DEBUG: Mostrando vista de trabajo de grado");
        controller.cargarDatosTrabajoGrado();

        // Recrear el panel con los datos actualizados
        contentPanel.remove(1); // Remover el panel anterior
        contentPanel.add(new EstudianteTrabajoGradoPanel(controller, this), TRABAJO_GRADO_VIEW);

        showView(TRABAJO_GRADO_VIEW);
        System.out.println("DEBUG: Vista cambiada a trabajo de grado");
    }

    public EstudianteController getController() {
        return controller;
    }

    public static void main(String args[]) {
        try {
            //UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception ignored) { }

        SwingUtilities.invokeLater(() -> {
            new EstudianteView(new User()).setVisible(true);
        });
    }
}