package co.unicauca.domain.entities;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

class CriteriosEvaluacionTest {
    
    private CriteriosEvaluacion criterios;
    
    @BeforeEach
    void setUp() {
        criterios = new CriteriosEvaluacion();
    }
    
    @Test
    void testAgregarCriterio() {
        // Arrange & Act
        criterios.agregarCriterio("competencias", 28);
        
        // Assert
        assertEquals(28, criterios.obtenerPuntuacion("competencias"));
    }
    
    @Test
    void testCalcularTotal() {
        // Arrange
        criterios.agregarCriterio("competencias", 28);
        criterios.agregarCriterio("impacto", 22);
        criterios.agregarCriterio("documentacion", 19);
        criterios.agregarCriterio("presentacion", 23);
        
        // Act
        int total = criterios.calcularTotal();
        
        // Assert
        assertEquals(92, total);
    }
    
    @Test
    void testObtenerPuntuacionCriterioNoExistente() {
        // Act
        int puntuacion = criterios.obtenerPuntuacion("criterio_inexistente");
        
        // Assert
        assertEquals(0, puntuacion, "Debe retornar 0 si el criterio no existe");
    }
}