package co.unicauca;

import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.CriteriosEvaluacion;
import co.unicauca.domain.entities.enumModalidad;
import co.unicauca.application.EvaluacionServicio;

public class Main {
    public static void main(String[] args) {
        System.out.println("========== EVALUACION DE PROYECTOS DE GRADO ==========\n");
        
        EvaluacionServicio evaluacionServicio = new EvaluacionServicio();
        
        // ========================================
        // PROYECTO 1: Excelente calificación
        // ========================================
        ProyectoGrado proyecto1 = new ProyectoGrado();
        proyecto1.setId(1);
        proyecto1.setTitulo("Sistema de prediccion de demanda con IA");
        proyecto1.setEstudiante1Id(101);
        proyecto1.setObjetivoGeneral("Desarrollar un sistema inteligente para prediccion de demanda");
        proyecto1.setObjetivosEspecificos("Implementar modelos de ML; Validar con datos reales");
        proyecto1.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        
        // 🆕 Definir criterios (EXCELENTE)
        CriteriosEvaluacion criterios1 = new CriteriosEvaluacion();
        criterios1.agregarCriterio("competencias_laborales", 28);
        criterios1.agregarCriterio("impacto_organizacion", 22);
        criterios1.agregarCriterio("calidad_documentacion", 19);
        criterios1.agregarCriterio("presentacion_profesional", 23);
        proyecto1.setCriteriosEvaluacion(criterios1);
        
        System.out.println("   PROYECTO 1: CALIFICACION ALTA\n");
        evaluacionServicio.evaluarProyecto(proyecto1, "PRACTICA_PROFESIONAL");
        
        System.out.println("\n\n");
        
        // ========================================
        // PROYECTO 2: Calificación deficiente
        // ========================================
        ProyectoGrado proyecto2 = new ProyectoGrado();
        proyecto2.setId(2);
        proyecto2.setTitulo("Sistema basico de inventario");
        proyecto2.setEstudiante1Id(102);
        proyecto2.setObjetivoGeneral("Crear un sistema de inventario simple");
        proyecto2.setObjetivosEspecificos("Registrar productos; Generar reportes");
        proyecto2.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        
        // 🆕 Definir criterios (DEFICIENTE)
        CriteriosEvaluacion criterios2 = new CriteriosEvaluacion();
        criterios2.agregarCriterio("competencias_laborales", 15);  // Bajo
        criterios2.agregarCriterio("impacto_organizacion", 12);    // Bajo
        criterios2.agregarCriterio("calidad_documentacion", 10);   // Bajo
        criterios2.agregarCriterio("presentacion_profesional", 14); // Bajo
        proyecto2.setCriteriosEvaluacion(criterios2);
        
        System.out.println("   PROYECTO 2: CALIFICACION BAJA\n");
        evaluacionServicio.evaluarProyecto(proyecto2, "PRACTICA_PROFESIONAL");
        
        System.out.println("\n\n");
        
        // ========================================
        // PROYECTO 3: Investigación excelente
        // ========================================
        ProyectoGrado proyecto3 = new ProyectoGrado();
        proyecto3.setId(3);
        proyecto3.setTitulo("Analisis de sentimientos en redes sociales usando Deep Learning");
        proyecto3.setEstudiante1Id(103);
        proyecto3.setObjetivoGeneral("Desarrollar un modelo de analisis de sentimientos");
        proyecto3.setObjetivosEspecificos("Implementar arquitectura transformer; Validar con datasets");
        proyecto3.setModalidad(enumModalidad.INVESTIGACION);
        
        // 🆕 Definir criterios de INVESTIGACIÓN (EXCELENTE)
        CriteriosEvaluacion criterios3 = new CriteriosEvaluacion();
        criterios3.agregarCriterio("originalidad", 24);
        criterios3.agregarCriterio("rigor_cientifico", 28);
        criterios3.agregarCriterio("metodologia", 19);
        criterios3.agregarCriterio("documentacion_academica", 14);
        criterios3.agregarCriterio("contribucion", 9);
        proyecto3.setCriteriosEvaluacion(criterios3);
        
        System.out.println("   PROYECTO 3: INVESTIGACION DE CALIDAD\n");
        evaluacionServicio.evaluarProyecto(proyecto3, "INVESTIGACION");
    }
}