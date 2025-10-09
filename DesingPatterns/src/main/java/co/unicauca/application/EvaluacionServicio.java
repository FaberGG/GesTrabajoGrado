
package co.unicauca.application;
import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.services.ProyectoGradoEvaluator;
import co.unicauca.domain.services.ProfessionalPracticeEvaluator;
import co.unicauca.domain.services.ResearchProjectEvaluator;

/**
 * Maneja la lógica de evaluación de proyectos
 */
public class EvaluacionServicio {
    
    /**
     * Método para evaluar un proyecto con una estrategia específica
     * @param proyecto el proyecto a evaluar
     * @param estrategia "PRACTICA_PROFESIONAL" o "INVESTIGACION"
     */
    public void evaluarProyecto(ProyectoGrado proyecto, String estrategia) {
        ProyectoGradoEvaluator evaluador;
        
        if ("PRACTICA_PROFESIONAL".equalsIgnoreCase(estrategia)) {
            evaluador = new ProfessionalPracticeEvaluator();
        } else if ("INVESTIGACION".equalsIgnoreCase(estrategia) || "BCA".equalsIgnoreCase(estrategia)) {
            evaluador = new ResearchProjectEvaluator();
        } else {
            throw new IllegalArgumentException("Estrategia desconocida: " + estrategia);
        }
        
        // Se ejecuta el template method
        evaluador.evaluarProyecto(proyecto);
    }
}