package com.unicauca.identity.dto.response;

import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO para la respuesta con roles y programas disponibles
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RolesResponse {

    private List<Rol> roles;
    private List<Programa> programas;
}
