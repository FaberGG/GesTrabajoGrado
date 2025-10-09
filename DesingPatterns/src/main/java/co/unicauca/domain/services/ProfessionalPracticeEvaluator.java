package co.unicauca.domain.services;

import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.CriteriosEvaluacion;
import co.unicauca.domain.entities.CriteriosEvaluacion;
import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.enumEstadoProyecto;
import co.unicauca.domain.entities.enumEstadoProyecto;

public class ProfessionalPracticeEvaluator extends ProyectoGradoEvaluator {
    
    @Override
    protected void evaluarRubrica(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 2] Evaluando rubrica (Professional Practice)...");
        System.out.println("    Criterios de evaluacion:");
        System.out.println("      Competencias laborales adquiridas: 30 puntos");
        System.out.println("      Impacto en la organizacion: 25 puntos");
        System.out.println("      Calidad de documentacion: 20 puntos");
        System.out.println("      Presentacion profesional: 25 puntos");
        System.out.println("    .....................................");
        System.out.println("    Total: 100 puntos");
    }
    
    @Override
    protected void calcularPuntuacion(ProyectoGrado proyecto) {
        System.out.println("\n [PASO 3] Calculando puntuacion (Professional Practice)...");
        
        // 🆕 Obtener criterios del proyecto (ahora dinámicos)
        CriteriosEvaluacion criterios = proyecto.getCriteriosEvaluacion();
        
        if (criterios == null) {
            throw new IllegalStateException("No se han definido criterios de evaluacion para este proyecto");
        }
        
        int competencias = criterios.obtenerPuntuacion("competencias_laborales");
        int impacto = criterios.obtenerPuntuacion("impacto_organizacion");
        int documentacion = criterios.obtenerPuntuacion("calidad_documentacion");
        int presentacion = criterios.obtenerPuntuacion("presentacion_profesional");
        
        int total = competencias + impacto + documentacion + presentacion;
        proyecto.setPuntuacionTotal(total);
        
        System.out.println("  Desglose:");
        System.out.println("     Competencias laborales: " + competencias + " / 30");
        System.out.println("     Impacto organizacional: " + impacto + " / 25");
        System.out.println("     Documentacion: " + documentacion + " / 20");
        System.out.println("     Presentacion: " + presentacion + " / 25");
        System.out.println("    .....................................");
        System.out.println("    TOTAL: " + total + " puntos");
    }
    
    @Override
    protected void generarVeredicto(ProyectoGrado proyecto) {
        System.out.println("\n [PASO 4] Generando veredicto (Professional Practice)...");
        int total = proyecto.getPuntuacionTotal();
        
        if (total >= 70) {
            proyecto.setEstado(enumEstadoProyecto.APROBADO);
            System.out.println("\n    RESULTADO: APROBADO");
            System.out.println("  Puntuacion: " + total + "/100");
            System.out.println("  Justificacion: Practica profesional cumple con los requisitos.");
            System.out.println("  Competencias laborales adquiridas satisfactoriamente.");
        } else {
            proyecto.setEstado(enumEstadoProyecto.RECHAZADO);
            System.out.println("\n    RESULTADO: NO APROBADO");
            System.out.println("  Puntuacion: " + total + "/100");
            System.out.println("  Justificacion: No cumple con competencias laborales requeridas.");
            System.out.println("  Se recomienda revisar con el director y presentar nuevamente.");
        }
    }
}