package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.domain.service.IUserService;
import co.unicauca.gestiontrabajogrado.domain.service.ServiceLocator;
import co.unicauca.gestiontrabajogrado.dto.FormatoADetalleDTO;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoResponseDTO;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.estudianteview.EstudianteView;

import javax.swing.JOptionPane;

public class EstudianteController {
    private EstudianteView view;
    private User currentUser;
    private IProyectoGradoService proyectoGradoService;
    private IUserService userService;
    private ProyectoGradoResponseDTO proyectoActual;

    private IDashBoardController navigator;
    public void setNavigator(IDashBoardController navigator) { this.navigator = navigator; }

    public EstudianteController(EstudianteView view, User user, IProyectoGradoService proyectoGradoService, IUserService userService) {
        this.view = view;
        this.currentUser = user;
        try {
            this.proyectoGradoService = proyectoGradoService;
            this.userService = userService;

            System.out.println("DEBUG: Servicios obtenidos correctamente del ServiceLocator");
        } catch (Exception e) {
            System.err.println("DEBUG: Error obteniendo servicios del ServiceLocator: " + e.getMessage());
        }
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
        if (userService != null) {
            return userService.obtenerNombreCompleto(proyectoActual.directorId);
        }
        return "No asignado";
    }

    public String obtenerNombreCodirector() {
        if (userService != null) {
            return userService.obtenerNombreCompleto(proyectoActual.codirectorId);
        }
        return "No asignado";
    }
    /**
     * Maneja el cierre de sesión del estudiante.
     */
    public void handleCerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                view,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                if (view != null) view.dispose();
                if (navigator != null) {
                    navigator.openLogin();
                } else {
                    System.err.println("No se pudo volver al login. Reinicia la aplicación.");
                }
            } catch (Exception e) {
                System.err.println("Error al cerrar sesión: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    public String obtenerNombreEstudiante2() {
        if (proyectoActual == null) {
            return null;
        }

        // Determinar quién es el "otro" estudiante
        Integer otroEstudianteId = determinarOtroEstudiante();

        if (otroEstudianteId == null) {
            return null; // Es un proyecto individual
        }

        if (userService != null) {
            String nombreCompleto = userService.obtenerNombreCompleto(otroEstudianteId);
            return "Usuario no encontrado".equals(nombreCompleto) ? null : nombreCompleto;
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
    public FormatoADetalleDTO obtenerUltimoFormatoA() {
        if (proyectoActual == null) {
            return null;
        }
        return proyectoGradoService.obtenerUltimoFormatoA(proyectoActual.id);
    }
    private Integer determinarOtroEstudiante() {
        if (proyectoActual.estudiante1Id != null && proyectoActual.estudiante2Id != null) {
            // Hay dos estudiantes, determinar cuál no es el usuario actual
            if (proyectoActual.estudiante1Id.equals(currentUser.getId())) {
                return proyectoActual.estudiante2Id;
            } else if (proyectoActual.estudiante2Id.equals(currentUser.getId())) {
                return proyectoActual.estudiante1Id;
            }
        }
        return null; // Proyecto individual o casos edge
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