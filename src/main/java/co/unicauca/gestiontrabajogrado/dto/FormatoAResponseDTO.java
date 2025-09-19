package co.unicauca.gestiontrabajogrado.dto;

import java.time.LocalDateTime;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;

public class FormatoAResponseDTO {
    public Integer id;
    public Integer proyectoGradoId;
    public Integer numeroIntento;
    public String rutaArchivo;
    public String nombreArchivo;
    public LocalDateTime fechaCarga;
    public enumEstadoFormato estado;
    public String observaciones;
}
