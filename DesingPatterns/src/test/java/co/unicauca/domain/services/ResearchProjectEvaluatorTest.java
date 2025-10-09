/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.domain.services;

import co.unicauca.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class ResearchProjectEvaluatorTest {
    
    private ResearchProjectEvaluator evaluador;
    private ProyectoGrado proyecto;
    
    @BeforeEach
    void setUp() {
        evaluador = new ResearchProjectEvaluator();
        proyecto = crearProyectoInvestigacion();
    }
    
    private ProyectoGrado crearProyectoInvestigacion() {
        ProyectoGrado p = new ProyectoGrado();
        p.setId(1);
        p.setTitulo("Análisis de sentimientos con Deep Learning");
        p.setEstudiante1Id(101);
        p.setObjetivoGeneral("Desarrollar modelo de análisis");
        p.setObjetivosEspecificos("Implementar transformer; Validar");
        p.setModalidad(enumModalidad.INVESTIGACION);
        return p;
    }
    
    @Test
    void testEvaluarInvestigacionAprobada() {
        // Arrange
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("originalidad", 24);
        criterios.agregarCriterio("rigor_cientifico", 28);
        criterios.agregarCriterio("metodologia", 19);
        criterios.agregarCriterio("documentacion_academica", 14);
        criterios.agregarCriterio("contribucion", 9);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Act
        evaluador.evaluarProyecto(proyecto);
        
        // Assert
        assertEquals(94, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
    }
    
    @Test
    void testEvaluarInvestigacionRechazada() {
        // Arrange
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("originalidad", 15);
        criterios.agregarCriterio("rigor_cientifico", 18);
        criterios.agregarCriterio("metodologia", 12);
        criterios.agregarCriterio("documentacion_academica", 10);
        criterios.agregarCriterio("contribucion", 5);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // Act
        evaluador.evaluarProyecto(proyecto);
        
        // Assert
        assertEquals(60, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado());
    }
}