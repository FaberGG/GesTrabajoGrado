package co.unicauca.gestiontrabajogrado.dto;

import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;

public class FormatoADetalleDTO {
    public Integer id;
    public Integer version;
    public enumEstadoFormato estado;
    public String observaciones;  // ← Las observaciones del coordinador
    public String nombreArchivo;
}