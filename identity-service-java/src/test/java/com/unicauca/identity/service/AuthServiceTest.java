package com.unicauca.identity.service;

import com.unicauca.identity.dto.request.LoginRequest;
import com.unicauca.identity.dto.request.RegisterRequest;
import com.unicauca.identity.dto.response.LoginResponse;
import com.unicauca.identity.dto.response.UserResponse;
import com.unicauca.identity.entity.User;
import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import com.unicauca.identity.exception.EmailAlreadyExistsException;
import com.unicauca.identity.exception.InvalidCredentialsException;
import com.unicauca.identity.repository.UserRepository;
import com.unicauca.identity.security.JwtTokenProvider;
import com.unicauca.identity.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    private User testUser;
    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;

    @BeforeEach
    void setUp() {
        // Configuración de datos de prueba
        testUser = User.builder()
                .id(1L)
                .nombres("Juan")
                .apellidos("Perez")
                .email("jperez@unicauca.edu.co")
                .programa(Programa.INGENIERIA_DE_SISTEMAS)
                .rol(Rol.ESTUDIANTE)
                .passwordHash("hashedPassword")
                .celular("3201234567")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        registerRequest = RegisterRequest.builder()
                .nombres("Juan")
                .apellidos("Perez")
                .email("jperez@unicauca.edu.co")
                .programa(Programa.INGENIERIA_DE_SISTEMAS)
                .rol(Rol.ESTUDIANTE)
                .password("Pass123!")
                .celular("3201234567")
                .build();

        loginRequest = LoginRequest.builder()
                .email("jperez@unicauca.edu.co")
                .password("Pass123!")
                .build();
    }

    @Test
    void register_ShouldReturnUserResponse_WhenUserDataIsValid() {
        // Configurar comportamiento de los mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("hashedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // Ejecutar método a probar
        UserResponse response = authService.register(registerRequest);

        // Verificar resultados
        assertNotNull(response);
        assertEquals(testUser.getId(), response.getId());
        assertEquals(testUser.getNombres(), response.getNombres());
        assertEquals(testUser.getApellidos(), response.getApellidos());
        assertEquals(testUser.getEmail(), response.getEmail());
        assertEquals(testUser.getPrograma(), response.getPrograma());
        assertEquals(testUser.getRol(), response.getRol());
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        // Configurar comportamiento de los mocks
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // Verificar que se lanza la excepción esperada
        assertThrows(EmailAlreadyExistsException.class, () -> {
            authService.register(registerRequest);
        });
    }

    @Test
    void login_ShouldReturnLoginResponse_WhenCredentialsAreValid() {
        // Configurar comportamiento de los mocks
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtTokenProvider.generateToken(any(User.class))).thenReturn("jwt.token.here");

        // Ejecutar método a probar
        LoginResponse response = authService.login(loginRequest);

        // Verificar resultados
        assertNotNull(response);
        assertNotNull(response.getToken());
        assertEquals("jwt.token.here", response.getToken());
        assertEquals(testUser.getId(), response.getUser().getId());
        assertEquals(testUser.getEmail(), response.getUser().getEmail());
    }

    @Test
    void login_ShouldThrowException_WhenEmailNotFound() {
        // Configurar comportamiento de los mocks
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // Verificar que se lanza la excepción esperada
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }

    @Test
    void login_ShouldThrowException_WhenPasswordIsInvalid() {
        // Configurar comportamiento de los mocks
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // Verificar que se lanza la excepción esperada
        assertThrows(InvalidCredentialsException.class, () -> {
            authService.login(loginRequest);
        });
    }
}
