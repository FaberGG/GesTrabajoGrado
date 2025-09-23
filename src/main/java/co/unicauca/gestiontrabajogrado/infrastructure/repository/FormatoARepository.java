package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.FormatoA;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FormatoARepository implements IFormatoARepository {

    @Override
    public FormatoA save(FormatoA formatoA) {
        if (formatoA.getId() == null) {
            return insert(formatoA);
        } else {
            return update(formatoA);
        }
    }

    private FormatoA insert(FormatoA formato) {
        String sql = """
            INSERT INTO formato_a 
            (proyecto_grado_id, numero_intento, ruta_archivo, nombre_archivo, 
             ruta_carta_aceptacion, nombre_carta_aceptacion, fecha_carga, estado, 
             observaciones, evaluado_por, fecha_evaluacion)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, formato.getProyectoGradoId());
            ps.setInt(2, formato.getNumeroIntento());
            ps.setString(3, formato.getRutaArchivo());
            ps.setString(4, formato.getNombreArchivo());

            // Nuevos campos para carta de aceptación
            if (formato.getRutaCartaAceptacion() != null) {
                ps.setString(5, formato.getRutaCartaAceptacion());
            } else {
                ps.setNull(5, Types.VARCHAR);
            }

            if (formato.getNombreCartaAceptacion() != null) {
                ps.setString(6, formato.getNombreCartaAceptacion());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            ps.setTimestamp(7, Timestamp.valueOf(
                    formato.getFechaCarga() != null ? formato.getFechaCarga() : LocalDateTime.now()));
            ps.setString(8, formato.getEstado().name());

            if (formato.getObservaciones() != null) {
                ps.setString(9, formato.getObservaciones());
            } else {
                ps.setNull(9, Types.VARCHAR);
            }

            if (formato.getEvaluadoPor() != null) {
                ps.setInt(10, formato.getEvaluadoPor());
            } else {
                ps.setNull(10, Types.INTEGER);
            }

            if (formato.getFechaEvaluacion() != null) {
                ps.setTimestamp(11, Timestamp.valueOf(formato.getFechaEvaluacion()));
            } else {
                ps.setNull(11, Types.TIMESTAMP);
            }

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    formato.setId(keys.getInt(1));
                }
            }

            return formato;

        } catch (SQLException e) {
            if (e.getMessage().contains("UNIQUE constraint failed")) {
                throw new IllegalArgumentException("Ya existe un formato para este proyecto en el intento " + formato.getNumeroIntento());
            }
            throw new RuntimeException("Error guardando formato A: " + e.getMessage(), e);
        }
    }

    @Override
    public FormatoA update(FormatoA formato) {
        String sql = """
            UPDATE formato_a SET 
            ruta_archivo = ?, nombre_archivo = ?, ruta_carta_aceptacion = ?, 
            nombre_carta_aceptacion = ?, estado = ?, observaciones = ?, 
            evaluado_por = ?, fecha_evaluacion = ?
            WHERE id = ?
        """;

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, formato.getRutaArchivo());
            ps.setString(2, formato.getNombreArchivo());

            // Campos de carta de aceptación
            if (formato.getRutaCartaAceptacion() != null) {
                ps.setString(3, formato.getRutaCartaAceptacion());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }

            if (formato.getNombreCartaAceptacion() != null) {
                ps.setString(4, formato.getNombreCartaAceptacion());
            } else {
                ps.setNull(4, Types.VARCHAR);
            }

            ps.setString(5, formato.getEstado().name());

            if (formato.getObservaciones() != null) {
                ps.setString(6, formato.getObservaciones());
            } else {
                ps.setNull(6, Types.VARCHAR);
            }

            if (formato.getEvaluadoPor() != null) {
                ps.setInt(7, formato.getEvaluadoPor());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            if (formato.getFechaEvaluacion() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(formato.getFechaEvaluacion()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            ps.setInt(9, formato.getId());

            int rowsAffected = ps.executeUpdate();
            if (rowsAffected == 0) {
                throw new RuntimeException("No se encontró el formato con ID: " + formato.getId());
            }

            return formato;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando formato A: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FormatoA> findById(Integer id) {
        String sql = "SELECT * FROM formato_a WHERE id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFormato(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando formato por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FormatoA> findByProyectoGradoId(Integer proyectoGradoId) {
        String sql = "SELECT * FROM formato_a WHERE proyecto_grado_id = ? ORDER BY numero_intento ASC";
        return executeQuery(sql, proyectoGradoId);
    }

    @Override
    public Optional<FormatoA> findByProyectoGradoIdAndNumeroIntento(Integer proyectoGradoId, Integer numeroIntento) {
        String sql = "SELECT * FROM formato_a WHERE proyecto_grado_id = ? AND numero_intento = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, proyectoGradoId);
            ps.setInt(2, numeroIntento);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFormato(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando formato por proyecto e intento: " + e.getMessage(), e);
        }
    }

    @Override
    public Optional<FormatoA> findLastFormatoByProyectoId(Integer proyectoGradoId) {
        String sql = """
            SELECT * FROM formato_a 
            WHERE proyecto_grado_id = ? 
            ORDER BY numero_intento DESC 
            LIMIT 1
        """;

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, proyectoGradoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearFormato(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando último formato: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FormatoA> findByEstado(enumEstadoFormato estado) {
        String sql = "SELECT * FROM formato_a WHERE estado = ? ORDER BY fecha_carga DESC";
        return executeQuery(sql, estado.name());
    }

    @Override
    public List<FormatoA> findByEvaluadoPor(Integer evaluadorId) {
        String sql = "SELECT * FROM formato_a WHERE evaluado_por = ? ORDER BY fecha_evaluacion DESC";
        return executeQuery(sql, evaluadorId);
    }

    @Override
    public Integer countByProyectoGradoId(Integer proyectoGradoId) {
        String sql = "SELECT COUNT(*) FROM formato_a WHERE proyecto_grado_id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, proyectoGradoId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error contando formatos por proyecto: " + e.getMessage(), e);
        }
    }

    @Override
    public List<FormatoA> findAll() {
        String sql = "SELECT * FROM formato_a ORDER BY fecha_carga DESC";
        return executeQuery(sql);
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM formato_a WHERE id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("No se encontró el formato con ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando formato: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteByProyectoGradoId(Integer proyectoGradoId) {
        String sql = "DELETE FROM formato_a WHERE proyecto_grado_id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, proyectoGradoId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando formatos del proyecto: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares
    private List<FormatoA> executeQuery(String sql, Object... params) {
        List<FormatoA> formatos = new ArrayList<>();

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    formatos.add(mapearFormato(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error ejecutando consulta: " + e.getMessage(), e);
        }

        return formatos;
    }

    private FormatoA mapearFormato(ResultSet rs) throws SQLException {
        FormatoA formato = new FormatoA();

        formato.setId(rs.getInt("id"));
        formato.setProyectoGradoId(rs.getInt("proyecto_grado_id"));
        formato.setNumeroIntento(rs.getInt("numero_intento"));
        formato.setRutaArchivo(rs.getString("ruta_archivo"));
        formato.setNombreArchivo(rs.getString("nombre_archivo"));

        // Mapear nuevos campos de carta de aceptación
        String rutaCarta = rs.getString("ruta_carta_aceptacion");
        if (!rs.wasNull()) {
            formato.setRutaCartaAceptacion(rutaCarta);
        }

        String nombreCarta = rs.getString("nombre_carta_aceptacion");
        if (!rs.wasNull()) {
            formato.setNombreCartaAceptacion(nombreCarta);
        }

        formato.setFechaCarga(rs.getTimestamp("fecha_carga").toLocalDateTime());
        formato.setEstado(enumEstadoFormato.valueOf(rs.getString("estado")));

        String observaciones = rs.getString("observaciones");
        if (!rs.wasNull()) {
            formato.setObservaciones(observaciones);
        }

        Integer evaluadoPor = rs.getInt("evaluado_por");
        if (!rs.wasNull()) {
            formato.setEvaluadoPor(evaluadoPor);
        }

        Timestamp fechaEvaluacion = rs.getTimestamp("fecha_evaluacion");
        if (!rs.wasNull()) {
            formato.setFechaEvaluacion(fechaEvaluacion.toLocalDateTime());
        }

        return formato;
    }
}