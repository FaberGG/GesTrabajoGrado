package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoRequestDTO;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoResponseDTO;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.stream.Collectors;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoServiceTest {

    @Mock IProyectoGradoRepository proyectoRepo;
    @Mock IFormatoARepository formatoRepo;
    @Mock IArchivoService archivoService;
    @Mock IUserRepository userRepo;

    @InjectMocks ProyectoGradoService service;

    private User docente(int id){ var u=new User(); u.setId(id); u.setRol(enumRol.DOCENTE); return u; }
    private User estudiante(int id){ var u=new User(); u.setId(id); u.setRol(enumRol.ESTUDIANTE); return u; }

        // ==================== PRUEBAS EXISTENTES ====================

        @Test
        void practicaProfesional_sinCarta_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "P";
            req.modalidad = enumModalidad.PRACTICA_PROFESIONAL;
            req.directorId = 10;
            req.estudiante1Id = 20;

            var formato = new File("formatoA.pdf");

            assertThrows(IllegalArgumentException.class,
                    () -> service.crearNuevoProyecto(req, formato));
        }

        @Test
        void practicaProfesional_cartaNoPdf_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "P";
            req.modalidad = enumModalidad.PRACTICA_PROFESIONAL;
            req.directorId = 10;
            req.estudiante1Id = 20;

            var formato = new File("formatoA.pdf");
            var carta   = new File("carta.txt");

            when(archivoService.validarTipoPDF(carta)).thenReturn(false);

            assertThrows(IllegalArgumentException.class,
                    () -> service.crearNuevoProyecto(req, formato, carta));
        }

        @Test
        void practicaProfesional_ok_guardaFormatoYCarta() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo="P"; req.modalidad=enumModalidad.PRACTICA_PROFESIONAL; req.directorId=10; req.estudiante1Id=20;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(20)).thenReturn(Optional.of(estudiante(20)));
            when(proyectoRepo.findByDirectorId(10)).thenReturn(List.of());
            when(proyectoRepo.save(any())).thenAnswer(inv -> { var p=(ProyectoGrado)inv.getArgument(0); p.setId(1); return p; });

            var formato = new File("formatoA.pdf");
            var carta = new File("carta.pdf");
            when(archivoService.validarTipoPDF(carta)).thenReturn(true);
            when(archivoService.guardarArchivo(formato, 1, 1)).thenReturn("/uploads/F1.pdf");
            when(archivoService.guardarArchivoCarta(carta, 1, 1)).thenReturn("/uploads/C1.pdf");

            service.crearNuevoProyecto(req, formato, carta);

            verify(formatoRepo).save(argThat(f ->
                    f.getProyectoGradoId().equals(1) &&
                            f.getNumeroIntento()==1 &&
                            "/uploads/F1.pdf".equals(f.getRutaArchivo()) &&
                            "/uploads/C1.pdf".equals(f.getRutaCartaAceptacion())
            ));
        }

        @Test
        void investigacion_conCeroEstudiantes_falla() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo="T"; req.modalidad=enumModalidad.INVESTIGACION; req.directorId=10;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));

            assertThrows(IllegalArgumentException.class, () -> service.crearNuevoProyecto(req, new File("a.pdf")));
        }

        @Test
        void roles_invalidos_falla() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo="T"; req.modalidad=enumModalidad.INVESTIGACION; req.directorId=10; req.estudiante1Id=21;

            var noDoc=new User(); noDoc.setId(10); noDoc.setRol(enumRol.ESTUDIANTE);
            when(userRepo.findById(10)).thenReturn(Optional.of(noDoc));

            assertThrows(IllegalArgumentException.class, () -> service.crearNuevoProyecto(req, new File("a.pdf")));
        }

        @Test
        void topeCincoDirecciones_falla() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo="T"; req.modalidad=enumModalidad.INVESTIGACION; req.directorId=10; req.estudiante1Id=21;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(21)).thenReturn(Optional.of(estudiante(21)));

            var enProceso = new ProyectoGrado(); enProceso.setEstado(enumEstadoProyecto.EN_PROCESO);
            var rechazado = new ProyectoGrado(); rechazado.setEstado(enumEstadoProyecto.RECHAZADO);
            when(proyectoRepo.findByDirectorId(10)).thenReturn(List.of(enProceso,rechazado,enProceso,rechazado,enProceso));

            assertThrows(IllegalArgumentException.class, () -> service.crearNuevoProyecto(req, new File("a.pdf")));
        }

        @Test
        void aprobarFormato_investigacion_ok() {
            var f = new FormatoA(); f.setId(51); f.setProyectoGradoId(78); f.setNumeroIntento(1);
            when(formatoRepo.findById(51)).thenReturn(Optional.of(f));

            var p = new ProyectoGrado(); p.setId(78); p.setModalidad(enumModalidad.INVESTIGACION);
            when(proyectoRepo.findById(78)).thenReturn(Optional.of(p));

            service.aprobarFormatoA(51, "ok", 100);

            assertEquals(enumEstadoProyecto.APROBADO, p.getEstado());
            verify(formatoRepo).update(any());
            verify(proyectoRepo).update(p);
        }

        // ==================== NUEVAS PRUEBAS ====================

        // --- PRUEBAS PARA subirNuevaVersion() ---

        @Test
        void subirNuevaVersion_proyectoNoExiste_lanzaExcepcion() {
            when(proyectoRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, 
                    () -> service.subirNuevaVersion(999, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_proyectoNoPuedeSubir_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setEstado(enumEstadoProyecto.APROBADO); // No puede subir más versiones

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalStateException.class, 
                    () -> service.subirNuevaVersion(1, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_practicaProfesional_sinCarta_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            proyecto.setNumeroIntentos(1);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalArgumentException.class, 
                    () -> service.subirNuevaVersion(1, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_practicaProfesional_cartaNoPdf_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            proyecto.setNumeroIntentos(1);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));
            when(archivoService.validarTipoPDF(any())).thenReturn(false);

            assertThrows(IllegalArgumentException.class, 
                    () -> service.subirNuevaVersion(1, new File("formato.pdf"), new File("carta.txt")));
        }

        @Test
        void subirNuevaVersion_investigacion_ok() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setModalidad(enumModalidad.INVESTIGACION);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            proyecto.setNumeroIntentos(1);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));
            when(archivoService.guardarArchivo(any(), eq(1), eq(2))).thenReturn("/uploads/F1-2.pdf");
            when(formatoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var archivo = new File("formato.pdf");
            FormatoA resultado = service.subirNuevaVersion(1, archivo);

            assertEquals(1, resultado.getProyectoGradoId());
            assertEquals(2, resultado.getNumeroIntento());
            assertEquals("/uploads/F1-2.pdf", resultado.getRutaArchivo());
            verify(proyectoRepo).update(proyecto);
            assertEquals(2, proyecto.getNumeroIntentos());
        }

        @Test
        void subirNuevaVersion_practicaProfesional_conCarta_ok() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            proyecto.setNumeroIntentos(1);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));
            when(archivoService.validarTipoPDF(any())).thenReturn(true);
            when(archivoService.guardarArchivo(any(), eq(1), eq(2))).thenReturn("/uploads/F1-2.pdf");
            when(archivoService.guardarArchivoCarta(any(), eq(1), eq(2))).thenReturn("/uploads/C1-2.pdf");
            when(formatoRepo.save(any())).thenAnswer(inv -> inv.getArgument(0));

            var archivo = new File("formato.pdf");
            var carta = new File("carta.pdf");
            FormatoA resultado = service.subirNuevaVersion(1, archivo, carta);

            assertEquals("/uploads/C1-2.pdf", resultado.getRutaCartaAceptacion());
            verify(proyectoRepo).update(proyecto);
        }

        // --- PRUEBAS PARA validarModalidadPracticaProfesional() ---

        @Test
        void validarModalidadPracticaProfesional_archivoValido_retornaTrue() {
            var archivo = new File("carta.pdf");
            when(archivoService.validarTipoPDF(archivo)).thenReturn(true);

            assertTrue(service.validarModalidadPracticaProfesional(archivo));
        }

        @Test
        void validarModalidadPracticaProfesional_archivoInvalido_retornaFalse() {
            var archivo = new File("carta.txt");
            when(archivoService.validarTipoPDF(archivo)).thenReturn(false);

            assertFalse(service.validarModalidadPracticaProfesional(archivo));
        }

        // --- PRUEBAS PARA obtenerProyectosPorDocente() ---

        @Test
        void obtenerProyectosPorDocente_sinProyectos_retornaListaVacia() {
            when(proyectoRepo.findByDocente(10)).thenReturn(List.of());

            List<ProyectoGradoResponseDTO> resultado = service.obtenerProyectosPorDocente(10);

            assertTrue(resultado.isEmpty());
        }

        @Test
        void obtenerProyectosPorDocente_conProyectos_retornaListaCompleta() {
            var proyecto1 = new ProyectoGrado();
            proyecto1.setId(1);
            proyecto1.setTitulo("Proyecto 1");
            proyecto1.setModalidad(enumModalidad.INVESTIGACION);
            proyecto1.setDirectorId(10);
            proyecto1.setEstado(enumEstadoProyecto.EN_PROCESO);
            proyecto1.setFechaCreacion(LocalDateTime.now());

            var proyecto2 = new ProyectoGrado();
            proyecto2.setId(2);
            proyecto2.setTitulo("Proyecto 2");
            proyecto2.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto2.setDirectorId(10);
            proyecto2.setEstado(enumEstadoProyecto.APROBADO);
            proyecto2.setFechaCreacion(LocalDateTime.now());

            when(proyectoRepo.findByDocente(10)).thenReturn(List.of(proyecto1, proyecto2));

            List<ProyectoGradoResponseDTO> resultado = service.obtenerProyectosPorDocente(10);

            assertEquals(2, resultado.size());
            assertEquals("Proyecto 1", resultado.get(0).titulo);
            assertEquals("Proyecto 2", resultado.get(1).titulo);
            assertEquals(enumModalidad.INVESTIGACION, resultado.get(0).modalidad);
            assertEquals(enumModalidad.PRACTICA_PROFESIONAL, resultado.get(1).modalidad);
        }

        // --- PRUEBAS PARA rechazarFormatoA() ---

        @Test
        void rechazarFormatoA_formatoNoExiste_lanzaExcepcion() {
            when(formatoRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, 
                    () -> service.rechazarFormatoA(999, "No válido", 100));
        }

        @Test
        void rechazarFormatoA_proyectoNoExiste_lanzaExcepcion() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(999);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, 
                    () -> service.rechazarFormatoA(1, "No válido", 100));
        }

        @Test
        void rechazarFormatoA_primerIntento_cambiaEstadoArechazado() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setNumeroIntento(1);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.rechazarFormatoA(1, "Falta información", 100);

            assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado());
            verify(formatoRepo).update(formato);
            verify(proyectoRepo).update(proyecto);
        }

        @Test
        void rechazarFormatoA_tercerIntento_rechazaDefinitivamente() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setNumeroIntento(3);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.rechazarFormatoA(1, "Rechazado definitivamente", 100);

            // Verificar que se llamó marcarComoRechazadoDefinitivo
            verify(proyectoRepo).update(proyecto);
        }

        // --- PRUEBAS ADICIONALES PARA aprobarFormatoA() ---

        @Test
        void aprobarFormatoA_formatoNoExiste_lanzaExcepcion() {
            when(formatoRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, 
                    () -> service.aprobarFormatoA(999, "Aprobado", 100));
        }

        @Test
        void aprobarFormatoA_proyectoNoExiste_lanzaExcepcion() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(999);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class, 
                    () -> service.aprobarFormatoA(1, "Aprobado", 100));
        }

        @Test
        void aprobarFormatoA_practicaProfesional_sinCarta_lanzaExcepcion() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            // Sin carta de aceptación

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalStateException.class, 
                    () -> service.aprobarFormatoA(1, "Aprobado", 100));
        }

        @Test
        void aprobarFormatoA_practicaProfesional_conCarta_aprueba() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setRutaCartaAceptacion("/uploads/carta.pdf"); // Tiene carta

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.aprobarFormatoA(1, "Aprobado", 100);

            assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
            verify(formatoRepo).update(formato);
            verify(proyectoRepo).update(proyecto);
        }

        @Test
        void crearNuevoProyecto_investigacionUnEstudiante_ok() {
        var req = new ProyectoGradoRequestDTO();
        req.titulo = "Investigación con un estudiante";
        req.modalidad = enumModalidad.INVESTIGACION;
        req.directorId = 10;
        req.estudiante1Id = 20;
        // Sin estudiante2Id

        when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
        when(userRepo.findById(20)).thenReturn(Optional.of(estudiante(20)));
        when(proyectoRepo.findByDirectorId(10)).thenReturn(List.of());
        when(proyectoRepo.save(any())).thenAnswer(inv -> {
            var p = (ProyectoGrado) inv.getArgument(0);
            p.setId(1);
            return p;
        });

        var archivo = new File("formato.pdf");
        when(archivoService.guardarArchivo(archivo, 1, 1)).thenReturn("/uploads/F1.pdf");

        ProyectoGrado resultado = service.crearNuevoProyecto(req, archivo);

        assertNotNull(resultado);
        assertEquals("Investigación con un estudiante", resultado.getTitulo());
        assertEquals(enumModalidad.INVESTIGACION, resultado.getModalidad());
        verify(proyectoRepo).save(any());
        verify(formatoRepo).save(any());
        }

        @Test
        void crearNuevoProyecto_investigacionDosEstudiantes_ok() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Investigación con dos estudiantes";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = 10;
            req.estudiante1Id = 20;
            req.estudiante2Id = 30;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(20)).thenReturn(Optional.of(estudiante(20)));
            when(userRepo.findById(30)).thenReturn(Optional.of(estudiante(30)));
            when(proyectoRepo.findByDirectorId(10)).thenReturn(List.of());
            when(proyectoRepo.save(any())).thenAnswer(inv -> {
                var p = (ProyectoGrado) inv.getArgument(0);
                p.setId(1);
                return p;
            });

            var archivo = new File("formato.pdf");
            when(archivoService.guardarArchivo(archivo, 1, 1)).thenReturn("/uploads/F1.pdf");

            ProyectoGrado resultado = service.crearNuevoProyecto(req, archivo);

            assertNotNull(resultado);
            assertEquals(20, resultado.getEstudiante1Id());
            assertEquals(30, resultado.getEstudiante2Id());
        }

        @Test
        void crearNuevoProyecto_directorNoExiste_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Test";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = 999; // No existe
            req.estudiante1Id = 20;

            when(userRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.crearNuevoProyecto(req, new File("formato.pdf")));
        }

        @Test
        void crearNuevoProyecto_estudianteNoExiste_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Test";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = 10;
            req.estudiante1Id = 999; // No existe

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(999)).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> service.crearNuevoProyecto(req, new File("formato.pdf")));
        }

        @Test
        void crearNuevoProyecto_estudianteNoEsEstudiante_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Test";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = 10;
            req.estudiante1Id = 20;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(20)).thenReturn(Optional.of(docente(20))); // Es docente, no estudiante

            assertThrows(IllegalArgumentException.class,
                    () -> service.crearNuevoProyecto(req, new File("formato.pdf")));
        }

        @Test
        void crearNuevoProyecto_requestNull_lanzaExcepcion() {
            assertThrows(NullPointerException.class,
                    () -> service.crearNuevoProyecto(null, new File("formato.pdf")));
        }

        @Test
        void crearNuevoProyecto_archivoNull_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Test";

            assertThrows(NullPointerException.class,
                    () -> service.crearNuevoProyecto(req, null));
        }

        @Test
        void crearNuevoProyecto_directorIdNull_lanzaExcepcion() {
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Test";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = null;
            req.estudiante1Id = 20;

            assertThrows(NullPointerException.class,
                    () -> service.crearNuevoProyecto(req, new File("formato.pdf")));
        }

        // ==================== PRUEBAS ADICIONALES PARA aprobarFormatoA() ====================

        @Test
        void aprobarFormatoA_conObservacionesYEvaluador_guardaCorrectamente() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setRutaCartaAceptacion("/uploads/carta.pdf"); // Para práctica profesional

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.aprobarFormatoA(1, "Excelente trabajo", 100);

            // Verificar que el formato fue actualizado con los datos correctos
            verify(formatoRepo).update(argThat(f -> {
                return f.getObservaciones().equals("Excelente trabajo") &&
                        f.getEvaluadoPor().equals(100) &&
                        f.getEstado() == enumEstadoFormato.APROBADO;
            }));

            assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
        }

        @Test
        void aprobarFormatoA_evaluadorIdNull_seGuardaNull() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setModalidad(enumModalidad.INVESTIGACION); // No necesita carta

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.aprobarFormatoA(1, "Aprobado", null);

            verify(formatoRepo).update(argThat(f -> f.getEvaluadoPor() == null));
        }

        @Test
        void aprobarFormatoA_observacionesVacias_seGuardanVacias() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setModalidad(enumModalidad.INVESTIGACION);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.aprobarFormatoA(1, "", 100);

            verify(formatoRepo).update(argThat(f -> f.getObservaciones().equals("")));
        }

        // ==================== PRUEBAS ADICIONALES PARA rechazarFormatoA() ====================

        @Test
        void rechazarFormatoA_segundoIntento_mantienEstadoRechazado() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setNumeroIntento(2); // Segundo intento

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);
            proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.rechazarFormatoA(1, "Aún faltan correcciones", 100);

            assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado());
            // No debe ser rechazado definitivo porque es el segundo intento
            assertNotEquals(enumEstadoProyecto.RECHAZADO_DEFINITIVO, proyecto.getEstado());
            verify(formatoRepo).update(formato);
            verify(proyectoRepo).update(proyecto);
        }

        @Test
        void rechazarFormatoA_conObservacionesDetalladas_guardaCorrectamente() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setNumeroIntento(1);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            String observacionesDetalladas = "Faltan referencias bibliográficas y el marco teórico necesita más desarrollo";

            service.rechazarFormatoA(1, observacionesDetalladas, 200);

            verify(formatoRepo).update(argThat(f -> {
                return f.getObservaciones().equals(observacionesDetalladas) &&
                        f.getEvaluadoPor().equals(200) &&
                        f.getEstado() == enumEstadoFormato.RECHAZADO;
            }));
        }

        @Test
        void rechazarFormatoA_observacionesNull_seGuardaNull() {
            var formato = new FormatoA();
            formato.setId(1);
            formato.setProyectoGradoId(10);
            formato.setNumeroIntento(1);

            var proyecto = new ProyectoGrado();
            proyecto.setId(10);

            when(formatoRepo.findById(1)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(10)).thenReturn(Optional.of(proyecto));

            service.rechazarFormatoA(1, null, 100);

            verify(formatoRepo).update(argThat(f -> f.getObservaciones() == null));
        }

    // ==================== PRUEBAS ADICIONALES PARA obtenerProyectosPorDocente() ====================

        @Test
        void obtenerProyectosPorDocente_proyectosConDiferentesEstados_retornaTodos() {
            var proyecto1 = new ProyectoGrado();
            proyecto1.setId(1);
            proyecto1.setTitulo("Proyecto En Proceso");
            proyecto1.setEstado(enumEstadoProyecto.EN_PROCESO);
            proyecto1.setModalidad(enumModalidad.INVESTIGACION);
            proyecto1.setDirectorId(10);
            proyecto1.setFechaCreacion(LocalDateTime.now());

            var proyecto2 = new ProyectoGrado();
            proyecto2.setId(2);
            proyecto2.setTitulo("Proyecto Aprobado");
            proyecto2.setEstado(enumEstadoProyecto.APROBADO);
            proyecto2.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
            proyecto2.setDirectorId(10);
            proyecto2.setFechaCreacion(LocalDateTime.now());

            var proyecto3 = new ProyectoGrado();
            proyecto3.setId(3);
            proyecto3.setTitulo("Proyecto Rechazado");
            proyecto3.setEstado(enumEstadoProyecto.RECHAZADO);
            proyecto3.setModalidad(enumModalidad.INVESTIGACION);
            proyecto3.setDirectorId(10);
            proyecto3.setFechaCreacion(LocalDateTime.now());

            when(proyectoRepo.findByDocente(10)).thenReturn(List.of(proyecto1, proyecto2, proyecto3));

            List<ProyectoGradoResponseDTO> resultado = service.obtenerProyectosPorDocente(10);

            assertEquals(3, resultado.size());

            // Verificar que se mapean todos los estados correctamente
            var estadosEncontrados = resultado.stream()
                    .map(dto -> dto.estado)
                    .collect(Collectors.toSet());

            assertTrue(estadosEncontrados.contains(enumEstadoProyecto.EN_PROCESO));
            assertTrue(estadosEncontrados.contains(enumEstadoProyecto.APROBADO));
            assertTrue(estadosEncontrados.contains(enumEstadoProyecto.RECHAZADO));
        }

        @Test
        void obtenerProyectosPorDocente_verificarMapeoCompletoDTO() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(100);
            proyecto.setTitulo("Proyecto Completo");
            proyecto.setModalidad(enumModalidad.INVESTIGACION);
            proyecto.setFechaCreacion(LocalDateTime.of(2024, 1, 15, 10, 30));
            proyecto.setDirectorId(10);
            proyecto.setCodirectorId(20);
            proyecto.setObjetivoGeneral("Objetivo general del proyecto");
            proyecto.setObjetivosEspecificos("Objetivos específicos del proyecto");
            proyecto.setEstudiante1Id(200);
            proyecto.setEstudiante2Id(300);
            proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);
            proyecto.setNumeroIntentos(2);

            when(proyectoRepo.findByDocente(10)).thenReturn(List.of(proyecto));

            List<ProyectoGradoResponseDTO> resultado = service.obtenerProyectosPorDocente(10);

            assertEquals(1, resultado.size());
            ProyectoGradoResponseDTO dto = resultado.get(0);

            // Verificar mapeo completo
            assertEquals(100, dto.id);
            assertEquals("Proyecto Completo", dto.titulo);
            assertEquals(enumModalidad.INVESTIGACION, dto.modalidad);
            assertEquals(LocalDateTime.of(2024, 1, 15, 10, 30), dto.fechaCreacion);
            assertEquals(10, dto.directorId);
            assertEquals(20, dto.codirectorId);
            assertEquals("Objetivo general del proyecto", dto.objetivoGeneral);
            assertEquals("Objetivos específicos del proyecto", dto.objetivosEspecificos);
            assertEquals(200, dto.estudiante1Id);
            assertEquals(300, dto.estudiante2Id);
            assertEquals(enumEstadoProyecto.EN_PROCESO, dto.estado);
            assertEquals(2, dto.numeroIntentos);
        }

        // ==================== PRUEBAS ADICIONALES PARA validarModalidadPracticaProfesional() ====================

        @Test
        void validarModalidadPracticaProfesional_archivoNull_retornaFalse() {
            // El método debería manejar null de forma segura
            when(archivoService.validarTipoPDF(null)).thenReturn(false);

            assertFalse(service.validarModalidadPracticaProfesional(null));
        }

        @Test
        void validarModalidadPracticaProfesional_archivoDocx_retornaFalse() {
            var archivo = new File("documento.docx");
            when(archivoService.validarTipoPDF(archivo)).thenReturn(false);

            assertFalse(service.validarModalidadPracticaProfesional(archivo));
        }

        @Test
        void validarModalidadPracticaProfesional_archivoImagen_retornaFalse() {
            var archivo = new File("imagen.jpg");
            when(archivoService.validarTipoPDF(archivo)).thenReturn(false);

            assertFalse(service.validarModalidadPracticaProfesional(archivo));
        }

        // ==================== PRUEBAS ADICIONALES PARA subirNuevaVersion() ====================

        @Test
        void subirNuevaVersion_proyectoAprobado_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setEstado(enumEstadoProyecto.APROBADO); // No puede subir más
            proyecto.setNumeroIntentos(1);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalStateException.class,
                    () -> service.subirNuevaVersion(1, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_proyectoRechazadoDefinitivo_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO_DEFINITIVO);
            proyecto.setNumeroIntentos(3);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalStateException.class,
                    () -> service.subirNuevaVersion(1, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_proyectoEnProcesoConVariosIntentos_lanzaExcepcion() {
            var proyecto = new ProyectoGrado();
            proyecto.setId(1);
            proyecto.setEstado(enumEstadoProyecto.EN_PROCESO); // No puede subir
            proyecto.setNumeroIntentos(2);

            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            assertThrows(IllegalStateException.class,
                    () -> service.subirNuevaVersion(1, new File("formato.pdf")));
        }

        @Test
        void subirNuevaVersion_argumentosNull_lanzaExcepciones() {
            // Proyecto ID null
            assertThrows(NullPointerException.class,
                    () -> service.subirNuevaVersion(null, new File("formato.pdf")));

            // Archivo null
            assertThrows(NullPointerException.class,
                    () -> service.subirNuevaVersion(1, null));
        }

        // ==================== PRUEBAS DE INTEGRACIÓN COMPLEJAS ====================

        @Test
        void flujoCompletoCreacionYAprobacion_investigacion() {
            // Setup inicial
            var req = new ProyectoGradoRequestDTO();
            req.titulo = "Proyecto de Investigación Completo";
            req.modalidad = enumModalidad.INVESTIGACION;
            req.directorId = 10;
            req.estudiante1Id = 20;

            when(userRepo.findById(10)).thenReturn(Optional.of(docente(10)));
            when(userRepo.findById(20)).thenReturn(Optional.of(estudiante(20)));
            when(proyectoRepo.findByDirectorId(10)).thenReturn(List.of());

            // Mock para creación
            when(proyectoRepo.save(any())).thenAnswer(inv -> {
                var p = (ProyectoGrado) inv.getArgument(0);
                p.setId(1);
                return p;
            });
            when(archivoService.guardarArchivo(any(), eq(1), eq(1))).thenReturn("/uploads/F1.pdf");
            when(formatoRepo.save(any())).thenAnswer(inv -> {
                var f = (FormatoA) inv.getArgument(0);
                f.setId(100);
                return f;
            });

            // 1. Crear proyecto
            ProyectoGrado proyecto = service.crearNuevoProyecto(req, new File("formato.pdf"));
            assertNotNull(proyecto);
            assertEquals(1, proyecto.getId());

            // 2. Setup para aprobación
            var formato = new FormatoA();
            formato.setId(100);
            formato.setProyectoGradoId(1);

            when(formatoRepo.findById(100)).thenReturn(Optional.of(formato));
            when(proyectoRepo.findById(1)).thenReturn(Optional.of(proyecto));

            // 3. Aprobar formato
            service.aprobarFormatoA(100, "Proyecto excelente", 50);

            // 4. Verificaciones finales
            assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
            verify(proyectoRepo, times(1)).save(any()); // Creación
            verify(proyectoRepo, times(1)).update(any()); // Aprobación
            verify(formatoRepo, times(1)).save(any()); // Creación formato
            verify(formatoRepo, times(1)).update(any()); // Aprobación formato
        }
    }