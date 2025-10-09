/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.domain.services;

import co.unicauca.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ProfessionalPracticeEvaluatorTest {
    
    private ProfessionalPracticeEvaluator evaluador;
    private ProyectoGrado proyecto;
    
    @BeforeEach
    void setUp() {
        evaluador = new ProfessionalPracticeEvaluator();
        proyecto = crearProyectoBase();
    }
    
    private ProyectoGrado crearProyectoBase() {
        ProyectoGrado p = new ProyectoGrado();
        p.setId(1);
        p.setTitulo("Sistema de IA");
        p.setEstudiante1Id(101);
        p.setObjetivoGeneral("Desarrollar sistema");
        p.setObjetivosEspecificos("Implementar ML");
        p.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        return p;
    }
    
    @Test
    void testEvaluarProyectoAprobado() {
        // Arrange - Calificaciones altas
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 28);
        criterios.agregarCriterio("impacto_organizacion", 22);
        criterios.agregarCriterio("calidad_documentacion", 19);
        criterios.agregarCriterio("presentacion_profesional", 23);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Act
        evaluador.evaluarProyecto(proyecto);
        
        // Assert
        assertEquals(92, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
        assertNotNull(proyecto.getFechaEvaluacion());
    }
    
    @Test
    void testEvaluarProyectoRechazado() {
        // Arrange - Calificaciones bajas
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 15);
        criterios.agregarCriterio("impacto_organizacion", 12);
        criterios.agregarCriterio("calidad_documentacion", 10);
        criterios.agregarCriterio("presentacion_profesional", 14);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Act
        evaluador.evaluarProyecto(proyecto);
        
        // Assert
        assertEquals(51, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado());
    }
    
    @Test
    void testEvaluarProyectoEnElLimite() {
        // Arrange - Exactamente 70 puntos (límite de aprobación)
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 20);
        criterios.agregarCriterio("impacto_organizacion", 20);
        criterios.agregarCriterio("calidad_documentacion", 15);
        criterios.agregarCriterio("presentacion_profesional", 15);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Act
        evaluador.evaluarProyecto(proyecto);
        
        // Assert
        assertEquals(70, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
    }
    
    @Test
    void testEvaluarProyectoSinCriterios() {
        // Arrange - No asignar criterios
        
        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            evaluador.evaluarProyecto(proyecto);
        });
    }
}