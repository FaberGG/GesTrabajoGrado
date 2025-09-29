package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.domain.service.ProyectoGradoService;
import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.dto.*;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.docenteview.DocenteView;

import javax.swing.*;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

public class DocenteController {

    private final IProyectoGradoService proyectoGradoService;
    private DocenteView docenteView;
    private User currentUser;

    private IDashBoardController navigator;
    public void setNavigator(IDashBoardController navigator) { this.navigator = navigator; }

    public DocenteController(IProyectoGradoService proyectoGradoService, User currentUser) {
        this.proyectoGradoService = proyectoGradoService;
        this.currentUser = currentUser;
    }

    public void setDocenteView(DocenteView docenteView) {
        this.docenteView = docenteView;
    }

    public User getCurrentUser() { return currentUser; }

    // ---------------- Creación de proyecto ----------------
    public void handleCrearProyecto(ProyectoGradoRequestDTO request, File archivoFormatoA, File cartaAceptacion) {
        try {
            if (request == null) { showError("Solicitud inválida."); return; }
            if (request.titulo == null || request.titulo.isBlank()) { showError("Por favor, ingresa el título del proyecto."); return; }
            if (request.modalidad == null) { showError("Por favor, selecciona una modalidad."); return; }
            if (archivoFormatoA == null) { showError("Debes adjuntar el Formato A (PDF)."); return; }

            if (request.modalidad == enumModalidad.PRACTICA_PROFESIONAL) {
                if (cartaAceptacion == null) {
                    showError("Para Práctica profesional debes adjuntar la Carta de Aceptación.");
                    return;
                }
                if (!proyectoGradoService.validarModalidadPracticaProfesional(cartaAceptacion)) {
                    showError("La carta de aceptación no es válida (debe ser PDF).");
                    return;
                }
            }

            ProyectoGrado creado;
            if (cartaAceptacion != null && proyectoGradoService instanceof ProyectoGradoService svc) {
                creado = svc.crearNuevoProyecto(request, archivoFormatoA, cartaAceptacion);
            } else {
                creado = proyectoGradoService.crearNuevoProyecto(request, archivoFormatoA);
            }

            showSuccess("Proyecto creado exitosamente" + (cartaAceptacion != null ? " con carta de aceptación." : "."));
            actualizarListaProyectos();

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Ha ocurrido un error inesperado al crear el proyecto.");
            e.printStackTrace();
        }
    }

    // ---------------- Nueva versión del Formato A ----------------
    public void handleSubirNuevaVersion(Integer proyectoId, File archivoFormatoA, File cartaAceptacion) {
        try {
            if (proyectoId == null) { showError("ID de proyecto inválido."); return; }
            if (archivoFormatoA == null) { showError("Debes adjuntar el Formato A (PDF)."); return; }

            FormatoA nuevo;
            if (cartaAceptacion != null && proyectoGradoService instanceof ProyectoGradoService svc) {
                nuevo = svc.subirNuevaVersion(proyectoId, archivoFormatoA, cartaAceptacion);
            } else {
                nuevo = proyectoGradoService.subirNuevaVersion(proyectoId, archivoFormatoA);
            }

            showSuccess("Nueva versión subida correctamente.");
            actualizarListaProyectos();

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Ha ocurrido un error inesperado al subir la nueva versión.");
            e.printStackTrace();
        }
    }

    // ---------------- Aprobación / Rechazo ----------------
    public void handleAprobarFormatoA(Integer formatoId, String observaciones) {
        try {
            if (formatoId == null) { showError("ID de formato inválido."); return; }
            proyectoGradoService.aprobarFormatoA(formatoId, observaciones, currentUser.getId());
            showSuccess("Formato A aprobado.");
            actualizarListaProyectos();
        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Error al aprobar el formato.");
            e.printStackTrace();
        }
    }

    public void handleRechazarFormatoA(Integer formatoId, String observaciones) {
        try {
            if (formatoId == null) { showError("ID de formato inválido."); return; }
            if (observaciones == null || observaciones.isBlank()) { showError("Debes ingresar observaciones para el rechazo."); return; }

            proyectoGradoService.rechazarFormatoA(formatoId, observaciones, currentUser.getId());
            showSuccess("Formato A rechazado.");
            actualizarListaProyectos();
        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
        } catch (Exception e) {
            showError("Error al rechazar el formato.");
            e.printStackTrace();
        }
    }

    // ---------------- Carga de datos para la vista ----------------
    public List<ProyectoGradoResponseDTO> obtenerMisProyectos() {
        try {
            return proyectoGradoService.obtenerProyectosPorDocente(currentUser.getId());
        } catch (Exception e) {
            showError("Error al obtener los proyectos.");
            e.printStackTrace();
            return List.of();
        }
    }

    public void actualizarListaProyectos(List<ProyectoGradoResponseDTO> proyectos) {
        if (docenteView == null || proyectos == null) return;

        List<DocenteView.PropuestaItem> items = proyectos.stream()
                .map(p -> new DocenteView.PropuestaItem(
                        p.titulo,
                        p.fechaCreacion != null ? p.fechaCreacion.toString() : "Sin fecha"))
                .collect(Collectors.toList());

        docenteView.setPropuestas(items);
    }

    public void actualizarListaProyectos() {
        if (docenteView == null) return;
        SwingUtilities.invokeLater(() -> {
            try {
                List<ProyectoGradoResponseDTO> proyectos = obtenerMisProyectos();
                actualizarListaProyectos(proyectos);
            } catch (Exception e) {
                showError("Error al actualizar la lista de proyectos.");
                e.printStackTrace();
            }
        });
    }

    // ---------------- Sesión / Navegación ----------------
    public void handleCerrarSesion() {
        int opcion = JOptionPane.showConfirmDialog(
                docenteView,
                "¿Estás seguro de que deseas cerrar sesión?",
                "Confirmar cierre de sesión",
                JOptionPane.YES_NO_OPTION);

        if (opcion == JOptionPane.YES_OPTION) {
            try {
                if (docenteView != null) docenteView.dispose();
                if (navigator != null) navigator.openLogin();
                else showError("No se pudo volver al login. Reinicia la aplicación.");
            } catch (Exception e) {
                showError("Error al cerrar sesión.");
                e.printStackTrace();
            }
        }
    }

    public void inicializarVista() {
        if (docenteView == null) return;
        SwingUtilities.invokeLater(() -> {
            try {
                docenteView.setUser(currentUser);
                actualizarListaProyectos();
            } catch (Exception e) {
                showError("Error al inicializar la vista del docente.");
                e.printStackTrace();
            }
        });
    }

    public IProyectoGradoService getProyectoGradoService() { return proyectoGradoService; }

    // ---------------- Helpers UI ----------------
    private void showError(String message) {
        JOptionPane.showMessageDialog(docenteView, message, "Error", JOptionPane.ERROR_MESSAGE);
    }
    private void showSuccess(String message) {
        JOptionPane.showMessageDialog(docenteView, message, "Éxito", JOptionPane.INFORMATION_MESSAGE);
    }
}
