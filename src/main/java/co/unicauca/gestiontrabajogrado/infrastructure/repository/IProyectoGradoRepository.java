
package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.ProyectoGrado;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoProyecto;
import java.util.List;
import java.util.Optional;

public interface IProyectoGradoRepository {

    /**
     * Guarda un nuevo proyecto de grado o actualiza uno existente
     * @param proyectoGrado El proyecto a guardar
     * @return El proyecto guardado con ID asignado
     */
    ProyectoGrado save(ProyectoGrado proyectoGrado);
    /**
     * Actualiza un proyecto existente
     * @param proyectoGrado El proyecto a actualizar
     * @return El proyecto actualizado
     */
    ProyectoGrado update(ProyectoGrado proyectoGrado);

    /**
     * Busca un proyecto por su ID
     * @param id ID del proyecto
     * @return Optional con el proyecto si existe
     */
    Optional<ProyectoGrado> findById(Integer id);

    /**
     * Obtiene todos los proyectos donde el usuario es director
     * @param directorId ID del docente director
     * @return Lista de proyectos
     */
    List<ProyectoGrado> findByDirectorId(Integer directorId);

    /**
     * Obtiene todos los proyectos donde el usuario es codirector
     * @param codirectorId ID del docente codirector
     * @return Lista de proyectos
     */
    List<ProyectoGrado> findByCodirectorId(Integer codirectorId);

    /**
     * Obtiene proyectos por estudiante
     * @param estudianteId ID del estudiante
     * @return Lista de proyectos
     */
    List<ProyectoGrado> findByEstudianteId(Integer estudianteId);
    /**
     * Obtiene todos los proyectos donde el docente participa como director o codirector
     * @param docenteId ID del docente
     * @return Lista de proyectos donde participa
     */
    List<ProyectoGrado> findByDocente(Integer docenteId);

    /**
     * Obtiene proyectos por estado
     * @param estado Estado del proyecto
     * @return Lista de proyectos
     */
    List<ProyectoGrado> findByEstado(enumEstadoProyecto estado);

    /**
     * Busca proyectos por título (búsqueda parcial)
     * @param titulo Título o parte del título
     * @return Lista de proyectos coincidentes
     */
    List<ProyectoGrado> findByTituloContaining(String titulo);

    /**
     * Cuenta el número de proyectos por director
     * @param directorId ID del director
     * @return Número de proyectos
     */
    long countByDirectorId(Integer directorId);

    /**
     * Obtiene todos los proyectos
     * @return Lista completa de proyectos
     */
    List<ProyectoGrado> findAll();

    /**
     * Elimina un proyecto por ID
     * @param id ID del proyecto
     */
    void deleteById(Integer id);

    /**
     * Verifica si existe un proyecto con el ID dado
     * @param id ID del proyecto
     * @return true si existe
     */
    boolean existsById(Integer id);
}