package com.unicauca.identity.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de verificación de token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyTokenRequest {

    @NotBlank(message = "El token es obligatorio")
    private String token;
}
