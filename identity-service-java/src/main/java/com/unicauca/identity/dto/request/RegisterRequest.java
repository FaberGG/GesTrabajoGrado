package com.unicauca.identity.dto.request;

import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import com.unicauca.identity.validation.InstitutionalEmail;
import com.unicauca.identity.validation.StrongPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la solicitud de registro de un nuevo usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {

    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$",
             message = "Nombres debe contener solo letras y tener al menos 2 caracteres")
    private String nombres;

    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$",
             message = "Apellidos debe contener solo letras y tener al menos 2 caracteres")
    private String apellidos;

    @Pattern(regexp = "^[0-9]{10}$",
             message = "Celular debe tener 10 dígitos numéricos")
    private String celular;

    @NotNull(message = "El programa es obligatorio")
    private Programa programa;

    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato del email no es válido")
    @InstitutionalEmail
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @StrongPassword
    private String password;
}
