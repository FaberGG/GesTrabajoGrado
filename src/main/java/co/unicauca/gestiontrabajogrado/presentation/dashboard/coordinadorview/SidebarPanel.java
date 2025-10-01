package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import co.unicauca.gestiontrabajogrado.presentation.common.BaseSidebarPanel;

import javax.swing.*;

class SidebarPanel extends BaseSidebarPanel {
    SidebarPanel(JFrame parentFrame) { super(parentFrame); }

    @Override protected String getRoleHeaderText() { return "Coordinador"; }

    @Override
    protected String[] getSubmenuItems() {
        return new String[]{
                "Evaluar Formato A",
                "Asignar Evaluadores",
                "Revisar Solicitud de Sustentación",
                "Asignar Jurados",
                "Asignar Sustentación",
                "Consolidar Calificaciones de Jurados",
                "Validad requisitos de Práctica profesional"
        };
    }

    @Override protected void createRoleSpecificComponents() {}
    @Override protected void setupSubmenuToggle() {}

    @Override
    protected void handleSubmenuAction(String actionText) {
        JOptionPane.showMessageDialog(this, "Acción: " + actionText);
    }
}
