package co.unicauca.domain.services;

import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.CriteriosEvaluacion;
import co.unicauca.domain.entities.CriteriosEvaluacion;
import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.enumEstadoProyecto;
import co.unicauca.domain.entities.enumEstadoProyecto;

/**
 * CLASE CONCRETA: ResearchProjectEvaluator (BCA)
 * Extiende: ProyectoGradoEvaluator
 * Estrategia: RESEARCH PROJECT - BCA (Basado en Criterios Académicos)
 */
public class ResearchProjectEvaluator extends ProyectoGradoEvaluator {
    
    @Override
    protected void evaluarRubrica(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 2] Evaluando rubrica (Research Project - BCA)...");
        System.out.println("    Criterios de evaluacion:");
        System.out.println("      Originalidad y novedad: 25 puntos");
        System.out.println("      Rigor cientifico: 30 puntos");
        System.out.println("      Metodologia adecuada: 20 puntos");
        System.out.println("      Documentacion academica: 15 puntos");
        System.out.println("      Contribucion al conocimiento: 10 puntos");
        System.out.println("    .....................................");
        System.out.println("    Total: 100 puntos");
    }
    
    @Override
    protected void calcularPuntuacion(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 3] Calculando puntuacion (Research Project - BCA)...");
        
        // 🆕 Obtener criterios del proyecto (ahora dinámicos)
        CriteriosEvaluacion criterios = proyecto.getCriteriosEvaluacion();
        
        if (criterios == null) {
            throw new IllegalStateException("No se han definido criterios de evaluacion para este proyecto");
        }
        
        int originalidad = criterios.obtenerPuntuacion("originalidad");
        int rigor = criterios.obtenerPuntuacion("rigor_cientifico");
        int metodologia = criterios.obtenerPuntuacion("metodologia");
        int documentacion = criterios.obtenerPuntuacion("documentacion_academica");
        int contribucion = criterios.obtenerPuntuacion("contribucion");
        
        int total = originalidad + rigor + metodologia + documentacion + contribucion;
        proyecto.setPuntuacionTotal(total);
        
        System.out.println("  Desglose:");
        System.out.println("      Originalidad: " + originalidad + " / 25");
        System.out.println("      Rigor cientifico: " + rigor + " / 30");
        System.out.println("      Metodologia: " + metodologia + " / 20");
        System.out.println("      Documentacion academica: " + documentacion + " / 15");
        System.out.println("      Contribucion: " + contribucion + " / 10");
        System.out.println("    .....................................");
        System.out.println("    TOTAL: " + total + " puntos");
    }
    
    @Override
    protected void generarVeredicto(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 4] Generando veredicto (Research Project - BCA)...");
        int total = proyecto.getPuntuacionTotal();
        
        if (total >= 75) {
            proyecto.setEstado(enumEstadoProyecto.APROBADO);
            System.out.println("\n    RESULTADO: APROBADO");
            System.out.println("  Puntuacion: " + total + "/100");
            System.out.println("  Justificacion: Investigacion cumple con estandares academicos.");
            System.out.println("  Rigor cientifico y metodologia validados correctamente.");
        } else {
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            System.out.println("\n    RESULTADO: NO APROBADO");
            System.out.println("  Puntuacion: " + total + "/100");
            System.out.println("  Justificacion: No cumple con estandares minimos de investigacion.");
            System.out.println("  Se solicita mejorar rigor cientifico, metodologia o documentacion.");
        }
    }
}