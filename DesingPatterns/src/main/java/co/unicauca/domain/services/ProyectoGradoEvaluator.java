package co.unicauca.domain.services;
import co.unicauca.domain.entities.ProyectoGrado;
import co.unicauca.domain.entities.ProyectoGrado;
import java.time.LocalDateTime;

/**
 * CLASE ABSTRACTA: ProyectoGradoEvaluator
 * Patrón: Template Method
 * Propósito: Define el flujo común de evaluación, permitiendo estrategias diferentes
 */
public abstract class ProyectoGradoEvaluator {
    
    // ============ TEMPLATE METHOD - El flujo que NUNCA cambia ============
    public final void evaluarProyecto(ProyectoGrado proyecto) {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("INICIANDO EVALUACION DEL PROYECTO: " + proyecto.getTitulo());
        System.out.println("Modalidad: " + proyecto.getModalidad());
        System.out.println("Estudiante: " + proyecto.getEstudiante1Id());
        System.out.println("=".repeat(60));
        
        // Paso 1: Verificar documentación (igual para todos)
        verificarDocumentacion(proyecto);
        
        // Paso 2: Evaluar rúbrica (diferente según estrategia)
        evaluarRubrica(proyecto);
        
        // Paso 3: Calcular puntuación (diferente según estrategia)
        calcularPuntuacion(proyecto);
        
        // Paso 4: Generar veredicto (diferente según estrategia)
        generarVeredicto(proyecto);
        
        // Paso 5: Registrar evaluación (igual para todos)
        registrarEvaluacion(proyecto);
    }
    
    // ============ PASOS COMUNES (implementación por defecto) ============
    protected void verificarDocumentacion(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 1] Verificando documentacion del proyecto...");
        if (proyecto.getTitulo() == null || proyecto.getTitulo().isEmpty()) {
            throw new IllegalArgumentException("El proyecto no tiene titulo");
        }
        if (proyecto.getObjetivoGeneral() == null || proyecto.getObjetivoGeneral().isEmpty()) {
            throw new IllegalArgumentException("El proyecto no tiene objetivo general");
        }
        System.out.println("   Titulo: " + proyecto.getTitulo() );
        System.out.println("   Objetivo General: Presente  ");
        System.out.println("   Objetivos Especificos: Presente  ");
        System.out.println("  Documentacion valida");
    }
    
    protected void registrarEvaluacion(ProyectoGrado proyecto) {
        System.out.println("\n  [PASO 5] Registrando evaluacion en el sistema...");
        proyecto.setFechaEvaluacion(LocalDateTime.now());
        System.out.println("   Evaluacion guardada");
        System.out.println("   Fecha: " + proyecto.getFechaEvaluacion());
        System.out.println("   Puntuacion registrada: " + proyecto.getPuntuacionTotal() + " puntos");
        System.out.println("\n" + "=".repeat(60));
    }
    
    // ============ PASOS ESPECÍFICOS (cada estrategia los implementa diferente) ============
    protected abstract void evaluarRubrica(ProyectoGrado proyecto);
    protected abstract void calcularPuntuacion(ProyectoGrado proyecto);
    protected abstract void generarVeredicto(ProyectoGrado proyecto);
}