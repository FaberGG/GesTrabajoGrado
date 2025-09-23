package co.unicauca.gestiontrabajogrado.domain.service;

import java.io.File;
// Interfaz para el servicio de manejo de archivos
// Define los métodos para guardar, recuperar, eliminar y validar archivos PDF
// Además, incluye métodos para generar nombres únicos para los archivos
// Estructura de directorios propuesta:
//uploads/
//        ├── formatos-a/
//        │   └── P1_I1_abc12345_FormatoA.pdf
//└── cartas-aceptacion/
//        └── CARTA_P1_I1_def67890_CartaEmpresa.pdf
public interface IArchivoService {
    String guardarArchivo(File archivo, Integer proyectoId, Integer numeroIntento);
    // Nuevo metodo para guardar carta de aceptación
    String guardarArchivoCarta(File cartaAceptacion, Integer proyectoId, Integer numeroIntento);

    File recuperarArchivo(String rutaArchivo);
    boolean eliminarArchivo(String rutaArchivo);
    boolean validarTipoPDF(File archivo);
    String generarNombreUnico(String nombreOriginal, Integer proyectoId, Integer intento);
    // Nuevo metodo para generar nombre único de cartas
    String generarNombreUnicoCarta(String nombreOriginal, Integer proyectoId, Integer intento);
}