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
    private final IUserRepository userRepo;

    public ProyectoGradoService(IProyectoGradoRepository p,
                                IFormatoARepository f,
                                IArchivoService a,
                                IUserRepository u) {
        this.proyectoRepo = p;
        this.formatoRepo = f;
        this.archivoService = a;
        this.userRepo = u;
    }

    // ---- CREAR PROYECTO ----
    @Override
    public ProyectoGrado crearNuevoProyecto(ProyectoGradoRequestDTO req, File archivo) {
        return crearNuevoProyecto(req, archivo, null);
    }

    public FormatoADetalleDTO obtenerUltimoFormatoA(Integer proyectoId) {
        Optional<FormatoA> formatoOpt = formatoRepo.findLastFormatoByProyectoId(proyectoId);

        if (formatoOpt.isEmpty()) {
            throw new RuntimeException("No se encontró un Formato A para el proyecto con id " + proyectoId);
        }

        FormatoA formato = formatoOpt.get();

        FormatoADetalleDTO dto = new FormatoADetalleDTO();
        dto.id = formato.getId();
        dto.version = formato.getNumeroIntento();
        dto.estado = formato.getEstado();
        dto.observaciones = formato.getObservaciones();
        dto.nombreArchivo = formato.getNombreArchivo();

        return dto;
    }


    public ProyectoGradoResponseDTO obtenerProyectoPorId(Integer idProyecto) {
        Optional<ProyectoGrado> proyectoOpt = proyectoRepo.findById(idProyecto);

        if (proyectoOpt.isEmpty()) {
            throw new RuntimeException("Proyecto con id " + idProyecto + " no encontrado.");
        }

        ProyectoGrado proyecto = proyectoOpt.get();

        // Convertir entidad a DTO
        ProyectoGradoResponseDTO dto = new ProyectoGradoResponseDTO();
        dto.id = proyecto.getId();
        dto.titulo = proyecto.getTitulo();
        dto.modalidad = proyecto.getModalidad();
        dto.fechaCreacion = proyecto.getFechaCreacion();
        dto.directorId = proyecto.getDirectorId();
        dto.codirectorId = proyecto.getCodirectorId();
        dto.objetivoGeneral = proyecto.getObjetivoGeneral();
        dto.objetivosEspecificos = proyecto.getObjetivosEspecificos();
        dto.estudiante1Id = proyecto.getEstudiante1Id();
        dto.estudiante2Id = proyecto.getEstudiante2Id();
        dto.estado = proyecto.getEstado();
        dto.numeroIntentos = proyecto.getNumeroIntentos();

        return dto;
    }

    // Sobrecarga con carta de aceptación
    public ProyectoGrado crearNuevoProyecto(ProyectoGradoRequestDTO req, File archivo, File cartaAceptacion) {
        Objects.requireNonNull(req, "request es requerido");
        Objects.requireNonNull(archivo, "archivo PDF del Formato A es requerido");

        // --- Reglas de práctica profesional (carta obligatoria y PDF) ---
        if (req.modalidad == enumModalidad.PRACTICA_PROFESIONAL) {
            if (cartaAceptacion == null) {
                throw new IllegalArgumentException("Para modalidad de Práctica Profesional se requiere carta de aceptación");
            }
            if (!archivoService.validarTipoPDF(cartaAceptacion)) {
                throw new IllegalArgumentException("La carta de aceptación no es válida (debe ser PDF)");
            }
        }

        // --- VALIDACIONES DE NEGOCIO ---
        // 1) Director existe y es DOCENTE
        User director = userRepo.findById(Objects.requireNonNull(req.directorId, "directorId es requerido"))
                .orElseThrow(() -> new IllegalArgumentException("El director indicado no existe"));
        if (director.getRol() != enumRol.DOCENTE) {
            throw new IllegalArgumentException("El usuario indicado como director no es DOCENTE");
        }

        // 2) Estudiantes (size por modalidad)
        List<Integer> estudiantes = new ArrayList<>();
        //debug imprimir ids
        System.out.println("id1:"+req.estudiante1Id+" id2:"+req.estudiante2Id);
        if (req.estudiante1Id != null) estudiantes.add(req.estudiante1Id);
        if (req.estudiante2Id != null) estudiantes.add(req.estudiante2Id);

        int size = estudiantes.size();
        //imprimo debug estudiantes
        System.out.println("Estudiantes " + estudiantes.toString());

        int max = req.modalidad.getMaxEstudiantes();

        if (req.modalidad == enumModalidad.PRACTICA_PROFESIONAL) {
            if (size != 1) throw new IllegalArgumentException("La Práctica Profesional es individual (exactamente 1 estudiante).");
        } else { // INVESTIGACION
            if (size < 1 || size > max) throw new IllegalArgumentException("Para Investigación se permite 1 o 2 estudiantes.");
        }

        // 3) Cada estudiante existe y es ESTUDIANTE
        for (Integer idEst : estudiantes) {
            User u = userRepo.findById(idEst)
                    .orElseThrow(() -> new IllegalArgumentException("Estudiante con id " + idEst + " no existe"));
            if (u.getRol() != enumRol.ESTUDIANTE) {
                throw new IllegalArgumentException("El usuario " + idEst + " no es ESTUDIANTE");
            }
        }

        // 4) Tope de direcciones por docente: máx 5 en curso (EN_PROCESO o RECHAZADO)
        long enCurso = proyectoRepo.findByDirectorId(req.directorId).stream()
                .filter(p -> p.getEstado() == enumEstadoProyecto.EN_PROCESO || p.getEstado() == enumEstadoProyecto.RECHAZADO)
                .count();
        if (enCurso >= 5) {
            throw new IllegalArgumentException("El director ya dirige 5 trabajos en curso. No puede asumir más.");
        }

        // --- Persistencia ---
        ProyectoGrado p = new ProyectoGrado();
        p.setTitulo(req.titulo);
        p.setModalidad(req.modalidad);
        p.setDirectorId(req.directorId);
        p.setCodirectorId(req.codirectorId);
        p.setObjetivoGeneral(req.objetivoGeneral);
        p.setObjetivosEspecificos(req.objetivosEspecificos);
        p.setEstudiante1Id(req.estudiante1Id);
        p.setEstudiante2Id(req.estudiante2Id);
        if (p.getFechaCreacion() == null) {
            p.setFechaCreacion(java.time.LocalDateTime.now());
        }

        p = proyectoRepo.save(p);

        // Guardar Formato A (intento 1)
        String ruta = archivoService.guardarArchivo(archivo, p.getId(), 1);

        FormatoA f = new FormatoA();
        f.setProyectoGradoId(p.getId());
        f.setNumeroIntento(1);
        f.setRutaArchivo(ruta);
        f.setNombreArchivo(archivo.getName());

        if (req.modalidad == enumModalidad.PRACTICA_PROFESIONAL && cartaAceptacion != null) {
            String rutaCarta = archivoService.guardarArchivoCarta(cartaAceptacion, p.getId(), 1);
            f.setRutaCartaAceptacion(rutaCarta);
            f.setNombreCartaAceptacion(cartaAceptacion.getName());
        }

        formatoRepo.save(f);
        return p;
    }

    // ---- SUBIR NUEVA VERSIÓN ----
    @Override
    public FormatoA subirNuevaVersion(Integer proyectoId, File archivo) {
        return subirNuevaVersion(proyectoId, archivo, null);
    }
    public FormatoA subirNuevaVersion(Integer proyectoId, File archivo, File cartaAceptacion,
                                      String objetivoGeneral, String objetivosEspecificos) {
        Objects.requireNonNull(proyectoId, "proyectoId es requerido");
        Objects.requireNonNull(archivo, "archivo PDF es requerido");
        Objects.requireNonNull(objetivoGeneral, "objetivoGeneral es requerido");
        Objects.requireNonNull(objetivosEspecificos, "objetivosEspecificos es requerido");

        ProyectoGrado p = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (!p.puedeSubirNuevaVersion()) {
            throw new IllegalStateException("No se puede subir nueva versión (estado/intentos)");
        }

        if (p.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL) {
            if (cartaAceptacion == null) {
                throw new IllegalArgumentException("Para modalidad de Práctica Profesional se requiere carta de aceptación");
            }
            if (!archivoService.validarTipoPDF(cartaAceptacion)) {
                throw new IllegalArgumentException("La carta de aceptación no es válida (debe ser PDF)");
            }
        }

        // Actualizar objetivos del proyecto
        p.setObjetivoGeneral(objetivoGeneral.trim());
        p.setObjetivosEspecificos(objetivosEspecificos.trim());

        // Obtener el siguiente número de versión basado en los FormatoA existentes
        Optional<FormatoA> ultimoFormato = formatoRepo.findLastFormatoByProyectoId(proyectoId);
        int siguienteVersion = ultimoFormato.map(f -> f.getNumeroIntento() + 1).orElse(1);

        String ruta = archivoService.guardarArchivo(archivo, proyectoId, siguienteVersion);

        FormatoA f = new FormatoA();
        f.setProyectoGradoId(proyectoId);
        f.setNumeroIntento(siguienteVersion);
        f.setRutaArchivo(ruta);
        f.setNombreArchivo(archivo.getName());

        if (p.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL && cartaAceptacion != null) {
            String rutaCarta = archivoService.guardarArchivoCarta(cartaAceptacion, proyectoId, siguienteVersion);
            f.setRutaCartaAceptacion(rutaCarta);
            f.setNombreCartaAceptacion(cartaAceptacion.getName());
        }

        f = formatoRepo.save(f);

        p.setNumeroIntentos(siguienteVersion);

        // Cambiar estado a EN_PROCESO
        p.setEstado(enumEstadoProyecto.EN_PROCESO);
        proyectoRepo.update(p);

        return f;
    }

    // Sobrecarga con carta
    public FormatoA subirNuevaVersion(Integer proyectoId, File archivo, File cartaAceptacion) {
        Objects.requireNonNull(proyectoId, "proyectoId es requerido");
        Objects.requireNonNull(archivo, "archivo PDF es requerido");

        ProyectoGrado p = proyectoRepo.findById(proyectoId)
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (!p.puedeSubirNuevaVersion()) {
            throw new IllegalStateException("No se puede subir nueva versión (estado/intentos)");
        }

        if (p.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL) {
            if (cartaAceptacion == null) {
                throw new IllegalArgumentException("Para modalidad de Práctica Profesional se requiere carta de aceptación");
            }
            if (!archivoService.validarTipoPDF(cartaAceptacion)) {
                throw new IllegalArgumentException("La carta de aceptación no es válida (debe ser PDF)");
            }
        }

        int intento = p.getNumeroIntentos() + 1;
        String ruta = archivoService.guardarArchivo(archivo, proyectoId, intento);

        FormatoA f = new FormatoA();
        f.setProyectoGradoId(proyectoId);
        f.setNumeroIntento(intento);
        f.setRutaArchivo(ruta);
        f.setNombreArchivo(archivo.getName());

        if (p.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL && cartaAceptacion != null) {
            String rutaCarta = archivoService.guardarArchivoCarta(cartaAceptacion, proyectoId, intento);
            f.setRutaCartaAceptacion(rutaCarta);
            f.setNombreCartaAceptacion(cartaAceptacion.getName());
        }

        f = formatoRepo.save(f);
        p.incrementarIntentos();
        proyectoRepo.update(p);
        return f;
    }

    // ---- VALIDACIÓN CARTA (interface) ----
    @Override
    public boolean validarModalidadPracticaProfesional(File archivo) {
        return archivoService.validarTipoPDF(archivo);
    }

    // ---- CONSULTAS ----
    @Override
    public List<ProyectoGradoResponseDTO> obtenerProyectosPorDocente(Integer docenteId) {
        return proyectoRepo.findByDocente(docenteId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ---- EVALUACIÓN FORMATO A ----
    @Override
    public void rechazarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId) {
        FormatoA f = formatoRepo.findById(formatoId)
                .orElseThrow(() -> new IllegalArgumentException("Formato no encontrado"));

        f.rechazar(evaluadorId, observaciones);
        formatoRepo.update(f);

        ProyectoGrado p = proyectoRepo.findById(f.getProyectoGradoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (f.getNumeroIntento() >= 3) {
            p.marcarComoRechazadoDefinitivo();
        } else {
            p.setEstado(enumEstadoProyecto.RECHAZADO);
        }
        proyectoRepo.update(p);
    }

    @Override
    public void aprobarFormatoA(Integer formatoId, String observaciones, Integer evaluadorId) {
        FormatoA f = formatoRepo.findById(formatoId)
                .orElseThrow(() -> new IllegalArgumentException("Formato no encontrado"));

        ProyectoGrado p = proyectoRepo.findById(f.getProyectoGradoId())
                .orElseThrow(() -> new IllegalArgumentException("Proyecto no encontrado"));

        if (p.getModalidad() == enumModalidad.PRACTICA_PROFESIONAL && !f.tieneCartaAceptacion()) {
            throw new IllegalStateException("No se puede aprobar: falta carta de aceptación para práctica profesional");
        }

        f.aprobar(evaluadorId, observaciones);
        formatoRepo.update(f);

        p.setEstado(enumEstadoProyecto.APROBADO);
        proyectoRepo.update(p);
    }
    @Override
    public ProyectoGradoResponseDTO obtenerProyectoPorEstudiante(Integer estudianteId) {
        Objects.requireNonNull(estudianteId, "estudianteId es requerido");

        // Verificar que el usuario existe y es estudiante
        User estudiante = userRepo.findById(estudianteId)
                .orElseThrow(() -> new IllegalArgumentException("Estudiante con id " + estudianteId + " no existe"));

        if (estudiante.getRol() != enumRol.ESTUDIANTE) {
            throw new IllegalArgumentException("El usuario no es ESTUDIANTE");
        }

        // Buscar el proyecto donde el estudiante participa (como estudiante1 o estudiante2)
        List<ProyectoGrado> proyectos = proyectoRepo.findByEstudianteId(estudianteId);

        if (proyectos.isEmpty()) {
            return null; // o lanzar excepción según tus necesidades de negocio
        }

        // Un estudiante debería tener solo un proyecto activo, tomamos el primero
        ProyectoGrado proyecto = proyectos.get(0);

        return toDto(proyecto);
    }

    // ---- Mapper ----
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