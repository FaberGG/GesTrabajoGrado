package co.unicauca.gestiontrabajogrado.domain.service;

import java.io.*;
import java.nio.file.*;
import java.util.UUID;

public class ArchivoService implements IArchivoService {
    private final String BASE_PATH = "uploads/formatos-a/";
    private final String CARTA_PATH = "uploads/cartas-aceptacion/";

    @Override
    public String guardarArchivo(File archivo, Integer proyectoId, Integer numeroIntento) {
        if (!validarTipoPDF(archivo)) {
            throw new IllegalArgumentException("El archivo debe ser PDF");
        }

        new File(BASE_PATH).mkdirs();
        String nombre = generarNombreUnico(archivo.getName(), proyectoId, numeroIntento);
        Path destino = Paths.get(BASE_PATH + nombre);

        try {
            Files.copy(archivo.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando archivo", e);
        }
    }

    @Override
    public String guardarArchivoCarta(File cartaAceptacion, Integer proyectoId, Integer numeroIntento) {
        if (!validarTipoPDF(cartaAceptacion)) {
            throw new IllegalArgumentException("La carta de aceptación debe ser PDF");
        }

        // Crear directorio si no existe
        new File(CARTA_PATH).mkdirs();

        String nombre = generarNombreUnicoCarta(cartaAceptacion.getName(), proyectoId, numeroIntento);
        Path destino = Paths.get(CARTA_PATH + nombre);

        try {
            Files.copy(cartaAceptacion.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            return destino.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error guardando carta de aceptación", e);
        }
    }

    @Override
    public File recuperarArchivo(String rutaArchivo) {
        return new File(rutaArchivo);
    }

    @Override
    public boolean eliminarArchivo(String rutaArchivo) {
        return new File(rutaArchivo).delete();
    }

    @Override
    public boolean validarTipoPDF(File archivo) {
        try {
            String mime = Files.probeContentType(archivo.toPath());
            return (mime != null && mime.equals("application/pdf")) ||
                    archivo.getName().toLowerCase().endsWith(".pdf");
        } catch (IOException e) {
            return archivo.getName().toLowerCase().endsWith(".pdf");
        }
    }

    @Override
    public String generarNombreUnico(String nombreOriginal, Integer proyectoId, Integer intento) {
        String limpio = nombreOriginal.replaceAll("[^a-zA-Z0-9_.-]", "_")
                .replaceAll("(?i)\\.pdf$", "");
        return String.format("P%d_I%d_%s_%s.pdf",
                proyectoId, intento,
                UUID.randomUUID().toString().substring(0, 8),
                limpio);
    }

    @Override
    public String generarNombreUnicoCarta(String nombreOriginal, Integer proyectoId, Integer intento) {
        String limpio = nombreOriginal.replaceAll("[^a-zA-Z0-9_.-]", "_")
                .replaceAll("(?i)\\.pdf$", "");
        return String.format("CARTA_P%d_I%d_%s_%s.pdf",
                proyectoId, intento,
                UUID.randomUUID().toString().substring(0, 8),
                limpio);
    }
}