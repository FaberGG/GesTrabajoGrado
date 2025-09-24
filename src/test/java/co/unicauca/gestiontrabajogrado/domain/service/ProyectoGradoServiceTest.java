package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.dto.ProyectoGradoRequestDTO;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProyectoGradoServiceTest {

    @Mock IProyectoGradoRepository proyectoRepo;
    @Mock IFormatoARepository formatoRepo;
    @Mock IArchivoService archivoService;
    @Mock IUserRepository userRepo;

    @InjectMocks ProyectoGradoService service;

    private User docente(int id){ var u=new User(); u.setId(id); u.setRol(enumRol.DOCENTE); return u; }
    private User estudiante(int id){ var u=new User(); u.setId(id); u.setRol(enumRol.ESTUDIANTE); return u; }

    @Test
    void practicaProfesional_sinCarta_lanzaExcepcion() {
        var req = new ProyectoGradoRequestDTO();
        req.titulo = "P";
        req.modalidad = enumModalidad.PRACTICA_PROFESIONAL;
        req.directorId = 10;
        req.estudiante1Id = 20;

        // NO stubs a userRepo aquí: no se usan
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

        // SOLO este stub es necesario
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
}

