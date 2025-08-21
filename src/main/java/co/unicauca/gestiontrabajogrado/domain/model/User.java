package co.unicauca.gestiontrabajogrado.domain.model;

public class User {
    private Integer id;
    private String nombres;
    private String apellidos;
    private String celular; // opcional
    private enumProgram enumProgram;
    private enumRol enumRol;
    private String email;
    private String passwordHash;

    public User() {}

    public User(Integer id, String nombres, String apellidos, String celular,
                enumProgram enumProgram, enumRol enumRol, String email, String passwordHash) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
        this.celular = celular;
        this.enumProgram = enumProgram;
        this.enumRol = enumRol;
        this.email = email;
        this.passwordHash = passwordHash;
    }

    // Getters/Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCelular() { return celular; }
    public void setCelular(String celular) { this.celular = celular; }
    public enumProgram getPrograma() { return enumProgram; }
    public void setPrograma(enumProgram enumProgram) { this.enumProgram = enumProgram; }
    public enumRol getRol() { return enumRol; }
    public void setRol(enumRol enumRol) { this.enumRol = enumRol; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
}
