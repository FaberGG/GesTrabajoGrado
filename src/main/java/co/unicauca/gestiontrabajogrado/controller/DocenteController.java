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
    public boolean handleCrearProyecto(ProyectoGradoRequestDTO request, File archivoFormatoA, File cartaAceptacion) {
        try {
            if (request == null) { showError("Solicitud inválida."); return false; }
            if (request.titulo == null || request.titulo.isBlank()) {
                showError("Por favor, ingresa el título del proyecto.");
                return false;
            }
            if (request.modalidad == null) {
                showError("Por favor, selecciona una modalidad.");
                return false;
            }
            if (archivoFormatoA == null) {
                showError("Debes adjuntar el Formato A (PDF).");
                return false;
            }

            if (request.modalidad == enumModalidad.PRACTICA_PROFESIONAL) {
                if (cartaAceptacion == null) {
                    showError("Para Práctica profesional debes adjuntar la Carta de Aceptación.");
                    return false;
                }
                if (!proyectoGradoService.validarModalidadPracticaProfesional(cartaAceptacion)) {
                    showError("La carta de aceptación no es válida (debe ser PDF).");
                    return false;
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
            return true;

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
            return false;
        } catch (Exception e) {
            showError("Ha ocurrido un error inesperado al crear el proyecto: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ---------------- Nueva versión del Formato A ----------------
// ---------------- Nueva versión del Formato A ----------------
    public boolean handleSubirNuevaVersion(Integer proyectoId, File archivoFormatoA, File cartaAceptacion,
                                           String objetivoGeneral, String objetivosEspecificos) {
        try {
            if (proyectoId == null) {
                showError("ID de proyecto inválido.");
                return false;
            }
            if (archivoFormatoA == null) {
                showError("Debes adjuntar el Formato A (PDF).");
                return false;
            }

            // Validar objetivos
            if (objetivoGeneral == null || objetivoGeneral.trim().isEmpty()) {
                showError("El Objetivo General no puede estar vacío.");
                return false;
            }
            if (objetivosEspecificos == null || objetivosEspecificos.trim().isEmpty()) {
                showError("Los Objetivos Específicos no pueden estar vacíos.");
                return false;
            }

            // Verificar que el servicio es del tipo correcto
            if (!(proyectoGradoService instanceof ProyectoGradoService)) {
                showError("El servicio no soporta esta operación.");
                return false;
            }

            ProyectoGradoService svc = (ProyectoGradoService) proyectoGradoService;
            FormatoA nuevo = svc.subirNuevaVersion(proyectoId, archivoFormatoA, cartaAceptacion,
                    objetivoGeneral, objetivosEspecificos);

            showSuccess("Nueva versión subida correctamente con objetivos actualizados.");
            actualizarListaProyectos();
            return true;

        } catch (IllegalArgumentException e) {
            showError("Error de validación: " + e.getMessage());
            return false;
        } catch (Exception e) {
            showError("Ha ocurrido un error inesperado: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    // ---------------- Consultas de proyectos ----------------
    public List<ProyectoGradoResponseDTO> obtenerMisProyectos() {
        try {
            return proyectoGradoService.obtenerProyectosPorDocente(currentUser.getId());
        } catch (Exception e) {
            showError("Error al obtener los proyectos.");
            e.printStackTrace();
            return List.of();
        }
    }

    public ProyectoGradoResponseDTO obtenerProyectoPorId(Integer proyectoId) {
        try {
            return proyectoGradoService.obtenerProyectoPorId(proyectoId);
        } catch (Exception e) {
            showError("Error al obtener el proyecto.");
            e.printStackTrace();
            return null;
        }
    }

    public FormatoADetalleDTO obtenerUltimoFormatoA(Integer proyectoId) {
        try {
            return proyectoGradoService.obtenerUltimoFormatoA(proyectoId);
        } catch (Exception e) {
            showError("Error al obtener los detalles del formato.");
            e.printStackTrace();
            return null;
        }
    }

    // ---------------- Actualización de vista ----------------
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

    public void actualizarListaProyectos(List<ProyectoGradoResponseDTO> proyectos) {
        if (docenteView == null || proyectos == null) return;

        List<DocenteView.PropuestaItem> items = proyectos.stream()
                .map(p -> new DocenteView.PropuestaItem(
                        p.titulo,
                        p.fechaCreacion != null ? p.fechaCreacion.toString() : "Sin fecha",
                        p.id,
                        p.estado != null ? p.estado.toString() : "DESCONOCIDO",
                        p.numeroIntentos != null ? p.numeroIntentos : 0
                ))
                .collect(Collectors.toList());

        docenteView.setPropuestas(items);
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