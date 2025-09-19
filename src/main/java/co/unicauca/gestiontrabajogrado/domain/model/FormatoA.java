package co.unicauca.gestiontrabajogrado.domain.model;

import java.time.LocalDateTime;

public class FormatoA {
    private Integer id;
    private Integer proyectoGradoId;
    private Integer numeroIntento;
    private String rutaArchivo;
    private String nombreArchivo;
    private LocalDateTime fechaCarga;
    private enumEstadoFormato estado = enumEstadoFormato.PENDIENTE;
    private String observaciones;
    private Integer evaluadoPor;
    private LocalDateTime fechaEvaluacion;

    public void aprobar(Integer evaluadorId, String observaciones) {
        this.estado = enumEstadoFormato.APROBADO;
        this.evaluadoPor = evaluadorId;
        this.observaciones = observaciones;
        this.fechaEvaluacion = LocalDateTime.now();
    }
    public void rechazar(Integer evaluadorId, String observaciones) {
        this.estado = enumEstadoFormato.RECHAZADO;
        this.evaluadoPor = evaluadorId;
        this.observaciones = observaciones;
        this.fechaEvaluacion = LocalDateTime.now();
    }
    public boolean esUltimoIntento() { return this.numeroIntento != null && this.numeroIntento >= 3; }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public Integer getProyectoGradoId() { return proyectoGradoId; }
    public void setProyectoGradoId(Integer proyectoGradoId) { this.proyectoGradoId = proyectoGradoId; }
    public Integer getNumeroIntento() { return numeroIntento; }
    public void setNumeroIntento(Integer numeroIntento) { this.numeroIntento = numeroIntento; }
    public String getRutaArchivo() { return rutaArchivo; }
    public void setRutaArchivo(String rutaArchivo) { this.rutaArchivo = rutaArchivo; }
    public String getNombreArchivo() { return nombreArchivo; }
    public void setNombreArchivo(String nombreArchivo) { this.nombreArchivo = nombreArchivo; }
    public LocalDateTime getFechaCarga() { return fechaCarga; }
    public void setFechaCarga(LocalDateTime fechaCarga) { this.fechaCarga = fechaCarga; }
    public enumEstadoFormato getEstado() { return estado; }
    public void setEstado(enumEstadoFormato estado) { this.estado = estado; }
    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
    public Integer getEvaluadoPor() { return evaluadoPor; }
    public void setEvaluadoPor(Integer evaluadoPor) { this.evaluadoPor = evaluadoPor; }
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }
}
