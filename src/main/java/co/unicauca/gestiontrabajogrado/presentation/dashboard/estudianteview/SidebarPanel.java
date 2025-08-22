package co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview;

import co.unicauca.gestiontrabajogrado.presentation.common.BaseSidebarPanel;

import javax.swing.*;

/**
 * Panel lateral específico para el rol de Estudiante
 */
public class SidebarPanel extends BaseSidebarPanel {

    public SidebarPanel(JFrame parentFrame) {
        super(parentFrame);
    }

    @Override
    protected String getRoleHeaderText() {
        return "Estudiante";
    }

    @Override
    protected String[] getSubmenuItems() {
        return new String[]{
                "Ver estado del Trabajo de Grado",
                "Crear nuevo Trabajo de Grado"
        };
    }

    @Override
    protected void createRoleSpecificComponents() {
        // Aquí se pueden agregar componentes específicos del estudiante si es necesario
        // Por ahora no hay componentes adicionales
    }

    @Override
    protected void setupSubmenuToggle() {
        // La lógica de toggle está en la clase base, aquí se puede personalizar si es necesario
        // Por ahora usamos el comportamiento por defecto
    }

    @Override
    protected void handleSubmenuAction(String actionText) {
        // Manejar acciones específicas del estudiante
        switch (actionText) {
            case "Ver estado del Trabajo de Grado":
                handleVerEstadoTrabajo();
                break;
            case "Crear nuevo Trabajo de Grado":
                handleCrearNuevoTrabajo();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Acción: " + actionText, "Info",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleVerEstadoTrabajo() {
        // TODO: Implementar navegación a la vista de estado del trabajo de grado
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de ver estado del trabajo de grado en desarrollo.",
                "Estado del Trabajo",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleCrearNuevoTrabajo() {
        // TODO: Implementar navegación a la vista de creación de nuevo trabajo
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de crear nuevo trabajo de grado en desarrollo.",
                "Nuevo Trabajo",
                JOptionPane.INFORMATION_MESSAGE);
    }
}