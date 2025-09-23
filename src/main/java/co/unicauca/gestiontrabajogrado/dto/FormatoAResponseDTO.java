package co.unicauca.gestiontrabajogrado.dto;

import java.time.LocalDateTime;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;

public class FormatoAResponseDTO {
    public Integer id;
    public Integer proyectoGradoId;
    public Integer numeroIntento;
    public String rutaArchivo;
    public String nombreArchivo;
    public String rutaCartaAceptacion; // Nuevo campo
    public String nombreCartaAceptacion; // Nuevo campo
    public LocalDateTime fechaCarga;
    public enumEstadoFormato estado;
    public String observaciones;

    // metodo de utilidad
    public boolean tieneCartaAceptacion() {
        return rutaCartaAceptacion != null && !rutaCartaAceptacion.trim().isEmpty();
    }
}