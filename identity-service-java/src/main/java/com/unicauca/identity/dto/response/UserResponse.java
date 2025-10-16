package com.unicauca.identity.dto.response;

import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para la respuesta con datos del usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponse {

    private Long id;
    private String nombres;
    private String apellidos;
    private String celular;
    private Programa programa;
    private Rol rol;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
