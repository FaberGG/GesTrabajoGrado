package co.unicauca.domain.entities;

import java.time.LocalDateTime;

public class ProyectoGrado {
    private Integer id;
    private String titulo;
    private enumModalidad modalidad;
    private LocalDateTime fechaCreacion;
    private Integer directorId;
    private Integer codirectorId;
    private String objetivoGeneral;
    private String objetivosEspecificos;
    private Integer estudiante1Id;
    private Integer estudiante2Id;
    private enumEstadoProyecto estado = enumEstadoProyecto.EN_PROCESO;
    private Integer numeroIntentos = 1;
    
    // Atributos para evaluación
    private Integer puntuacionTotal;
    private String evaluador;
    private LocalDateTime fechaEvaluacion;
    private CriteriosEvaluacion criteriosEvaluacion; // 🆕 NUEVO
    
    public boolean puedeSubirNuevaVersion() {
        return estado == enumEstadoProyecto.RECHAZADO && numeroIntentos < 3;
    }
    
    public void marcarComoRechazadoDefinitivo() { 
        this.estado = enumEstadoProyecto.RECHAZADO_DEFINITIVO; 
    }
    
    public void incrementarIntentos() { 
        this.numeroIntentos++; 
    }
    
    // Getters y Setters existentes...
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
    public Integer getEstudiante1Id() { return estudiante1Id; }
    public void setEstudiante1Id(Integer estudiante1Id) { this.estudiante1Id = estudiante1Id; }
    public Integer getEstudiante2Id() { return estudiante2Id; }
    public void setEstudiante2Id(Integer estudiante2Id) { this.estudiante2Id = estudiante2Id; }
    public enumEstadoProyecto getEstado() { return estado; }
    public void setEstado(enumEstadoProyecto estado) { this.estado = estado; }
    public Integer getNumeroIntentos() { return numeroIntentos; }
    public void setNumeroIntentos(Integer numeroIntentos) { this.numeroIntentos = numeroIntentos; }
    
    public Integer getPuntuacionTotal() { return puntuacionTotal; }
    public void setPuntuacionTotal(Integer puntuacion) { this.puntuacionTotal = puntuacion; }
    
    public String getEvaluador() { return evaluador; }
    public void setEvaluador(String evaluador) { this.evaluador = evaluador; }
    
    public LocalDateTime getFechaEvaluacion() { return fechaEvaluacion; }
    public void setFechaEvaluacion(LocalDateTime fechaEvaluacion) { this.fechaEvaluacion = fechaEvaluacion; }
    
    // 🆕 NUEVO: Getter y Setter para criterios
    public CriteriosEvaluacion getCriteriosEvaluacion() { 
        return criteriosEvaluacion; 
    }
    public void setCriteriosEvaluacion(CriteriosEvaluacion criterios) { 
        this.criteriosEvaluacion = criterios; 
    }
}