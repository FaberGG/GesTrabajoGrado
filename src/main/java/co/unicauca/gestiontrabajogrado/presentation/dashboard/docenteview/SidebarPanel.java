package co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview;

import co.unicauca.gestiontrabajogrado.presentation.common.BaseSidebarPanel;

import javax.swing.*;

/**
 * Panel lateral específico para el rol de Docente
 */
class SidebarPanel extends BaseSidebarPanel {

    SidebarPanel(JFrame parentFrame) {
        super(parentFrame);
    }

    @Override
    protected String getRoleHeaderText() {
        return "Docente";
    }

    @Override
    protected String[] getSubmenuItems() {
        return new String[]{
                "Evaluar monografía",
                "Evaluar anteproyecto"
        };
    }

    @Override
    protected void createRoleSpecificComponents() {
        // Aquí se pueden agregar componentes específicos del docente si es necesario
        // Por ahora no hay componentes adicionales
    }

    @Override
    protected void setupSubmenuToggle() {
        // La lógica de toggle está en la clase base, aquí se puede personalizar si es necesario
        // Por ahora usamos el comportamiento por defecto
    }

    @Override
    protected void handleSubmenuAction(String actionText) {
        // Manejar acciones específicas del docente
        switch (actionText) {
            case "Evaluar monografía":
                handleEvaluarMonografia();
                break;
            case "Evaluar anteproyecto":
                handleEvaluarAnteproyecto();
                break;
            default:
                JOptionPane.showMessageDialog(this, "Acción: " + actionText, "Info",
                        JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void handleEvaluarMonografia() {
        // TODO: Implementar navegación a la vista de evaluación de monografía
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de evaluación de monografía en desarrollo.",
                "Evaluar Monografía",
                JOptionPane.INFORMATION_MESSAGE);
    }

    private void handleEvaluarAnteproyecto() {
        // TODO: Implementar navegación a la vista de evaluación de anteproyecto
        JOptionPane.showMessageDialog(this,
                "Funcionalidad de evaluación de anteproyecto en desarrollo.",
                "Evaluar Anteproyecto",
                JOptionPane.INFORMATION_MESSAGE);
    }
}