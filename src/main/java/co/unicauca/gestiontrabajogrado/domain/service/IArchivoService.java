package co.unicauca.gestiontrabajogrado.domain.service;

import java.io.File;

public interface IArchivoService {
    String guardarArchivo(File archivo, Integer proyectoId, Integer numeroIntento);
    File recuperarArchivo(String rutaArchivo);
    boolean eliminarArchivo(String rutaArchivo);
    boolean validarTipoPDF(File archivo);
    String generarNombreUnico(String nombreOriginal, Integer proyectoId, Integer intento);
}