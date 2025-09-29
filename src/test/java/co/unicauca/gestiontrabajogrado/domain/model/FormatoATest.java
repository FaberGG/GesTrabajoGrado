package co.unicauca.gestiontrabajogrado.domain.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class FormatoATest {

    private FormatoA formato;

    @BeforeEach
    void setUp() {
        formato = new FormatoA();
        formato.setId(1);
        formato.setProyectoGradoId(100);
        formato.setNumeroIntento(1);
        formato.setRutaArchivo("/uploads/formato1.pdf");
        formato.setNombreArchivo("formato1.pdf");
        formato.setFechaCarga(LocalDateTime.now());
    }

    // ==================== PRUEBAS PARA aprobar() ====================

    @Test
    void aprobar_estadoInicialPendiente_cambiaAAprobado() {
        // Arrange: El formato ya está configurado en setUp con estado PENDIENTE
        assertEquals(enumEstadoFormato.PENDIENTE, formato.getEstado());
        
        // Act: Aprobar el formato
        formato.aprobar(50, "Formato correcto");
        
        // Assert: Verificar que cambió correctamente
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertEquals(50, formato.getEvaluadoPor());
        assertEquals("Formato correcto", formato.getObservaciones());
        assertNotNull(formato.getFechaEvaluacion());
    }

    @Test
    void aprobar_conObservacionesVacias_guardaObservacionesVacias() {
        formato.aprobar(25, "");
        
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertEquals("", formato.getObservaciones());
        assertEquals(25, formato.getEvaluadoPor());
    }

    @Test
    void aprobar_conObservacionesNull_guardaObservacionesNull() {
        formato.aprobar(30, null);
        
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertNull(formato.getObservaciones());
        assertEquals(30, formato.getEvaluadoPor());
    }

    @Test
    void aprobar_evaluadorIdNull_guardaEvaluadorNull() {
        formato.aprobar(null, "Aprobado sin evaluador");
        
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertNull(formato.getEvaluadoPor());
        assertEquals("Aprobado sin evaluador", formato.getObservaciones());
    }

    @Test
    void aprobar_fechaEvaluacionSeAsignaAutomaticamente() {
        LocalDateTime antesDeAprobar = LocalDateTime.now();
        
        formato.aprobar(40, "Aprobado");
        
        LocalDateTime despuesDeAprobar = LocalDateTime.now();
        
        assertTrue(formato.getFechaEvaluacion().isAfter(antesDeAprobar) || 
                  formato.getFechaEvaluacion().isEqual(antesDeAprobar));
        assertTrue(formato.getFechaEvaluacion().isBefore(despuesDeAprobar) || 
                  formato.getFechaEvaluacion().isEqual(despuesDeAprobar));
    }

    // ==================== PRUEBAS PARA rechazar() ====================

    @Test
    void rechazar_estadoInicialPendiente_cambiaARechazado() {
        // Arrange: El formato ya está configurado en setUp con estado PENDIENTE
        assertEquals(enumEstadoFormato.PENDIENTE, formato.getEstado());
        
        // Act: Rechazar el formato
        formato.rechazar(60, "Faltan datos requeridos");
        
        // Assert: Verificar que cambió correctamente
        assertEquals(enumEstadoFormato.RECHAZADO, formato.getEstado());
        assertEquals(60, formato.getEvaluadoPor());
        assertEquals("Faltan datos requeridos", formato.getObservaciones());
        assertNotNull(formato.getFechaEvaluacion());
    }

    @Test
    void rechazar_conObservacionesVacias_guardaObservacionesVacias() {
        formato.rechazar(35, "");
        
        assertEquals(enumEstadoFormato.RECHAZADO, formato.getEstado());
        assertEquals("", formato.getObservaciones());
        assertEquals(35, formato.getEvaluadoPor());
    }

    @Test
    void rechazar_evaluadorIdNull_guardaEvaluadorNull() {
        formato.rechazar(null, "Rechazado automáticamente");
        
        assertEquals(enumEstadoFormato.RECHAZADO, formato.getEstado());
        assertNull(formato.getEvaluadoPor());
        assertEquals("Rechazado automáticamente", formato.getObservaciones());
    }

    @Test
    void rechazar_fechaEvaluacionSeAsignaAutomaticamente() {
        LocalDateTime antesDeRechazar = LocalDateTime.now();
        
        formato.rechazar(45, "Rechazado");
        
        LocalDateTime despuesDeRechazar = LocalDateTime.now();
        
        assertTrue(formato.getFechaEvaluacion().isAfter(antesDeRechazar) || 
                  formato.getFechaEvaluacion().isEqual(antesDeRechazar));
        assertTrue(formato.getFechaEvaluacion().isBefore(despuesDeRechazar) || 
                  formato.getFechaEvaluacion().isEqual(despuesDeRechazar));
    }

    // ==================== PRUEBAS PARA esUltimoIntento() ====================

    @Test
    void esUltimoIntento_numeroIntentoNull_retornaFalse() {
        formato.setNumeroIntento(null);
        
        assertFalse(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoPrimero_retornaFalse() {
        formato.setNumeroIntento(1);
        
        assertFalse(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoSegundo_retornaFalse() {
        formato.setNumeroIntento(2);
        
        assertFalse(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoTercero_retornaTrue() {
        formato.setNumeroIntento(3);
        
        assertTrue(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoMayorATres_retornaTrue() {
        formato.setNumeroIntento(4);
        
        assertTrue(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoCero_retornaFalse() {
        formato.setNumeroIntento(0);
        
        assertFalse(formato.esUltimoIntento());
    }

    @Test
    void esUltimoIntento_numeroIntentoNegativo_retornaFalse() {
        formato.setNumeroIntento(-1);
        
        assertFalse(formato.esUltimoIntento());
    }

    // ==================== PRUEBAS PARA tieneCartaAceptacion() ====================

    @Test
    void tieneCartaAceptacion_rutaNull_retornaFalse() {
        formato.setRutaCartaAceptacion(null);
        
        assertFalse(formato.tieneCartaAceptacion());
    }

    @Test
    void tieneCartaAceptacion_rutaVacia_retornaFalse() {
        formato.setRutaCartaAceptacion("");
        
        assertFalse(formato.tieneCartaAceptacion());
    }

    @Test
    void tieneCartaAceptacion_rutaSoloEspacios_retornaFalse() {
        formato.setRutaCartaAceptacion("   ");
        
        assertFalse(formato.tieneCartaAceptacion());
    }

    @Test
    void tieneCartaAceptacion_rutaValida_retornaTrue() {
        formato.setRutaCartaAceptacion("/uploads/carta123.pdf");
        
        assertTrue(formato.tieneCartaAceptacion());
    }

    @Test
    void tieneCartaAceptacion_rutaConEspaciosYTexto_retornaTrue() {
        formato.setRutaCartaAceptacion("  /uploads/carta.pdf  ");
        
        // Según la implementación actual, esto debería retornar true
        // porque trim() elimina los espacios y queda contenido
        assertTrue(formato.tieneCartaAceptacion());
    }

    // ==================== PRUEBAS PARA GETTERS Y SETTERS ====================

    @Test
    void constructor_estadoInicialEsPendiente() {
        FormatoA nuevoFormato = new FormatoA();
        
        assertEquals(enumEstadoFormato.PENDIENTE, nuevoFormato.getEstado());
    }

    @Test
    void settersYGetters_funcionanCorrectamente() {
        FormatoA nuevoFormato = new FormatoA();
        LocalDateTime fechaCarga = LocalDateTime.now();
        LocalDateTime fechaEvaluacion = LocalDateTime.now().plusHours(1);
        
        // Probar todos los setters
        nuevoFormato.setId(999);
        nuevoFormato.setProyectoGradoId(888);
        nuevoFormato.setNumeroIntento(2);
        nuevoFormato.setRutaArchivo("/test/ruta.pdf");
        nuevoFormato.setNombreArchivo("test.pdf");
        nuevoFormato.setRutaCartaAceptacion("/test/carta.pdf");
        nuevoFormato.setNombreCartaAceptacion("carta.pdf");
        nuevoFormato.setFechaCarga(fechaCarga);
        nuevoFormato.setEstado(enumEstadoFormato.APROBADO);
        nuevoFormato.setObservaciones("Test observaciones");
        nuevoFormato.setEvaluadoPor(777);
        nuevoFormato.setFechaEvaluacion(fechaEvaluacion);
        
        // Verificar todos los getters
        assertEquals(999, nuevoFormato.getId());
        assertEquals(888, nuevoFormato.getProyectoGradoId());
        assertEquals(2, nuevoFormato.getNumeroIntento());
        assertEquals("/test/ruta.pdf", nuevoFormato.getRutaArchivo());
        assertEquals("test.pdf", nuevoFormato.getNombreArchivo());
        assertEquals("/test/carta.pdf", nuevoFormato.getRutaCartaAceptacion());
        assertEquals("carta.pdf", nuevoFormato.getNombreCartaAceptacion());
        assertEquals(fechaCarga, nuevoFormato.getFechaCarga());
        assertEquals(enumEstadoFormato.APROBADO, nuevoFormato.getEstado());
        assertEquals("Test observaciones", nuevoFormato.getObservaciones());
        assertEquals(777, nuevoFormato.getEvaluadoPor());
        assertEquals(fechaEvaluacion, nuevoFormato.getFechaEvaluacion());
    }

    // ==================== PRUEBAS DE INTEGRACIÓN ====================

    @Test
    void flujoCompleto_aprobarDespuesDeRechazar_mantieneDatosCorrectos() {
        // Primer rechazo
        formato.rechazar(100, "Primera revisión: faltan datos");
        assertEquals(enumEstadoFormato.RECHAZADO, formato.getEstado());
        assertEquals(100, formato.getEvaluadoPor());
        
        // Posterior aprobación (simulando nueva versión)
        formato.aprobar(200, "Segunda revisión: todo correcto");
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertEquals(200, formato.getEvaluadoPor());
        assertEquals("Segunda revisión: todo correcto", formato.getObservaciones());
    }

    @Test
    void flujoCompleto_ultimoIntentoConCarta_todosLosMetodos() {
        // Configurar como último intento con carta
        formato.setNumeroIntento(3);
        formato.setRutaCartaAceptacion("/uploads/carta_final.pdf");
        
        // Verificar estado inicial
        assertTrue(formato.esUltimoIntento());
        assertTrue(formato.tieneCartaAceptacion());
        
        // Aprobar
        formato.aprobar(300, "Último intento aprobado");
        
        // Verificar estado final
        assertEquals(enumEstadoFormato.APROBADO, formato.getEstado());
        assertEquals(300, formato.getEvaluadoPor());
        assertTrue(formato.esUltimoIntento());
        assertTrue(formato.tieneCartaAceptacion());
        assertNotNull(formato.getFechaEvaluacion());
    }
}