/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.application;

import co.unicauca.domain.entities.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class EvaluacionServicioTest {
    
    private EvaluacionServicio servicio;
    private ProyectoGrado proyecto;
    
    @BeforeEach
    void setUp() {
        servicio = new EvaluacionServicio();
        proyecto = crearProyectoConCriterios();
    }
    
    private ProyectoGrado crearProyectoConCriterios() {
        ProyectoGrado p = new ProyectoGrado();
        p.setId(1);
        p.setTitulo("Sistema de IA");
        p.setEstudiante1Id(101);
        p.setObjetivoGeneral("Desarrollar sistema");
        p.setObjetivosEspecificos("Implementar ML");
        p.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 28);
        criterios.agregarCriterio("impacto_organizacion", 22);
        criterios.agregarCriterio("calidad_documentacion", 19);
        criterios.agregarCriterio("presentacion_profesional", 23);
        p.setCriteriosEvaluacion(criterios);
        
        return p;
    }
    
    @Test
    void testEvaluarConEstrategiaPracticaProfesional() {
        // Act
        servicio.evaluarProyecto(proyecto, "PRACTICA_PROFESIONAL");
        
        // Assert
        assertEquals(92, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
    }
    
    @Test
    void testEvaluarConEstrategiaDesconocida() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            servicio.evaluarProyecto(proyecto, "ESTRATEGIA_INEXISTENTE");
        });
    }
    
    @Test
    void testEstrategiasCaseInsensitive() {
        // Act - probar minúsculas
        servicio.evaluarProyecto(proyecto, "practica_profesional");
        
        // Assert
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado());
    }
}