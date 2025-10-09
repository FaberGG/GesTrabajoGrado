/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package co.unicauca.domain.entities;

import java.util.HashMap;
import java.util.Map;

/**
 * Clase para encapsular los criterios de evaluación con sus puntuaciones
 */
public class CriteriosEvaluacion {
    private Map<String, Integer> criterios;
    
    public CriteriosEvaluacion() {
        this.criterios = new HashMap<>();
    }
    
    public void agregarCriterio(String nombre, Integer puntuacion) {
        this.criterios.put(nombre, puntuacion);
    }
    
    public Integer obtenerPuntuacion(String criterio) {
        return this.criterios.getOrDefault(criterio, 0);
    }
    
    public int calcularTotal() {
        return criterios.values().stream().mapToInt(Integer::intValue).sum();
    }
    
    public Map<String, Integer> getCriterios() {
        return new HashMap<>(criterios);
    }
}