package com.unicauca.identity.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.unicauca.identity.enums.Programa;
import com.unicauca.identity.enums.Rol;
import com.unicauca.identity.validation.InstitutionalEmail;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * Entidad que representa a un usuario en el sistema
 */
@Entity
@Table(name = "usuarios", indexes = {
    @Index(name = "idx_usuarios_email", columnList = "email"),
    @Index(name = "idx_usuarios_rol", columnList = "rol")
})
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombres", nullable = false, length = 100)
    @NotBlank(message = "Los nombres son obligatorios")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$",
             message = "Nombres debe contener solo letras y tener al menos 2 caracteres")
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    @NotBlank(message = "Los apellidos son obligatorios")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ ]{2,}$",
             message = "Apellidos debe contener solo letras y tener al menos 2 caracteres")
    private String apellidos;

    @Column(name = "celular", length = 20)
    @Pattern(regexp = "^[0-9]{10}$",
             message = "Celular debe tener 10 dígitos numéricos")
    private String celular;

    @Enumerated(EnumType.STRING)
    @Column(name = "programa", nullable = false)
    @NotNull(message = "El programa es obligatorio")
    private Programa programa;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false)
    @NotNull(message = "El rol es obligatorio")
    private Rol rol;

    @Column(name = "email", nullable = false, unique = true)
    @Email(message = "Email debe ser válido")
    @NotBlank(message = "El email es obligatorio")
    @InstitutionalEmail
    private String email;

    @Column(name = "password_hash", nullable = false)
    @JsonIgnore
    private String passwordHash;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    @LastModifiedDate
    private LocalDateTime updatedAt;
}
