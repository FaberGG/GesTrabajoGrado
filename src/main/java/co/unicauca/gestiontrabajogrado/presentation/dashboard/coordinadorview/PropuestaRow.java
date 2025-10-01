package co.unicauca.gestiontrabajogrado.presentation.dashboard.coordinadorview;

import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoProyecto;

public record PropuestaRow(
        Integer proyectoId,
        Integer formatoId,
        String  titulo,
        enumEstadoProyecto estadoProyecto,
        enumEstadoFormato  estadoFormato,
        Integer intento
) {
    /** Devuelve una copia con el estado de formato actualizado */
    public PropuestaRow withEstado(enumEstadoFormato nuevo) {
        return new PropuestaRow(
                this.proyectoId(),
                this.formatoId(),
                this.titulo(),
                this.estadoProyecto(),
                nuevo,                 // <— aquí el nuevo estado
                this.intento()
        );
    }
}
