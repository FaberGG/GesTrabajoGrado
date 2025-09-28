package co.unicauca.gestiontrabajogrado.dto;

import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;

/**
 * DTO para transferir información básica de usuario
 * Contiene solo los datos necesarios para la presentación
 */
public class UserInfoDTO {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String nombreCompleto;
    private String email;
    private enumRol rol;
    private enumProgram programa;

    // Constructores
    public UserInfoDTO() {}

    public UserInfoDTO(Integer id, String nombres, String apellidos, String email,
                       enumRol rol, enumProgram programa) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.email = email;
        this.rol = rol;
        this.programa = programa;
    }

    // Getters y Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public enumRol getRol() {
        return rol;
    }

    public void setRol(enumRol rol) {
        this.rol = rol;
    }

    public enumProgram getPrograma() {
        return programa;
    }

    public void setPrograma(enumProgram programa) {
        this.programa = programa;
    }

    @Override
    public String toString() {
        return "UserInfoDTO{" +
                "id=" + id +
                ", nombreCompleto='" + nombreCompleto + '\'' +
                ", email='" + email + '\'' +
                ", rol=" + rol +
                '}';
    }
}