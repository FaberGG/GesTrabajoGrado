package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;
import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.dto.*;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ProyectoGradoService implements IProyectoGradoService {
    private final IProyectoGradoRepository proyectoRepo;
    private final IFormatoARepository formatoRepo;
    private final IArchivoService archivoService;

    public ProyectoGradoService(IProyectoGradoRepository p, IFormatoARepository f, IArchivoService a) {
        this.proyectoRepo = p; this.formatoRepo = f; this.archivoService = a;
    }

    @Override
    public ProyectoGrado crearNuevoProyecto(ProyectoGradoRequestDTO req, File archivo) {
        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo(req.titulo);
        p.setModalidad(req.modalidad);
        p.setDirectorId(req.directorId);
        p.setCodirectorId(req.codirectorId);
        p.setObjetivoGeneral(req.objetivoGeneral);
        p.setObjetivosEspecificos(req.objetivosEspecificos);
        p.setEstudiante1Id(req.estudiante1Id);
        p.setEstudiante2Id(req.estudiante2Id);
        p = proyectoRepo.save(p);

        String ruta = archivoService.guardarArchivo(archivo, p.getId(), 1);
        FormatoA f = new FormatoA();
        f.setProyectoGradoId(p.getId());
        f.setNumeroIntento(1);
        f.setRutaArchivo(ruta);
        f.setNombreArchivo(archivo.getName());
        formatoRepo.save(f);
        return p;
    }

    @Override
    public FormatoA subirNuevaVersion(Integer proyectoId, File archivo) {
        ProyectoGrado p = proyectoRepo.findById(proyectoId).orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        if (!p.puedeSubirNuevaVersion()) throw new IllegalStateException("No se puede subir nueva versión (estado/intentos)");
        int intento = p.getNumeroIntentos() + 1;
        String ruta = archivoService.guardarArchivo(archivo, proyectoId, intento);
        FormatoA f = new FormatoA();
        f.setProyectoGradoId(proyectoId);
        f.setNumeroIntento(intento);
        f.setRutaArchivo(ruta);
        f.setNombreArchivo(archivo.getName());
        f = formatoRepo.save(f);
        p.incrementarIntentos();
        proyectoRepo.update(p);
        return f;
    }

    @Override
    public boolean validarModalidadPracticaProfesional(File archivo) {
        // Validación simple placeholder: el nombre del archivo contiene "carta"
        String name = archivo.getName().toLowerCase();
        return name.contains("carta") && archivoService.validarTipoPDF(archivo);
    }

    @Override
    public List<ProyectoGradoResponseDTO> obtenerProyectosPorDocente(Integer docenteId) {
        return proyectoRepo.findByDocente(docenteId).stream().map(this::toDto).collect(Collectors.toList());
    }

    @Override
    public void rechazarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId) {
        FormatoA f = formatoRepo.findById(formatoId).orElseThrow(() -> new IllegalArgumentException("Formato no encontrado"));
        f.rechazar(evaluadorId, observaciones);
        formatoRepo.update(f);

        ProyectoGrado p = proyectoRepo.findById(f.getProyectoGradoId()).orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        if (f.getNumeroIntento() >= 3) {
            p.marcarComoRechazadoDefinitivo();
            proyectoRepo.update(p);
        } else {
            // solo queda en RECHAZADO (permite nueva versión)
            p.setEstado(enumEstadoProyecto.RECHAZADO);
            proyectoRepo.update(p);
        }
    }

    @Override
    public void aprobarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId) {
        FormatoA f = formatoRepo.findById(formatoId).orElseThrow(() -> new IllegalArgumentException("Formato no encontrado"));
        f.aprobar(evaluadorId, observaciones);
        formatoRepo.update(f);

        ProyectoGrado p = proyectoRepo.findById(f.getProyectoGradoId()).orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));
        p.setEstado(enumEstadoProyecto.APROBADO);
        proyectoRepo.update(p);
    }

    private ProyectoGradoResponseDTO toDto(ProyectoGrado p) {
        ProyectoGradoResponseDTO d = new ProyectoGradoResponseDTO();
        d.id = p.getId();
        d.titulo = p.getTitulo();
        d.modalidad = p.getModalidad();
        d.fechaCreacion = p.getFechaCreacion();
        d.directorId = p.getDirectorId();
        d.codirectorId = p.getCodirectorId();
        d.objetivoGeneral = p.getObjetivoGeneral();
        d.objetivosEspecificos = p.getObjetivosEspecificos();
        d.estudiante1Id = p.getEstudiante1Id();
        d.estudiante2Id = p.getEstudiante2Id();
        d.estado = p.getEstado();
        d.numeroIntentos = p.getNumeroIntentos();
        return d;
    }
}
