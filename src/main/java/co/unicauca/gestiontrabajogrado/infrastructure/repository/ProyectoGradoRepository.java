
package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.ProyectoGrado;
import co.unicauca.gestiontrabajogrado.domain.model.enumModalidad;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoProyecto;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProyectoGradoRepository implements IProyectoGradoRepository {

    @Override
    public ProyectoGrado save(ProyectoGrado proyectoGrado) {
        if (proyectoGrado.getId() == null) {
            return insert(proyectoGrado);
        } else {
            return update(proyectoGrado);
        }
    }

    // ProyectoGradoRepository.java

    private ProyectoGrado insert(ProyectoGrado proyecto) {
        String sql = """
        INSERT INTO proyecto_grado
        (titulo, modalidad, fecha_creacion, director_id, codirector_id,
         objetivo_general, objetivos_especificos, estudiante1_id, estudiante2_id, estado, numero_intentos)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """;

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, proyecto.getTitulo());
            ps.setString(2, proyecto.getModalidad().name());
            ps.setTimestamp(3, Timestamp.valueOf(proyecto.getFechaCreacion()));
            ps.setInt(4, proyecto.getDirectorId());

            if (proyecto.getCodirectorId() != null) {
                ps.setInt(5, proyecto.getCodirectorId());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setString(6, proyecto.getObjetivoGeneral());
            ps.setString(7, proyecto.getObjetivosEspecificos());

            if (proyecto.getEstudiante1Id() != null) {
                ps.setInt(8, proyecto.getEstudiante1Id());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            if (proyecto.getEstudiante2Id() != null) {
                ps.setInt(9, proyecto.getEstudiante2Id());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            ps.setString(10, proyecto.getEstado().name());
            ps.setInt(11, proyecto.getNumeroIntentos() != null ? proyecto.getNumeroIntentos() : 1);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    proyecto.setId(keys.getInt(1));
                }
            }
            return proyecto;

        } catch (SQLException e) {
            throw new RuntimeException("Error guardando proyecto de grado: " + e.getMessage(), e);
        }
    }

    @Override
    public ProyectoGrado update(ProyectoGrado proyecto) {
        String sql = """
        UPDATE proyecto_grado SET
        titulo = ?, modalidad = ?, director_id = ?, codirector_id = ?,
        objetivo_general = ?, objetivos_especificos = ?, estudiante1_id = ?, estudiante2_id = ?,
        estado = ?, numero_intentos = ?, updated_at = CURRENT_TIMESTAMP
        WHERE id = ?
    """;

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, proyecto.getTitulo());
            ps.setString(2, proyecto.getModalidad().name());
            ps.setInt(3, proyecto.getDirectorId());

            if (proyecto.getCodirectorId() != null) {
                ps.setInt(4, proyecto.getCodirectorId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            ps.setString(5, proyecto.getObjetivoGeneral());
            ps.setString(6, proyecto.getObjetivosEspecificos());

            if (proyecto.getEstudiante1Id() != null) {
                ps.setInt(7, proyecto.getEstudiante1Id());
            } else {
                ps.setNull(7, Types.INTEGER);
            }

            if (proyecto.getEstudiante2Id() != null) {
                ps.setInt(8, proyecto.getEstudiante2Id());
            } else {
                ps.setNull(8, Types.INTEGER);
            }

            ps.setString(9, proyecto.getEstado().name());
            ps.setInt(10, proyecto.getNumeroIntentos() != null ? proyecto.getNumeroIntentos() : 1);
            ps.setInt(11, proyecto.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("No se encontró el proyecto con ID: " + proyecto.getId());
            }
            return proyecto;

        } catch (SQLException e) {
            throw new RuntimeException("Error actualizando proyecto de grado: " + e.getMessage(), e);
        }
    }


    @Override
    public Optional<ProyectoGrado> findById(Integer id) {
        String sql = "SELECT * FROM proyecto_grado WHERE id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearProyecto(rs));
                }
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error buscando proyecto por ID: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProyectoGrado> findByDirectorId(Integer directorId) {
        String sql = "SELECT * FROM proyecto_grado WHERE director_id = ? ORDER BY fecha_creacion DESC";
        return executeQuery(sql, directorId);
    }

    @Override
    public List<ProyectoGrado> findByCodirectorId(Integer codirectorId) {
        String sql = "SELECT * FROM proyecto_grado WHERE codirector_id = ? ORDER BY fecha_creacion DESC";
        return executeQuery(sql, codirectorId);
    }

    /**
     * Obtiene todos los proyectos donde el docente participa como director o codirector
     * @param docenteId ID del docente
     * @return Lista de proyectos donde participa
     */
    @Override
    public List<ProyectoGrado> findByDocente(Integer docenteId) {
        String sql = """
            SELECT * FROM proyecto_grado 
            WHERE director_id = ? OR codirector_id = ? 
            ORDER BY fecha_creacion DESC
        """;
        return executeQuery(sql, docenteId, docenteId);
    }

    @Override
    public List<ProyectoGrado> findByEstudianteId(Integer estudianteId) {
        String sql = "SELECT * FROM proyecto_grado WHERE estudiante1_id = ? OR estudiante2_id = ? ORDER BY fecha_creacion DESC";
        return executeQuery(sql, estudianteId, estudianteId);
    }



    @Override
    public List<ProyectoGrado> findByEstado(enumEstadoProyecto estado) {
        String sql = "SELECT * FROM proyecto_grado WHERE estado = ? ORDER BY fecha_creacion DESC";
        return executeQuery(sql, estado.name());
    }

    @Override
    public List<ProyectoGrado> findByTituloContaining(String titulo) {
        String sql = "SELECT * FROM proyecto_grado WHERE LOWER(titulo) LIKE LOWER(?) ORDER BY titulo";
        return executeQuery(sql, "%" + titulo + "%");
    }

    @Override
    public long countByDirectorId(Integer directorId) {
        String sql = "SELECT COUNT(*) FROM proyecto_grado WHERE director_id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, directorId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
                return 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error contando proyectos por director: " + e.getMessage(), e);
        }
    }

    @Override
    public List<ProyectoGrado> findAll() {
        String sql = "SELECT * FROM proyecto_grado ORDER BY fecha_creacion DESC";
        return executeQuery(sql);
    }

    @Override
    public void deleteById(Integer id) {
        String sql = "DELETE FROM proyecto_grado WHERE id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();

            if (rowsAffected == 0) {
                throw new RuntimeException("No se encontró el proyecto con ID: " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error eliminando proyecto: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean existsById(Integer id) {
        String sql = "SELECT 1 FROM proyecto_grado WHERE id = ?";

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error verificando existencia del proyecto: " + e.getMessage(), e);
        }
    }

    // Métodos auxiliares
    private List<ProyectoGrado> executeQuery(String sql, Object... params) {
        List<ProyectoGrado> proyectos = new ArrayList<>();

        try (Connection c = DatabaseConnection.get();
             PreparedStatement ps = c.prepareStatement(sql)) {

            for (int i = 0; i < params.length; i++) {
                ps.setObject(i + 1, params[i]);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    proyectos.add(mapearProyecto(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error ejecutando consulta: " + e.getMessage(), e);
        }

        return proyectos;
    }

    private ProyectoGrado mapearProyecto(ResultSet rs) throws SQLException {
        ProyectoGrado proyecto = new ProyectoGrado();

        proyecto.setId(rs.getInt("id"));
        proyecto.setTitulo(rs.getString("titulo"));

        // modalidad
        String modalidad = rs.getString("modalidad");
        if (modalidad != null) {
            proyecto.setModalidad(enumModalidad.valueOf(modalidad));
        }

        // fecha_creacion (evita NPE si viene null)
        java.sql.Timestamp tsCreacion = rs.getTimestamp("fecha_creacion");
        if (tsCreacion != null) {
            proyecto.setFechaCreacion(tsCreacion.toLocalDateTime());
        }

        // director y codirector
        int dir = rs.getInt("director_id");
        if (!rs.wasNull()) proyecto.setDirectorId(dir);

        int codir = rs.getInt("codirector_id");
        if (!rs.wasNull()) proyecto.setCodirectorId(codir);

        // textos
        proyecto.setObjetivoGeneral(rs.getString("objetivo_general"));
        proyecto.setObjetivosEspecificos(rs.getString("objetivos_especificos"));

        // ⚠️ aquí estaba el problema: mapear ambos estudiantes
        int e1 = rs.getInt("estudiante1_id");
        if (!rs.wasNull()) proyecto.setEstudiante1Id(e1);

        int e2 = rs.getInt("estudiante2_id");
        if (!rs.wasNull()) proyecto.setEstudiante2Id(e2);

        // estado
        String estado = rs.getString("estado");
        if (estado != null) {
            proyecto.setEstado(enumEstadoProyecto.valueOf(estado));
        }

        // intentos
        int intentos = rs.getInt("numero_intentos");
        if (!rs.wasNull()) proyecto.setNumeroIntentos(intentos);

        return proyecto;
    }



}