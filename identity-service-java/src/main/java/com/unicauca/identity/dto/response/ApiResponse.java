package com.unicauca.identity.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper genérico para todas las respuestas API
 * @param <T> Tipo de datos que contiene la respuesta
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
    private List<String> errors;

    /**
     * Crea una respuesta de éxito con datos
     *
     * @param data Los datos a devolver
     * @param message Mensaje descriptivo opcional
     * @return ApiResponse con indicación de éxito
     * @param <T> Tipo de datos
     */
    public static <T> ApiResponse<T> success(T data, String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Crea una respuesta de éxito con datos pero sin mensaje
     *
     * @param data Los datos a devolver
     * @return ApiResponse con indicación de éxito
     * @param <T> Tipo de datos
     */
    public static <T> ApiResponse<T> success(T data) {
        return success(data, null);
    }

    /**
     * Crea una respuesta de error con mensaje
     *
     * @param message El mensaje de error
     * @return ApiResponse con indicación de error
     * @param <T> Tipo de datos (generalmente Void)
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * Crea una respuesta de error con mensaje y lista detallada de errores
     *
     * @param message El mensaje general de error
     * @param errors Lista detallada de errores
     * @return ApiResponse con indicación de error
     * @param <T> Tipo de datos (generalmente Void)
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}
