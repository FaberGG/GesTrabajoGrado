package co.unicauca.gestiontrabajogrado.presentation.common;

import co.unicauca.gestiontrabajogrado.domain.service.*;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.*;

/**
 * Gestor singleton para servicios de la aplicación
 * Permite acceso global a servicios importantes sin duplicar instancias
 */
public class ServiceManager {

    private static ServiceManager instance;

    // Servicios
    private IAutenticacionService autenticacionService;
    private IProyectoGradoService proyectoGradoService;
    private IArchivoService archivoService;

    // Repositorios
    private IProyectoGradoRepository proyectoGradoRepository;
    private IFormatoARepository formatoARepository;
    private IUserRepository userRepository;

    private ServiceManager() {
        // Constructor privado para patrón singleton
        inicializarServicios();
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
     * Inicializa todos los servicios y repositorios necesarios
     */
    private void inicializarServicios() {
        try {
            // Inicializar repositorios primero (sin dependencias)
            userRepository = crearInstancia(
                    "co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository",
                    IUserRepository.class
            );

            proyectoGradoRepository = crearInstancia(
                    "co.unicauca.gestiontrabajogrado.infrastructure.repository.ProyectoGradoRepository",
                    IProyectoGradoRepository.class
            );

            formatoARepository = crearInstancia(
                    "co.unicauca.gestiontrabajogrado.infrastructure.repository.FormatoARepository",
                    IFormatoARepository.class
            );

            // Inicializar servicio de archivos
            archivoService = new ArchivoService();

            // Inicializar servicio de proyecto (requiere repositorios y archivos)
            proyectoGradoService = new ProyectoGradoService(
                    proyectoGradoRepository,
                    formatoARepository,
                    archivoService,
                    userRepository
            );

            // Autenticación - intentar crear con userRepository si existe
            try {
                Class<?> authClass = Class.forName("co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService");

                // Intentar constructor con IUserRepository
                try {
                    autenticacionService = (IAutenticacionService) authClass
                            .getConstructor(IUserRepository.class)
                            .newInstance(userRepository);
                } catch (NoSuchMethodException e) {
                    // Intentar constructor vacío
                    try {
                        autenticacionService = (IAutenticacionService) authClass
                                .getDeclaredConstructor()
                                .newInstance();
                    } catch (Exception e2) {
                        System.err.println("AutenticacionService no disponible: " + e2.getMessage());
                        autenticacionService = null;
                    }
                }
            } catch (ClassNotFoundException e) {
                System.err.println("AutenticacionService no encontrado: " + e.getMessage());
                autenticacionService = null;
            }

        } catch (Exception e) {
            System.err.println("Error inicializando servicios: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Crea una instancia de una clase usando reflexión (solo constructores sin parámetros)
     */
    @SuppressWarnings("unchecked")
    private <T> T crearInstancia(String nombreClase, Class<T> interfaz) {
        try {
            Class<?> clase = Class.forName(nombreClase);
            return (T) clase.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            System.err.println("No se pudo crear instancia de " + nombreClase + ": " + e.getMessage());
            return null;
        }
    }

    // ========== GETTERS ==========

    public IAutenticacionService getAutenticacionService() {
        return autenticacionService;
    }

    public IProyectoGradoService getProyectoGradoService() {
        return proyectoGradoService;
    }

    // Alias para el método del main
    public IProyectoGradoService getProyectoService() {
        return proyectoGradoService;
    }

    public IArchivoService getArchivoService() {
        return archivoService;
    }

    public IProyectoGradoRepository getProyectoGradoRepository() {
        return proyectoGradoRepository;
    }

    // Alias para el método del main
    public IProyectoGradoRepository getProyectoRepository() {
        return proyectoGradoRepository;
    }

    public IFormatoARepository getFormatoARepository() {
        return formatoARepository;
    }

    // Alias para el método del main
    public IFormatoARepository getFormatoRepository() {
        return formatoARepository;
    }

    public IUserRepository getUserRepository() {
        return userRepository;
    }

    // ========== SETTERS (para configuración manual si es necesario) ==========

    public void setAutenticacionService(IAutenticacionService service) {
        this.autenticacionService = service;
    }

    public void setProyectoGradoService(IProyectoGradoService service) {
        this.proyectoGradoService = service;
    }

    public void setProyectoGradoRepository(IProyectoGradoRepository repository) {
        this.proyectoGradoRepository = repository;
    }

    public void setFormatoARepository(IFormatoARepository repository) {
        this.formatoARepository = repository;
    }

    public void setUserRepository(IUserRepository repository) {
        this.userRepository = repository;
    }
}