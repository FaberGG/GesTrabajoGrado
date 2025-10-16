package com.unicauca.identity.service.impl;

import com.unicauca.identity.dto.request.LoginRequest;
import com.unicauca.identity.dto.request.RegisterRequest;
import com.unicauca.identity.dto.request.VerifyTokenRequest;
import com.unicauca.identity.dto.response.LoginResponse;
import com.unicauca.identity.dto.response.RolesResponse;
import com.unicauca.identity.dto.response.TokenVerificationResponse;
import com.unicauca.identity.dto.response.UserResponse;
import com.unicauca.identity.entity.User;
import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import com.unicauca.identity.exception.EmailAlreadyExistsException;
import com.unicauca.identity.exception.InvalidCredentialsException;
import com.unicauca.identity.exception.InvalidTokenException;
import com.unicauca.identity.exception.UserNotFoundException;
import com.unicauca.identity.repository.UserRepository;
import com.unicauca.identity.security.JwtTokenProvider;
import com.unicauca.identity.service.AuthService;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * Implementación del servicio de autenticación
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public UserResponse register(RegisterRequest request) {
        // Verificar si el email ya existe
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException();
        }

        // Crear nuevo usuario con contraseña encriptada
        User newUser = User.builder()
                .nombres(request.getNombres())
                .apellidos(request.getApellidos())
                .celular(request.getCelular())
                .programa(request.getPrograma())
                .rol(request.getRol())
                .email(request.getEmail())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .build();

        User savedUser = userRepository.save(newUser);
        log.info("Usuario registrado exitosamente: {}", savedUser.getEmail());

        return mapUserToUserResponse(savedUser);
    }

    @Override
    public LoginResponse login(LoginRequest request) {
        // Buscar usuario por email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(InvalidCredentialsException::new);

        // Verificar contraseña
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // Generar token JWT
        String token = jwtTokenProvider.generateToken(user);

        log.info("Usuario autenticado exitosamente: {}", user.getEmail());

        return LoginResponse.builder()
                .user(mapUserToUserResponse(user))
                .token(token)
                .build();
    }

    @Override
    public UserResponse getProfile(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        return mapUserToUserResponse(user);
    }

    @Override
    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("Usuario con email " + email + " no encontrado"));

        return user.getId();
    }

    @Override
    public RolesResponse getRolesAndPrograms() {
        return RolesResponse.builder()
                .roles(Arrays.asList(Rol.values()))
                .programas(Arrays.asList(Programa.values()))
                .build();
    }

    @Override
    public TokenVerificationResponse verifyToken(VerifyTokenRequest request) {
        try {
            if (!jwtTokenProvider.validateToken(request.getToken())) {
                return TokenVerificationResponse.invalid("Token inválido o expirado");
            }

            Claims claims = jwtTokenProvider.getAllClaimsFromToken(request.getToken());
            Long userId = Long.valueOf(claims.get("userId").toString());
            String email = claims.getSubject();
            Rol rol = Rol.valueOf(claims.get("rol").toString());
            Programa programa = Programa.valueOf(claims.get("programa").toString());

            // Verificar que el usuario siga existiendo
            if (!userRepository.existsById(userId)) {
                return TokenVerificationResponse.invalid("Usuario no encontrado");
            }

            return TokenVerificationResponse.valid(
                    TokenVerificationResponse.TokenData.builder()
                            .userId(userId)
                            .email(email)
                            .rol(rol)
                            .programa(programa)
                            .build()
            );
        } catch (InvalidTokenException e) {
            log.info("Token verificado como inválido: {}", e.getMessage());
            return TokenVerificationResponse.invalid(e.getMessage());
        } catch (Exception e) {
            log.error("Error al verificar token", e);
            return TokenVerificationResponse.invalid("Error al procesar el token");
        }
    }

    @Override
    public UserResponse mapUserToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .nombres(user.getNombres())
                .apellidos(user.getApellidos())
                .celular(user.getCelular())
                .programa(user.getPrograma())
                .rol(user.getRol())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }

    @Override
    public Page<UserResponse> searchUsers(String query, Rol rol, Programa programa, int page, int size) {
        Specification<User> spec = Specification.where(null);

        // Aplicar filtro de búsqueda por texto si se proporciona
        if (query != null && !query.trim().isEmpty()) {
            String searchTerm = "%" + query.toLowerCase() + "%";
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.or(
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("nombres")), searchTerm),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("apellidos")), searchTerm),
                            criteriaBuilder.like(criteriaBuilder.lower(root.get("email")), searchTerm)
                    )
            );
        }

        // Aplicar filtro de rol si se proporciona
        if (rol != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("rol"), rol)
            );
        }

        // Aplicar filtro de programa si se proporciona
        if (programa != null) {
            spec = spec.and((root, criteriaQuery, criteriaBuilder) ->
                    criteriaBuilder.equal(root.get("programa"), programa)
            );
        }

        // Crear objeto de paginación con ordenamiento por ID
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").ascending());

        // Ejecutar la consulta
        Page<User> users = userRepository.findAll(spec, pageable);

        // Mapear resultados a DTOs
        return users.map(this::mapUserToUserResponse);
    }
}
