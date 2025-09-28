package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.domain.service.ServiceLocator;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoResponseDTO;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;

import javax.swing.JOptionPane;

public class EstudianteController {
    private EstudianteView view;
    private User currentUser;
    private IProyectoGradoService proyectoGradoService;
    private ProyectoGradoResponseDTO proyectoActual;

    public EstudianteController(EstudianteView view, User user) {
        this.view = view;
        this.currentUser = user;
        //Inyectar el servicio a través de dependency injection
        this.proyectoGradoService = ServiceLocator.getInstance().getProyectoGradoService();
    }

    public void cargarDatosTrabajoGrado() {
        System.out.println("DEBUG: Cargando datos para usuario ID: " + currentUser.getId());

        try {
            proyectoActual = proyectoGradoService.obtenerProyectoPorEstudiante(currentUser.getId());

            if (proyectoActual == null) {
                mostrarMensajeNoProyecto();
            }
        } catch (Exception e) {
            System.out.println("DEBUG: Error al obtener proyecto: " + e.getMessage());
            e.printStackTrace();
            mostrarMensajeNoProyecto();
        }
    }

    private void mostrarMensajeNoProyecto() {
        JOptionPane.showMessageDialog(view,
                "No tienes un trabajo de grado asignado actualmente.",
                "Sin proyecto", JOptionPane.INFORMATION_MESSAGE);
    }

    public void volverAlDashboard() {
        view.showView(EstudianteView.DASHBOARD_VIEW);
    }

    // Getters
    public ProyectoGradoResponseDTO getProyectoActual() {
        return proyectoActual;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean tieneProyecto() {
        boolean tiene = proyectoActual != null;
        System.out.println("DEBUG: tieneProyecto() = " + tiene);
        if (tiene) {
            System.out.println("DEBUG: Proyecto título: " + proyectoActual.titulo);
        }
        return tiene;
    }

    // Métodos para obtener información del director/codirector
    public String obtenerNombreDirector() {
        // TODO: Implementar servicio para obtener datos del director
        return "Dr. Juan Pérez";
    }

    public String obtenerNombreCodirector() {
        if (proyectoActual != null && proyectoActual.codirectorId != null) {
            // TODO: Implementar servicio para obtener datos del codirector
            return "Dra. María García"; // Simulado
        }
        return "No asignado";
    }

    public String obtenerNombreEstudiante2() {
        if (proyectoActual != null && proyectoActual.estudiante2Id != null) {
            // TODO: Implementar servicio para obtener datos del estudiante 2
            return "Ana López";
        }
        return null;
    }

    // Métodos para el seguimiento del proyecto
    public String obtenerEstadoActualTexto() {
        if (proyectoActual == null) return "Sin proyecto";

        switch (proyectoActual.estado) {
            case EN_PROCESO:
                return getEstadoDetallado();
            case RECHAZADO:
                return "Esperando correcciones del formato A";
            case APROBADO:
                return "Propuesta Aceptada";
            case RECHAZADO_DEFINITIVO:
                return "Propuesta rechazada definitivamente";
            default:
                return "Estado desconocido";
        }
    }

    private String getEstadoDetallado() {
        if (proyectoActual.numeroIntentos == 1) {
            return "Primera evaluación del formato A";
        } else if (proyectoActual.numeroIntentos == 2) {
            return "Segunda evaluación del formato A";
        } else if (proyectoActual.numeroIntentos == 3) {
            return "Tercera evaluación del formato A";
        }
        return "En proceso";
    }
}