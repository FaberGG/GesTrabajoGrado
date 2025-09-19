package co.unicauca.gestiontrabajogrado.dto;

import java.time.LocalDateTime;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoProyecto;
import co.unicauca.gestiontrabajogrado.domain.model.enumModalidad;

public class ProyectoGradoResponseDTO {
    public Integer id;
    public String titulo;
    public enumModalidad modalidad;
    public LocalDateTime fechaCreacion;
    public Integer directorId;
    public Integer codirectorId;
    public String objetivoGeneral;
    public String objetivosEspecificos;
    public Integer estudianteId;
    public enumEstadoProyecto estado;
    public Integer numeroIntentos;
}
