package com.unicauca.identity.dto.response;

import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para la respuesta de verificación de token
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenVerificationResponse {

    private boolean success;
    private boolean valid;
    private String message;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenData {
        private Long userId;
        private String email;
        private Rol rol;
        private Programa programa;
    }

    private TokenData data;

    public static TokenVerificationResponse valid(TokenData data) {
        return TokenVerificationResponse.builder()
                .success(true)
                .valid(true)
                .data(data)
                .build();
    }

    public static TokenVerificationResponse invalid(String message) {
        return TokenVerificationResponse.builder()
                .success(false)
                .valid(false)
                .message(message)
                .build();
    }
}
