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

    // Constructor sin parámetros para compatibilidad con DashboardNavigator
    public EstudianteView() {
        super("Panel del Estudiante");
        initializeComponents();
        setupLayout();
    }

    // Constructor con usuario (mantener para compatibilidad con código existente)
    public EstudianteView(User user) {
        this();
        setUser(user);
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

        body.add(contentPanel, BorderLayout.CENTER);
    }

    /**
     * Configura el usuario y reconstruye las vistas
     */
    public void setUser(User user) {
        this.currentUser = user;
        rebuildViews();
    }

    /**
     * Configura el controller
     */
    public void setController(EstudianteController controller) {
        this.controller = controller;
        rebuildViews();
    }

    /**
     * Reconstruye las vistas con el usuario y controller actuales
     */
    private void rebuildViews() {
        if (currentUser == null || controller == null) {
            return; // Esperar a que ambos estén configurados
        }

        contentPanel.removeAll();
        contentPanel.add(new EstudianteDashboardPanel(currentUser, this), DASHBOARD_VIEW);
        contentPanel.add(new EstudianteTrabajoGradoPanel(controller, this), TRABAJO_GRADO_VIEW);

        // Mostrar vista inicial
        showView(DASHBOARD_VIEW);

        contentPanel.revalidate();
        contentPanel.repaint();
    }

    public void showView(String viewName) {
        if (cardLayout != null && contentPanel != null) {
            cardLayout.show(contentPanel, viewName);
        }
    }

    public void showTrabajoGradoView() {
        if (controller == null) {
            System.err.println("ERROR: Controller no configurado");
            return;
        }

        System.out.println("DEBUG: Mostrando vista de trabajo de grado");
        controller.cargarDatosTrabajoGrado();

        // Recrear el panel con los datos actualizados
        Component[] components = contentPanel.getComponents();
        for (int i = 0; i < components.length; i++) {
            Component comp = components[i];
            // Encontrar el panel de trabajo de grado por nombre
            String name = null;
            for (Component c : contentPanel.getComponents()) {
                if (c == comp) {
                    // Buscar en el CardLayout
                    break;
                }
            }
        }

        // Remover y recrear el panel de trabajo de grado
        contentPanel.remove(1);
        contentPanel.add(new EstudianteTrabajoGradoPanel(controller, this), TRABAJO_GRADO_VIEW);

        showView(TRABAJO_GRADO_VIEW);
        contentPanel.revalidate();
        contentPanel.repaint();

        System.out.println("DEBUG: Vista cambiada a trabajo de grado");
    }

    public EstudianteController getController() {
        return controller;
    }

    public User getCurrentUser() {
        return currentUser;
    }

//    // Main de prueba (solo para desarrollo)
//    public static void main(String args[]) {
//        try {
//            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        } catch (Exception ignored) { }
//
//        SwingUtilities.invokeLater(() -> {
//            // Crear usuario de prueba
//            User testUser = new User();
//            testUser.setId(1);
//            testUser.setNombres("Juan");
//            testUser.setApellidos("Pérez");
//
//            EstudianteView view = new EstudianteView();
//            EstudianteController controller = new EstudianteController(view, testUser);
//
//            view.setUser(testUser);
//            view.setController(controller);
//
//            view.setVisible(true);
//        });
//    }
}