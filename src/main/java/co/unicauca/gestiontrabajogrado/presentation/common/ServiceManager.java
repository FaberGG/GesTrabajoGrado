package co.unicauca.gestiontrabajogrado.presentation.common;

import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;

/**
 * Gestor singleton para servicios de la aplicación
 * Permite acceso global a servicios importantes sin duplicar instancias
 */
public class ServiceManager {

    private static ServiceManager instance;
    private IAutenticacionService autenticacionService;

    private ServiceManager() {
        // Constructor privado para patrón singleton
    }

    /**
     * Obtiene la instancia única del ServiceManager
     */
    public static ServiceManager getInstance() {
        if (instance == null) {
            synchronized (ServiceManager.class) {
                if (instance == null) {
                    instance = new ServiceManager();
                }
            }
        }
        return instance;
    }

    /**
     * Configura el servicio de autenticación
     * Debe ser llamado al inicio de la aplicación
     */
    public void setAutenticacionService(IAutenticacionService service) {
        this.autenticacionService = service;
    }

    /**
     * Obtiene el servicio de autenticación configurado
     */
    public IAutenticacionService getAutenticacionService() {
        if (autenticacionService == null) {
            // Crear instancia por defecto si no se ha configurado
            autenticacionService = createDefaultAutenticacionService();
        }
        return autenticacionService;
    }

    /**
     * Crea una instancia por defecto del servicio de autenticación
     */
    private IAutenticacionService createDefaultAutenticacionService() {
        try {
            // Cargar tu implementación real de AutenticacionService
            Class<?> serviceClass = Class.forName("co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService");
            return (IAutenticacionService) serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("Error al cargar AutenticacionService: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("No se pudo inicializar el servicio de autenticación", e);
        }
    }
}