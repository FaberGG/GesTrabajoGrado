package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.dto.UserInfoDTO;
import java.util.Optional;

/**
 * Interfaz para operaciones de servicio relacionadas con usuarios
 */
public interface IUserService {

    /**
     * Obtiene información completa de un usuario
     * @param userId ID del usuario
     * @return UserInfoDTO con la información formateada, o Optional.empty() si no existe
     */
    Optional<UserInfoDTO> obtenerInformacionUsuario(Integer userId);

    /**
     * Obtiene el nombre completo formateado de un usuario
     * @param userId ID del usuario
     * @return Nombre completo o mensaje de error si no existe
     */
    String obtenerNombreCompleto(Integer userId);

    /**
     * Verifica si existe un usuario con el ID dado
     * @param userId ID del usuario a verificar
     * @return true si existe, false en caso contrario
     */
    boolean existeUsuario(Integer userId);
}