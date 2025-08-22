package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import java.sql.*;
import java.util.Optional;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;

public class UserRepository implements IUserRepository {
    // Implementación de los métodos de IUserRepository
    // Aquí puedes usar DatabaseConnection para obtener conexiones a la base de datos
    // y realizar las operaciones necesarias sobre los usuarios.
    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM usuarios WHERE email = ?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (Exception e) {
            throw new RuntimeException("Error comprobando email: " + e.getMessage(), e);
        }
    }

    @Override
    public User save(User u) {
        String sql = "INSERT INTO usuarios(nombres, apellidos, celular, enumProgram, enumRol, email, password) " +
                "VALUES(?,?,?,?,?,?,?)";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombres());
            ps.setString(2, u.getApellidos());
            ps.setString(3, u.getCelular());
            ps.setString(4, u.getPrograma().name());
            ps.setString(5, u.getRol().name());
            ps.setString(6, u.getEmail());
            ps.setString(7, u.getPasswordHash());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) u.setId(keys.getInt(1));
            }
            return u;
        } catch (SQLException e) {
            if (e.getMessage() != null && e.getMessage().toLowerCase().contains("unique")) {
                throw new IllegalArgumentException("El email ya está registrado");
            }
            throw new RuntimeException("Error guardando usuario: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        String sql = "SELECT * FROM usuarios WHERE email = ?";
        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return Optional.empty();
                User u = new User(
                        rs.getInt("id"),
                        rs.getString("nombres"),
                        rs.getString("apellidos"),
                        rs.getString("celular"),
                        enumProgram.valueOf(rs.getString("enumProgram")),
                        enumRol.valueOf(rs.getString("enumRol")),
                        rs.getString("email"),
                        rs.getString("password")
                );
                return Optional.of(u);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error buscando usuario por email: " + e.getMessage(), e);
        }
    }
}
