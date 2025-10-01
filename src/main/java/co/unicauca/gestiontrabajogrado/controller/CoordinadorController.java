package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.model.FormatoA;
import co.unicauca.gestiontrabajogrado.domain.model.ProyectoGrado;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;
import co.unicauca.gestiontrabajogrado.domain.service.IProyectoGradoService;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IFormatoARepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IProyectoGradoRepository;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview.CoordinadorView;
import co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview.PropuestaRow;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Controlador para la vista del Coordinador.
 * Maneja la lógica de presentación y coordina entre el modelo y la vista.
 */
public class CoordinadorController {

    // Dependencias del dominio
    private final IProyectoGradoRepository proyectoRepo;
    private final IFormatoARepository formatoRepo;
    private final IProyectoGradoService proyectoService;

    private IDashBoardController navigator;
    public void setNavigator(IDashBoardController navigator) { this.navigator = navigator; }

    // Vista asociada
    private CoordinadorView view;

    public CoordinadorController(
            IProyectoGradoRepository proyectoRepo,
            IFormatoARepository formatoRepo,
            IProyectoGradoService proyectoService) {
        this.proyectoRepo = proyectoRepo;
        this.formatoRepo = formatoRepo;
        this.proyectoService = proyectoService;
    }

    /**
     * Asocia la vista con este controlador.
     */
    public void setView(CoordinadorView view) {
        this.view = view;
    }

    // ============================================================
    // OPERACIONES DE CONSULTA
    // ============================================================

    /**
     * Obtiene la lista de propuestas para mostrar en la tabla.
     * @param soloPendientes si es true, filtra solo las pendientes
     * @return lista de DTOs para la vista
     */
    public List<PropuestaRow> obtenerPropuestas(boolean soloPendientes) {
        List<PropuestaRow> resultado = new ArrayList<>();

        for (ProyectoGrado proyecto : proyectoRepo.findAll()) {
            Optional<FormatoA> ultimoFormato = formatoRepo.findLastFormatoByProyectoId(proyecto.getId());

            enumEstadoFormato estadoFormato = ultimoFormato
                    .map(FormatoA::getEstado)
                    .orElse(enumEstadoFormato.PENDIENTE);

            // Filtrar si solo queremos pendientes
            if (soloPendientes && estadoFormato != enumEstadoFormato.PENDIENTE) {
                continue;
            }

            PropuestaRow row = new PropuestaRow(
                    proyecto.getId(),
                    ultimoFormato.map(FormatoA::getId).orElse(null),
                    proyecto.getTitulo(),
                    proyecto.getEstado(),
                    estadoFormato,
                    ultimoFormato.map(FormatoA::getNumeroIntento).orElse(0)
            );

            resultado.add(row);
        }

        // Ordenar por intento (más recientes primero) y luego por título
        resultado.sort(
                Comparator.<PropuestaRow>comparingInt(r -> r.intento() == null ? 0 : r.intento())
                        .reversed()
                        .thenComparing(PropuestaRow::titulo)
        );

        return resultado;
    }

    /**
     * Cuenta cuántas propuestas están en un estado específico.
     */
    public long contarPorEstado(enumEstadoFormato estado) {
        return obtenerPropuestas(false).stream()
                .filter(p -> p.estadoFormato() == estado)
                .count();
    }
    /**
     * Maneja el cierre de sesión del coordinador.
     */
    public void handleCerrarSesion() {
        if (view != null) {
            int opcion = javax.swing.JOptionPane.showConfirmDialog(
                    view,
                    "¿Estás seguro de que deseas cerrar sesión?",
                    "Confirmar cierre de sesión",
                    javax.swing.JOptionPane.YES_NO_OPTION);

            if (opcion == javax.swing.JOptionPane.YES_OPTION) {
                try {
                    view.dispose();
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
    }
    /**
     * Obtiene el detalle completo de una propuesta.
     */
    public DetallePropuestaDTO obtenerDetallePropuesta(Integer proyectoId) {
        ProyectoGrado proyecto = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "Proyecto no encontrado: " + proyectoId));

        Optional<FormatoA> ultimoFormato = formatoRepo.findLastFormatoByProyectoId(proyectoId);

        // Construir DTO con toda la información
        DetallePropuestaDTO dto = new DetallePropuestaDTO();
        dto.setTitulo(proyecto.getTitulo());
        dto.setEstadoProyecto(proyecto.getEstado().name());
        dto.setDirectorId(proyecto.getDirectorId());
        dto.setEstudiante1Id(proyecto.getEstudiante1Id());
        dto.setEstudiante2Id(proyecto.getEstudiante2Id());

        if (ultimoFormato.isPresent()) {
            FormatoA formato = ultimoFormato.get();
            dto.setEstadoFormato(formato.getEstado().name());
            dto.setNumeroIntento(formato.getNumeroIntento());
            dto.setObservaciones(formato.getObservaciones());
        } else {
            dto.setEstadoFormato(enumEstadoFormato.PENDIENTE.name());
            dto.setNumeroIntento(1);
            dto.setObservaciones("(sin observaciones)");
        }

        return dto;
    }

    // ============================================================
    // OPERACIONES DE MODIFICACIÓN
    // ============================================================

    /**
     * Aprueba un Formato A.
     * @param formatoId ID del formato a aprobar
     * @param observaciones comentarios del coordinador
     * @return el nuevo estado del formato
     */
    public enumEstadoFormato aprobarFormato(Integer formatoId, String observaciones) {
        validarObservaciones(observaciones);

        // TODO: Obtener el ID del coordinador del contexto de sesión
        Integer coordinadorId = obtenerCoordinadorLogueado();

        proyectoService.aprobarFormatoA(formatoId, observaciones, coordinadorId);

        // Notificar a la vista si está presente
        if (view != null) {
            view.notificarCambioEstado(formatoId, enumEstadoFormato.APROBADO);
        }

        return enumEstadoFormato.APROBADO;
    }

    /**
     * Rechaza un Formato A.
     * @param formatoId ID del formato a rechazar
     * @param observaciones comentarios del coordinador
     * @return el nuevo estado del formato
     */
    public enumEstadoFormato rechazarFormato(Integer formatoId, String observaciones) {
        validarObservaciones(observaciones);

        Integer coordinadorId = obtenerCoordinadorLogueado();

        proyectoService.rechazarFormatoA(formatoId, observaciones, coordinadorId);

        // Notificar a la vista si está presente
        if (view != null) {
            view.notificarCambioEstado(formatoId, enumEstadoFormato.RECHAZADO);
        }

        return enumEstadoFormato.RECHAZADO;
    }

    // ============================================================
    // MÉTODOS AUXILIARES Y VALIDACIONES
    // ============================================================

    private void validarObservaciones(String observaciones) {
        if (observaciones == null || observaciones.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Las observaciones son obligatorias para evaluar un formato");
        }
    }

    private Integer obtenerCoordinadorLogueado() {
        // TODO: Implementar cuando tengas gestión de sesión
        // Por ahora retorna un valor temporal
        return 0;
    }

    // ============================================================
    // DTO PARA DETALLES
    // ============================================================

    /**
     * DTO para transferir información detallada de una propuesta a la vista.
     */
    public static class DetallePropuestaDTO {
        private String titulo;
        private String estadoProyecto;
        private String estadoFormato;
        private Integer numeroIntento;
        private String observaciones;
        private Integer directorId;
        private Integer estudiante1Id;
        private Integer estudiante2Id;

        // Getters y setters
        public String getTitulo() { return titulo; }
        public void setTitulo(String titulo) { this.titulo = titulo; }

        public String getEstadoProyecto() { return estadoProyecto; }
        public void setEstadoProyecto(String estadoProyecto) {
            this.estadoProyecto = estadoProyecto;
        }

        public String getEstadoFormato() { return estadoFormato; }
        public void setEstadoFormato(String estadoFormato) {
            this.estadoFormato = estadoFormato;
        }

        public Integer getNumeroIntento() { return numeroIntento; }
        public void setNumeroIntento(Integer numeroIntento) {
            this.numeroIntento = numeroIntento;
        }

        public String getObservaciones() { return observaciones; }
        public void setObservaciones(String observaciones) {
            this.observaciones = observaciones;
        }

        public Integer getDirectorId() { return directorId; }
        public void setDirectorId(Integer directorId) {
            this.directorId = directorId;
        }

        public Integer getEstudiante1Id() { return estudiante1Id; }
        public void setEstudiante1Id(Integer estudiante1Id) {
            this.estudiante1Id = estudiante1Id;
        }

        public Integer getEstudiante2Id() { return estudiante2Id; }
        public void setEstudiante2Id(Integer estudiante2Id) {
            this.estudiante2Id = estudiante2Id;
        }

        /**
         * Formatea la información para mostrar en la vista.
         */
        public String formatearParaVista() {
            return String.format("""
                    Propuesta
                    ---------
                    %s
                    
                    Estado del proyecto: %s
                    Último Formato A: intento #%d, estado %s
                    
                    Observaciones del Formato A:
                    %s
                    
                    Director ID: %s
                    Estudiante(s): %s %s
                    """,
                    titulo,
                    estadoProyecto,
                    numeroIntento,
                    estadoFormato,
                    observaciones,
                    directorId != null ? directorId : "-",
                    estudiante1Id != null ? estudiante1Id : "-",
                    estudiante2Id != null ? (", " + estudiante2Id) : ""
            );
        }
    }
}