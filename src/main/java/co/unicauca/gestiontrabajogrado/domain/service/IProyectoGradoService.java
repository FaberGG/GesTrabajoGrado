package co.unicauca.gestiontrabajogrado.domain.service;

import java.io.File;
import java.util.*;
import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.dto.*;

public interface IProyectoGradoService {
    ProyectoGrado crearNuevoProyecto(ProyectoGradoRequestDTO request, File archivo);
    FormatoA subirNuevaVersion(Integer proyectoId, File archivo);
    boolean validarModalidadPracticaProfesional(File archivo);
    ProyectoGradoResponseDTO obtenerProyectoPorEstudiante(Integer estudianteId);
    List<ProyectoGradoResponseDTO> obtenerProyectosPorDocente(Integer docenteId);
    void rechazarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId);
    void aprobarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId);
}
