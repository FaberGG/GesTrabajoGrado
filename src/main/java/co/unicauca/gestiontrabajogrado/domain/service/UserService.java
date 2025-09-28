package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.dto.UserInfoDTO;

import java.util.Optional;

/**
 * Servicio para operaciones relacionadas con usuarios
 * Encapsula la lógica de negocio y formateo de datos de usuario
 */
public class UserService implements IUserService {

    private final IUserRepository userRepository;

    public UserService(IUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Optional<UserInfoDTO> obtenerInformacionUsuario(Integer userId) {
        if (userId == null) {
            return Optional.empty();
        }

        try {
            Optional<User> user = userRepository.findById(userId);
            return user.map(this::convertToUserInfoDTO);
        } catch (Exception e) {
            System.err.println("Error obteniendo información de usuario " + userId + ": " + e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public String obtenerNombreCompleto(Integer userId) {
        return obtenerInformacionUsuario(userId)
                .map(UserInfoDTO::getNombreCompleto)
                .orElse("Usuario no encontrado");
    }

    @Override
    public boolean existeUsuario(Integer userId) {
        if (userId == null) return false;

        try {
            return userRepository.existsById(userId);
        } catch (Exception e) {
            System.err.println("Error verificando existencia de usuario " + userId + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Convierte un User a UserInfoDTO aplicando lógica de formateo
     */
    private UserInfoDTO convertToUserInfoDTO(User user) {
        UserInfoDTO dto = new UserInfoDTO();
        dto.setId(user.getId());
        dto.setNombres(user.getNombres());
        dto.setApellidos(user.getApellidos());
        dto.setEmail(user.getEmail());
        dto.setRol(user.getRol());
        dto.setPrograma(user.getPrograma());

        // Lógica de formateo de nombre completo
        dto.setNombreCompleto(formatearNombreCompleto(user));

        return dto;
    }

    /**
     * Aplica reglas de negocio para formatear nombres
     */
    private String formatearNombreCompleto(User user) {
        if (user == null) return "Usuario no encontrado";

        StringBuilder nombre = new StringBuilder();

        if (user.getNombres() != null && !user.getNombres().trim().isEmpty()) {
            nombre.append(user.getNombres().trim());
        }

        if (user.getApellidos() != null && !user.getApellidos().trim().isEmpty()) {
            if (nombre.length() > 0) nombre.append(" ");
            nombre.append(user.getApellidos().trim());
        }

        return nombre.length() > 0 ? nombre.toString() : "Nombre no disponible";
    }
}