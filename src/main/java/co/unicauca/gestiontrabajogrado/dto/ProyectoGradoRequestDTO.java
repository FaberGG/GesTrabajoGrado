package co.unicauca.gestiontrabajogrado.dto;

import co.unicauca.gestiontrabajogrado.domain.model.enumModalidad;

public class ProyectoGradoRequestDTO {
    public String titulo;
    public enumModalidad modalidad;
    public Integer directorId;
    public Integer codirectorId; // opcional
    public String objetivoGeneral;
    public String objetivosEspecificos;
    public Integer estudianteId; // opcional
}