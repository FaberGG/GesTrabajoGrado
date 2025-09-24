package co.unicauca.gestiontrabajogrado.domain.service;

import org.junit.jupiter.api.Test;
import java.io.File;
import static org.junit.jupiter.api.Assertions.*;

class ArchivoServiceTest {

    @Test
    void generarNombreUnico_noDuplicaExtension() {
        var srv = new ArchivoService();
        var n1 = srv.generarNombreUnico("FORMATO.PDF", 1, 1);
        var n2 = srv.generarNombreUnicoCarta("Carta.PDF", 1, 1);

        assertTrue(n1.endsWith(".pdf"));
        assertTrue(n2.endsWith(".pdf"));
        assertFalse(n1.toLowerCase().contains(".pdf.pdf"));
        assertFalse(n2.toLowerCase().contains(".pdf.pdf"));
    }

    @Test
    void validarTipoPDF_porExtension() {
        var srv = new ArchivoService();
        assertTrue(srv.validarTipoPDF(new File("x.pdf")));
        assertFalse(srv.validarTipoPDF(new File("x.txt")));
    }
}
