package co.unicauca.gestiontrabajogrado.domain.model;

import java.time.LocalDateTime;

public class ProyectoGrado {
    private Integer id;
    private String titulo;
    private enumModalidad modalidad;
    private LocalDateTime fechaCreacion;
    private Integer directorId;
    private Integer codirectorId; // opcional
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private Integer estudianteId; // opcional
    private enumEstadoProyecto estado = enumEstadoProyecto.EN_PROCESO;
    private Integer numeroIntentos = 1;

    public boolean puedeSubirNuevaVersion() {
        return estado == enumEstadoProyecto.RECHAZADO && numeroIntentos < 3;
    }
    public void marcarComoRechazadoDefinitivo() { this.estado = enumEstadoProyecto.RECHAZADO_DEFINITIVO; }
    public void incrementarIntentos() { this.numeroIntentos = this.numeroIntentos + 1; }

    // Getters & Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public enumModalidad getModalidad() { return modalidad; }
    public void setModalidad(enumModalidad modalidad) { this.modalidad = modalidad; }
    public LocalDateTime getFechaCreacion() { return fechaCreacion; }
    public void setFechaCreacion(LocalDateTime fechaCreacion) { this.fechaCreacion = fechaCreacion; }
    public Integer getDirectorId() { return directorId; }
    public void setDirectorId(Integer directorId) { this.directorId = directorId; }
    public Integer getCodirectorId() { return codirectorId; }
    public void setCodirectorId(Integer codirectorId) { this.codirectorId = codirectorId; }
    public String getObjetivoGeneral() { return objetivoGeneral; }
    public void setObjetivoGeneral(String objetivoGeneral) { this.objetivoGeneral = objetivoGeneral; }
    public String getObjetivosEspecificos() { return objetivosEspecificos; }
    public void setObjetivosEspecificos(String objetivosEspecificos) { this.objetivosEspecificos = objetivosEspecificos; }
    public Integer getEstudianteId() { return estudianteId; }
    public void setEstudianteId(Integer estudianteId) { this.estudianteId = estudianteId; }
    public enumEstadoProyecto getEstado() { return estado; }
    public void setEstado(enumEstadoProyecto estado) { this.estado = estado; }
    public Integer getNumeroIntentos() { return numeroIntentos; }
    public void setNumeroIntentos(Integer numeroIntentos) { this.numeroIntentos = numeroIntentos; }
}