/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit5TestClass.java to edit this template
 */
package co.unicauca.integration;

import co.unicauca.domain.entities.*;
import co.unicauca.application.EvaluacionServicio;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Pruebas de Integración - Flujo Completo de Evaluación")
class EvaluacionIntegrationTest {
    
    private EvaluacionServicio servicio;
    
    @BeforeEach
    void setUp() {
        servicio = new EvaluacionServicio();
    }
    
    @Test
    @DisplayName("Flujo completo: Proyecto de práctica profesional aprobado")
    void testFlujoCompletoProyectoAprobado() {
        // ========== PASO 1: CREAR PROYECTO ==========
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(1);
        proyecto.setTitulo("Sistema de IA Predictiva para Inventarios");
        proyecto.setEstudiante1Id(101);
        proyecto.setEstudiante2Id(102);
        proyecto.setObjetivoGeneral("Desarrollar un sistema inteligente de predicción de inventarios");
        proyecto.setObjetivosEspecificos("Implementar algoritmos de ML; Validar con datos reales; Integrar con ERP");
        proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        
        // ========== PASO 2: ASIGNAR CALIFICACIONES ==========
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 28);
        criterios.agregarCriterio("impacto_organizacion", 22);
        criterios.agregarCriterio("calidad_documentacion", 19);
        criterios.agregarCriterio("presentacion_profesional", 23);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // ========== PASO 3: EVALUAR ==========
        servicio.evaluarProyecto(proyecto, "PRACTICA_PROFESIONAL");
        
        // ========== PASO 4: VERIFICAR TODO EL RESULTADO ==========
        assertAll("Verificación completa del proyecto aprobado",
            () -> assertEquals(92, proyecto.getPuntuacionTotal(), "Puntuación total debe ser 92"),
            () -> assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado(), "Estado debe ser APROBADO"),
            () -> assertNotNull(proyecto.getFechaEvaluacion(), "Debe tener fecha de evaluación"),
            () -> assertEquals(1, proyecto.getNumeroIntentos(), "Debe estar en el primer intento"),
            () -> assertEquals("Sistema de IA Predictiva para Inventarios", proyecto.getTitulo()),
            () -> assertEquals(101, proyecto.getEstudiante1Id())
        );
    }
    
    @Test
    @DisplayName("Flujo completo: Proyecto de investigación aprobado")
    void testFlujoCompletoInvestigacionAprobada() {
        // ========== CREAR PROYECTO DE INVESTIGACIÓN ==========
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(2);
        proyecto.setTitulo("Análisis de Sentimientos en Redes Sociales usando Transformers");
        proyecto.setEstudiante1Id(201);
        proyecto.setObjetivoGeneral("Desarrollar un modelo de análisis de sentimientos basado en BERT");
        proyecto.setObjetivosEspecificos("Implementar arquitectura transformer; Entrenar con datasets en español; Validar resultados");
        proyecto.setModalidad(enumModalidad.INVESTIGACION);
        
        // ========== ASIGNAR CALIFICACIONES DE INVESTIGACIÓN ==========
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("originalidad", 24);
        criterios.agregarCriterio("rigor_cientifico", 28);
        criterios.agregarCriterio("metodologia", 19);
        criterios.agregarCriterio("documentacion_academica", 14);
        criterios.agregarCriterio("contribucion", 9);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // ========== EVALUAR ==========
        servicio.evaluarProyecto(proyecto, "INVESTIGACION");
        
        // ========== VERIFICAR ==========
        assertAll("Verificación completa de investigación aprobada",
            () -> assertEquals(94, proyecto.getPuntuacionTotal()),
            () -> assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado()),
            () -> assertNotNull(proyecto.getFechaEvaluacion()),
            () -> assertEquals(enumModalidad.INVESTIGACION, proyecto.getModalidad())
        );
    }
    
    @Test
    @DisplayName("Flujo completo: Proyecto rechazado con posibilidad de reenvío")
    void testFlujoCompletoProyectoRechazadoConReenvio() {
        // ========== CREAR PROYECTO CON CALIFICACIONES BAJAS ==========
        ProyectoGrado proyecto = new ProyectoGrado();
        proyecto.setId(3);
        proyecto.setTitulo("Sistema básico de gestión");
        proyecto.setEstudiante1Id(301);
        proyecto.setObjetivoGeneral("Crear un sistema de gestión simple");
        proyecto.setObjetivosEspecificos("Registrar datos; Mostrar reportes");
        proyecto.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        
        // Calificaciones bajas
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 15);
        criterios.agregarCriterio("impacto_organizacion", 12);
        criterios.agregarCriterio("calidad_documentacion", 10);
        criterios.agregarCriterio("presentacion_profesional", 14);
        proyecto.setCriteriosEvaluacion(criterios);
        
        // ========== EVALUAR ==========
        servicio.evaluarProyecto(proyecto, "PRACTICA_PROFESIONAL");
        
        // ========== VERIFICAR RECHAZO ==========
        assertEquals(51, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado());
        
        // ========== VERIFICAR QUE PUEDE REENVIAR ==========
        assertTrue(proyecto.puedeSubirNuevaVersion(), 
            "Debe poder subir nueva versión después del primer intento");
        
        // ========== SIMULAR SEGUNDO INTENTO ==========
        proyecto.incrementarIntentos();
        assertEquals(2, proyecto.getNumeroIntentos());
        assertTrue(proyecto.puedeSubirNuevaVersion(), 
            "Aún puede subir en el segundo intento");
        
        // ========== SIMULAR TERCER INTENTO ==========
        proyecto.incrementarIntentos();
        assertEquals(3, proyecto.getNumeroIntentos());
        assertFalse(proyecto.puedeSubirNuevaVersion(), 
            "Ya NO puede subir después del tercer intento");
    }
    
    @Test
    @DisplayName("Evaluar múltiples proyectos diferentes en secuencia")
    void testEvaluarMultiplesProyectosDiferentes() {
        // ========== PROYECTO 1: Práctica Profesional APROBADA ==========
        ProyectoGrado p1 = crearProyecto(1, "Proyecto A - Sistema ERP", 101, enumModalidad.PRACTICA_PROFESIONAL);
        asignarCriteriosPractica(p1, 28, 22, 19, 23);
        servicio.evaluarProyecto(p1, "PRACTICA_PROFESIONAL");
        
        // ========== PROYECTO 2: Práctica Profesional RECHAZADA ==========
        ProyectoGrado p2 = crearProyecto(2, "Proyecto B - App básica", 102, enumModalidad.PRACTICA_PROFESIONAL);
        asignarCriteriosPractica(p2, 15, 12, 10, 14);
        servicio.evaluarProyecto(p2, "PRACTICA_PROFESIONAL");
        
        // ========== PROYECTO 3: Investigación APROBADA ==========
        ProyectoGrado p3 = crearProyecto(3, "Proyecto C - Deep Learning", 103, enumModalidad.INVESTIGACION);
        asignarCriteriosInvestigacion(p3, 24, 28, 19, 14, 9);
        servicio.evaluarProyecto(p3, "INVESTIGACION");
        
        // ========== PROYECTO 4: Investigación RECHAZADA ==========
        ProyectoGrado p4 = crearProyecto(4, "Proyecto D - Investigación básica", 104, enumModalidad.INVESTIGACION);
        asignarCriteriosInvestigacion(p4, 15, 18, 12, 10, 5);
        servicio.evaluarProyecto(p4, "INVESTIGACION");
        
        // ========== VERIFICAR TODOS LOS RESULTADOS ==========
        assertAll("Verificar estado de 4 proyectos diferentes",
            () -> assertEquals(enumEstadoProyecto.APROBADO, p1.getEstado(), "Proyecto 1 debe estar aprobado"),
            () -> assertEquals(92, p1.getPuntuacionTotal()),
            
            () -> assertEquals(enumEstadoProyecto.RECHAZADO, p2.getEstado(), "Proyecto 2 debe estar rechazado"),
            () -> assertEquals(51, p2.getPuntuacionTotal()),
            
            () -> assertEquals(enumEstadoProyecto.APROBADO, p3.getEstado(), "Proyecto 3 debe estar aprobado"),
            () -> assertEquals(94, p3.getPuntuacionTotal()),
            
            () -> assertEquals(enumEstadoProyecto.RECHAZADO, p4.getEstado(), "Proyecto 4 debe estar rechazado"),
            () -> assertEquals(60, p4.getPuntuacionTotal())
        );
    }
    
    @Test
    @DisplayName("Proyecto en el límite exacto de aprobación")
    void testProyectoEnElLimiteDeAprobacion() {
        // Proyecto con EXACTAMENTE 70 puntos (límite de práctica profesional)
        ProyectoGrado proyecto = crearProyecto(5, "Proyecto Límite", 105, enumModalidad.PRACTICA_PROFESIONAL);
        
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("competencias_laborales", 20);
        criterios.agregarCriterio("impacto_organizacion", 20);
        criterios.agregarCriterio("calidad_documentacion", 15);
        criterios.agregarCriterio("presentacion_profesional", 15);
        proyecto.setCriteriosEvaluacion(criterios);
        
        servicio.evaluarProyecto(proyecto, "PRACTICA_PROFESIONAL");
        
        assertEquals(70, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.APROBADO, proyecto.getEstado(), 
            "Con 70 puntos debe aprobar (límite inclusive)");
    }
    
    @Test
    @DisplayName("Investigación requiere mayor puntaje que práctica profesional")
    void testInvestigacionRequiereMayorPuntaje() {
        // Proyecto con 72 puntos
        // - Aprobaría en PRÁCTICA (límite: 70)
        // - NO aprobaría en INVESTIGACIÓN (límite: 75)
        
        ProyectoGrado proyecto = crearProyecto(6, "Proyecto 72 puntos", 106, enumModalidad.INVESTIGACION);
        
        CriteriosEvaluacion criterios = new CriteriosEvaluacion();
        criterios.agregarCriterio("originalidad", 18);
        criterios.agregarCriterio("rigor_cientifico", 22);
        criterios.agregarCriterio("metodologia", 15);
        criterios.agregarCriterio("documentacion_academica", 12);
        criterios.agregarCriterio("contribucion", 5);
        proyecto.setCriteriosEvaluacion(criterios);
        
        servicio.evaluarProyecto(proyecto, "INVESTIGACION");
        
        assertEquals(72, proyecto.getPuntuacionTotal());
        assertEquals(enumEstadoProyecto.RECHAZADO, proyecto.getEstado(),
            "Con 72 puntos NO aprueba investigación (requiere 75)");
    }
    
    @Test
    @DisplayName("Validar que diferentes estrategias usan diferentes evaluadores")
    void testDiferentesEstrategiasUsanDiferentesEvaluadores() {
        // Mismo proyecto, evaluado con dos estrategias diferentes
        ProyectoGrado proyecto = crearProyecto(7, "Proyecto Dual", 107, enumModalidad.PRACTICA_PROFESIONAL);
        
        // Calificaciones para PRÁCTICA PROFESIONAL
        CriteriosEvaluacion criteriosPractica = new CriteriosEvaluacion();
        criteriosPractica.agregarCriterio("competencias_laborales", 28);
        criteriosPractica.agregarCriterio("impacto_organizacion", 22);
        criteriosPractica.agregarCriterio("calidad_documentacion", 19);
        criteriosPractica.agregarCriterio("presentacion_profesional", 23);
        proyecto.setCriteriosEvaluacion(criteriosPractica);
        
        // Evaluar como PRÁCTICA
        servicio.evaluarProyecto(proyecto, "PRACTICA_PROFESIONAL");
        int puntuacionPractica = proyecto.getPuntuacionTotal();
        enumEstadoProyecto estadoPractica = proyecto.getEstado();
        
        // Ahora evaluar el MISMO proyecto como INVESTIGACIÓN
        proyecto.setModalidad(enumModalidad.INVESTIGACION);
        CriteriosEvaluacion criteriosInvestigacion = new CriteriosEvaluacion();
        criteriosInvestigacion.agregarCriterio("originalidad", 24);
        criteriosInvestigacion.agregarCriterio("rigor_cientifico", 28);
        criteriosInvestigacion.agregarCriterio("metodologia", 19);
        criteriosInvestigacion.agregarCriterio("documentacion_academica", 14);
        criteriosInvestigacion.agregarCriterio("contribucion", 9);
        proyecto.setCriteriosEvaluacion(criteriosInvestigacion);
        
        servicio.evaluarProyecto(proyecto, "INVESTIGACION");
        int puntuacionInvestigacion = proyecto.getPuntuacionTotal();
        
        // Verificar que se aplicaron DIFERENTES criterios
        assertNotEquals(puntuacionPractica, puntuacionInvestigacion,
            "Las estrategias deben producir puntuaciones diferentes");
    }
    
    // ========== MÉTODOS AUXILIARES ==========
    
    private ProyectoGrado crearProyecto(int id, String titulo, int estudianteId, enumModalidad modalidad) {
        ProyectoGrado p = new ProyectoGrado();
        p.setId(id);
        p.setTitulo(titulo);
        p.setEstudiante1Id(estudianteId);
        p.setObjetivoGeneral("Objetivo general del proyecto " + id);
        p.setObjetivosEspecificos("Objetivos específicos del proyecto " + id);
        p.setModalidad(modalidad);
        return p;
    }
    
    private void asignarCriteriosPractica(ProyectoGrado p, int comp, int imp, int doc, int pres) {
        CriteriosEvaluacion c = new CriteriosEvaluacion();
        c.agregarCriterio("competencias_laborales", comp);
        c.agregarCriterio("impacto_organizacion", imp);
        c.agregarCriterio("calidad_documentacion", doc);
        c.agregarCriterio("presentacion_profesional", pres);
        p.setCriteriosEvaluacion(c);
    }
    
    private void asignarCriteriosInvestigacion(ProyectoGrado p, int orig, int rigor, int met, int doc, int cont) {
        CriteriosEvaluacion c = new CriteriosEvaluacion();
        c.agregarCriterio("originalidad", orig);
        c.agregarCriterio("rigor_cientifico", rigor);
        c.agregarCriterio("metodologia", met);
        c.agregarCriterio("documentacion_academica", doc);
        c.agregarCriterio("contribucion", cont);
        p.setCriteriosEvaluacion(c);
    }
}