package com.unicauca.identity.dto.request;

import com.unicauca.identity.validation.InstitutionalEmail;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de login
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @InstitutionalEmail
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;
}
