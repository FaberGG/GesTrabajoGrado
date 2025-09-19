package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.service.*;
import co.unicauca.gestiontrabajogrado.dto.*;
import java.io.File;
import java.util.List;

public class ProyectoGradoController {
    private final IProyectoGradoService service;
    public ProyectoGradoController(IProyectoGradoService service) { this.service = service; }

    public void manejarNuevoProyecto(ProyectoGradoRequestDTO request, File archivo) {
        service.crearNuevoProyecto(request, archivo);
    }
    public void manejarNuevaVersion(Integer proyectoId, File archivo) {
        service.subirNuevaVersion(proyectoId, archivo);
    }
    public List<ProyectoGradoResponseDTO> cargarMisProyectos(Integer docenteId) {
        return service.obtenerProyectosPorDocente(docenteId);
    }
    public void rechazarFormato(Integer formatoId, String observaciones, Integer evaluadorId) {
        service.rechazarFormatoA(formatoId, observaciones, evaluadorId);
    }
    public void aprobarFormato(Integer formatoId, String observaciones, Integer evaluadorId) {
        service.aprobarFormatoA(formatoId, observaciones, evaluadorId);
    }
}
