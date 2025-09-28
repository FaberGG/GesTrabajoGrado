package co.unicauca.gestiontrabajogrado.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class ProyectoGradoTest {

    private ProyectoGrado proyecto;

    @BeforeEach
    void setUp() {
        proyecto = new ProyectoGrado();
        proyecto.setId(1);
        proyecto.setTitulo("Sistema de Gestión de Trabajos de Grado");
        proyecto.setModalidad(enumModalidad.INVESTIGACION);
        proyecto.setDirectorId(100);
        proyecto.setEstudiante1Id(200);
        proyecto.setFechaCreacion(LocalDateTime.now());
    }

    // ==================== PRUEBAS PARA puedeSubirNuevaVersion() ====================

    @Test
    void puedeSubirNuevaVersion_estadoEnProceso_retornaFalse() {
        proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);
        proyecto.setNumeroIntentos(1);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoAprobado_retornaFalse() {
        proyecto.setEstado(enumEstadoProyecto.APROBADO);
        proyecto.setNumeroIntentos(1);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoDefinitivo_retornaFalse() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO_DEFINITIVO);
        proyecto.setNumeroIntentos(3);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoPrimerIntento_retornaTrue() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(1);
        
        assertTrue(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoSegundoIntento_retornaTrue() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(2);
        
        assertTrue(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoTercerIntento_retornaFalse() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(3);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoMasDeTresIntentos_retornaFalse() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(4);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_estadoRechazadoCeroIntentos_retornaTrue() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(0);
        
        assertTrue(proyecto.puedeSubirNuevaVersion());
    }

    // ==================== PRUEBAS PARA marcarComoRechazadoDefinitivo() ====================

    @Test
    void marcarComoRechazadoDefinitivo_cambiaEstadoCorrectamente() {
        // Estado inicial diferente
        proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);
        
        proyecto.marcarComoRechazadoDefinitivo();
        
        assertEquals(enumEstadoProyecto.RECHAZADO_DEFINITIVO, proyecto.getEstado());
    }

    @Test
    void marcarComoRechazadoDefinitivo_desdeEstadoRechazado_cambiaCorrectamente() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        
        proyecto.marcarComoRechazadoDefinitivo();
        
        assertEquals(enumEstadoProyecto.RECHAZADO_DEFINITIVO, proyecto.getEstado());
    }

    @Test
    void marcarComoRechazadoDefinitivo_desdeEstadoAprobado_cambiaCorrectamente() {
        proyecto.setEstado(enumEstadoProyecto.APROBADO);
        
        proyecto.marcarComoRechazadoDefinitivo();
        
        assertEquals(enumEstadoProyecto.RECHAZADO_DEFINITIVO, proyecto.getEstado());
    }

    @Test
    void marcarComoRechazadoDefinitivo_noAfectaOtrosCampos() {
        Integer idOriginal = proyecto.getId();
        String tituloOriginal = proyecto.getTitulo();
        Integer intentosOriginales = proyecto.getNumeroIntentos();
        
        proyecto.marcarComoRechazadoDefinitivo();
        
        assertEquals(idOriginal, proyecto.getId());
        assertEquals(tituloOriginal, proyecto.getTitulo());
        assertEquals(intentosOriginales, proyecto.getNumeroIntentos());
    }

    // ==================== PRUEBAS PARA incrementarIntentos() ====================

    @Test
    void incrementarIntentos_valorInicial1_incrementaA2() {
        proyecto.setNumeroIntentos(1);
        
        proyecto.incrementarIntentos();
        
        assertEquals(2, proyecto.getNumeroIntentos());
    }

    @Test
    void incrementarIntentos_valorInicial2_incrementaA3() {
        proyecto.setNumeroIntentos(2);
        
        proyecto.incrementarIntentos();
        
        assertEquals(3, proyecto.getNumeroIntentos());
    }

    @Test
    void incrementarIntentos_valorInicial0_incrementaA1() {
        proyecto.setNumeroIntentos(0);
        
        proyecto.incrementarIntentos();
        
        assertEquals(1, proyecto.getNumeroIntentos());
    }

    @Test
    void incrementarIntentos_valorAlto_incrementaCorrectamente() {
        proyecto.setNumeroIntentos(10);
        
        proyecto.incrementarIntentos();
        
        assertEquals(11, proyecto.getNumeroIntentos());
    }

    @Test
    void incrementarIntentos_valorNull_manejaNullSafely() {
        proyecto.setNumeroIntentos(null);
        
        // Esto debería lanzar NullPointerException según la implementación actual
        assertThrows(NullPointerException.class, () -> {
            proyecto.incrementarIntentos();
        });
    }

    @Test
    void incrementarIntentos_noAfectaOtrosCampos() {
        Integer idOriginal = proyecto.getId();
        String tituloOriginal = proyecto.getTitulo();
        enumEstadoProyecto estadoOriginal = proyecto.getEstado();
        
        proyecto.incrementarIntentos();
        
        assertEquals(idOriginal, proyecto.getId());
        assertEquals(tituloOriginal, proyecto.getTitulo());
        assertEquals(estadoOriginal, proyecto.getEstado());
    }

    // ==================== PRUEBAS PARA VALORES POR DEFECTO ====================

    @Test
    void constructor_estadoInicialEnProceso() {
        ProyectoGrado nuevoProyecto = new ProyectoGrado();
        
        assertEquals(enumEstadoProyecto.EN_PROCESO, nuevoProyecto.getEstado());
    }

    @Test
    void constructor_numeroIntentosInicial1() {
        ProyectoGrado nuevoProyecto = new ProyectoGrado();
        
        assertEquals(1, nuevoProyecto.getNumeroIntentos());
    }

    @Test
    void constructor_otrosCamposNull() {
        ProyectoGrado nuevoProyecto = new ProyectoGrado();
        
        assertNull(nuevoProyecto.getId());
        assertNull(nuevoProyecto.getTitulo());
        assertNull(nuevoProyecto.getModalidad());
        assertNull(nuevoProyecto.getFechaCreacion());
        assertNull(nuevoProyecto.getDirectorId());
        assertNull(nuevoProyecto.getCodirectorId());
        assertNull(nuevoProyecto.getObjetivoGeneral());
        assertNull(nuevoProyecto.getObjetivosEspecificos());
        assertNull(nuevoProyecto.getEstudiante1Id());
        assertNull(nuevoProyecto.getEstudiante2Id());
    }

    // ==================== PRUEBAS PARA GETTERS Y SETTERS ====================

    @Test
    void settersYGetters_funcionanCorrectamente() {
        ProyectoGrado nuevoProyecto = new ProyectoGrado();
        LocalDateTime fecha = LocalDateTime.now();
        
        // Probar todos los setters
        nuevoProyecto.setId(999);
        nuevoProyecto.setTitulo("Nuevo Título");
        nuevoProyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        nuevoProyecto.setFechaCreacion(fecha);
        nuevoProyecto.setDirectorId(500);
        nuevoProyecto.setCodirectorId(600);
        nuevoProyecto.setObjetivoGeneral("Objetivo General");
        nuevoProyecto.setObjetivosEspecificos("Objetivos Específicos");
        nuevoProyecto.setEstudiante1Id(700);
        nuevoProyecto.setEstudiante2Id(800);
        nuevoProyecto.setEstado(enumEstadoProyecto.APROBADO);
        nuevoProyecto.setNumeroIntentos(3);
        
        // Verificar todos los getters
        assertEquals(999, nuevoProyecto.getId());
        assertEquals("Nuevo Título", nuevoProyecto.getTitulo());
        assertEquals(enumModalidad.PRACTICA_PROFESIONAL, nuevoProyecto.getModalidad());
        assertEquals(fecha, nuevoProyecto.getFechaCreacion());
        assertEquals(500, nuevoProyecto.getDirectorId());
        assertEquals(600, nuevoProyecto.getCodirectorId());
        assertEquals("Objetivo General", nuevoProyecto.getObjetivoGeneral());
        assertEquals("Objetivos Específicos", nuevoProyecto.getObjetivosEspecificos());
        assertEquals(700, nuevoProyecto.getEstudiante1Id());
        assertEquals(800, nuevoProyecto.getEstudiante2Id());
        assertEquals(enumEstadoProyecto.APROBADO, nuevoProyecto.getEstado());
        assertEquals(3, nuevoProyecto.getNumeroIntentos());
    }

    // ==================== PRUEBAS DE INTEGRACIÓN DE MÉTODOS ====================

    @Test
    void flujoCompleto_rechazarYIncrementar_mantienLogicaConsistente() {
        // Estado inicial: EN_PROCESO, 1 intento
        proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);
        proyecto.setNumeroIntentos(1);
        
        // No puede subir nueva versión porque está EN_PROCESO
        assertFalse(proyecto.puedeSubirNuevaVersion());
        
        // Cambiar a RECHAZADO (simular rechazo)
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        
        // Ahora sí puede subir nueva versión
        assertTrue(proyecto.puedeSubirNuevaVersion());
        
        // Incrementar intentos (simular nueva subida)
        proyecto.incrementarIntentos();
        assertEquals(2, proyecto.getNumeroIntentos());
        
        // Aún puede subir otra versión
        assertTrue(proyecto.puedeSubirNuevaVersion());
        
        // Incrementar a 3 intentos
        proyecto.incrementarIntentos();
        assertEquals(3, proyecto.getNumeroIntentos());
        
        // Ya no puede subir más versiones
        assertFalse(proyecto.puedeSubirNuevaVersion());
        
        // Marcar como rechazado definitivo
        proyecto.marcarComoRechazadoDefinitivo();
        assertEquals(enumEstadoProyecto.RECHAZADO_DEFINITIVO, proyecto.getEstado());
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void flujoAprobacion_primerIntento() {
        // Estado inicial
        proyecto.setEstado(enumEstadoProyecto.EN_PROCESO);
        proyecto.setNumeroIntentos(1);
        
        assertFalse(proyecto.puedeSubirNuevaVersion());
        
        // Aprobar proyecto
        proyecto.setEstado(enumEstadoProyecto.APROBADO);
        
        // No puede subir más versiones porque está aprobado
        assertFalse(proyecto.puedeSubirNuevaVersion());
        assertEquals(1, proyecto.getNumeroIntentos());
    }

    @Test
    void edgeCases_numerosNegativos() {
        proyecto.setNumeroIntentos(-1);
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        
        // Con número negativo, debería poder subir (< 3)
        assertTrue(proyecto.puedeSubirNuevaVersion());
        
        proyecto.incrementarIntentos();
        assertEquals(0, proyecto.getNumeroIntentos());
    }

    // ==================== PRUEBAS PARA CASOS LÍMITE ====================

    @Test
    void puedeSubirNuevaVersion_limiteExacto3Intentos() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(3);
        
        // Exactamente 3 intentos = no puede subir más
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }

    @Test
    void puedeSubirNuevaVersion_unIntentoAntesDellimite() {
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(2);
        
        // 2 intentos = puede subir uno más
        assertTrue(proyecto.puedeSubirNuevaVersion());
    }
}