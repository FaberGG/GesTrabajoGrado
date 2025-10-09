/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.domain.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ProyectoGradoTest {
    
    private ProyectoGrado proyecto;
    
    @BeforeEach
    void setUp() {
        proyecto = new ProyectoGrado();
        proyecto.setId(1);
        proyecto.setTitulo("Sistema de IA");
        proyecto.setEstudiante1Id(101);
        proyecto.setObjetivoGeneral("Desarrollar sistema inteligente");
        proyecto.setObjetivosEspecificos("Implementar ML; Validar datos");
    }
    
    @Test
    void testCrearProyectoConDatosBasicos() {
        // Assert
        assertNotNull(proyecto);
        assertEquals(1, proyecto.getId());
        assertEquals("Sistema de IA", proyecto.getTitulo());
        assertEquals(101, proyecto.getEstudiante1Id());
    }
    
    @Test
    void testEstadoInicialEsEnProceso() {
        // Assert
        assertEquals(enumEstadoProyecto.EN_PROCESO, proyecto.getEstado());
    }
    
    @Test
    void testNumeroIntentosInicialEs1() {
        // Assert
        assertEquals(1, proyecto.getNumeroIntentos());
    }
    
    @Test
    void testAsignarCriteriosEvaluacion() {
        // Arrange
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias", 28);
        
        // Act
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Assert
        assertNotNull(proyecto.getCriteriosEvaluacion());
        assertEquals(28, proyecto.getCriteriosEvaluacion().obtenerPuntuacion("competencias"));
    }
    
    @Test
    void testPuedeSubirNuevaVersionCuandoEstaRechazado() {
        // Arrange
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(2);
        
        // Act & Assert
        assertTrue(proyecto.puedeSubirNuevaVersion());
    }
    
    @Test
    void testNoPuedeSubirNuevaVersionDespuesDe3Intentos() {
        // Arrange
        proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
        proyecto.setNumeroIntentos(3);
        
        // Act & Assert
        assertFalse(proyecto.puedeSubirNuevaVersion());
    }
}