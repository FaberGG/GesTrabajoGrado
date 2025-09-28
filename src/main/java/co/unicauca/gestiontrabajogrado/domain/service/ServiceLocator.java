package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;

/**
 * Service Locator para gestión de dependencias de la aplicación
 * Implementa el patrón Service Locator con thread-safety
 */
public class ServiceLocator {

    private static volatile ServiceLocator instance;

    // Servicios
    private IAutenticacionService autenticacionService;
    private IProyectoGradoService proyectoGradoService;
    private IArchivoService archivoService;

    // Repositorios
    private IUserRepository userRepository;
    private IProyectoGradoRepository proyectoGradoRepository;
    private IFormatoARepository formatoARepository;

    private ServiceLocator() {
        // Constructor privado para singleton
    }

    /**
     * Obtiene la instancia única del ServiceLocator (thread-safe)
     */
    public static ServiceLocator getInstance() {
        if (instance == null) {
            synchronized (ServiceLocator.class) {
                if (instance == null) {
                    instance = new ServiceLocator();
                }
            }
        }
        return instance;
    }

    /**
     * Configura todos los servicios de una vez
     * Este método debe ser llamado al inicio de la aplicación
     */
    public void configure(IUserRepository userRepo,
                          IProyectoGradoRepository proyectoRepo,
                          IFormatoARepository formatoRepo,
                          IAutenticacionService autenticacionServ,
                          IProyectoGradoService proyectoServ,
                          IArchivoService archivoServ) {

        // Validar que no se configure más de una vez
        if (this.userRepository != null) {
            throw new IllegalStateException("ServiceLocator ya ha sido configurado");
        }

        this.userRepository = userRepo;
        this.proyectoGradoRepository = proyectoRepo;
        this.formatoARepository = formatoRepo;
        this.autenticacionService = autenticacionServ;
        this.proyectoGradoService = proyectoServ;
        this.archivoService = archivoServ;
    }

    /**
     * Verifica si el ServiceLocator ha sido configurado
     */
    public boolean isConfigured() {
        return userRepository != null;
    }

    // === GETTERS PARA SERVICIOS ===

    public IAutenticacionService getAutenticacionService() {
        validateConfigured();
        return autenticacionService;
    }

    public IProyectoGradoService getProyectoGradoService() {
        validateConfigured();
        return proyectoGradoService;
    }

    public IArchivoService getArchivoService() {
        validateConfigured();
        return archivoService;
    }

    // === GETTERS PARA REPOSITORIOS ===

    public IUserRepository getUserRepository() {
        validateConfigured();
        return userRepository;
    }

    public IProyectoGradoRepository getProyectoGradoRepository() {
        validateConfigured();
        return proyectoGradoRepository;
    }

    public IFormatoARepository getFormatoARepository() {
        validateConfigured();
        return formatoARepository;
    }

    /**
     * Valida que el ServiceLocator esté configurado antes de usar
     */
    private void validateConfigured() {
        if (!isConfigured()) {
            throw new IllegalStateException(
                    "ServiceLocator no ha sido configurado. " +
                            "Llama a configure() al inicio de la aplicación."
            );
        }
    }

    /**
     * Método para limpiar la instancia (útil para testing)
     */
    public static void reset() {
        instance = null;
    }
}