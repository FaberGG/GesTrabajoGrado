
package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.FormatoA;
import co.unicauca.gestiontrabajogrado.domain.model.enumEstadoFormato;
import java.util.List;
import java.util.Optional;

public interface IFormatoARepository {

    /**
     * Guarda un nuevo formato A o actualiza uno existente
     * @param formatoA El formato a guardar
     * @return El formato guardado con ID asignado
     */
    FormatoA save(FormatoA formatoA);
    /**
     * Actualiza un formato existente
     * @param formatoA El formato a actualizar
     * @return El formato actualizado
     */
    FormatoA update(FormatoA formatoA);
    /**
     * Busca un formato por su ID
     * @param id ID del formato
     * @return Optional con el formato si existe
     */
    Optional<FormatoA> findById(Integer id);

    /**
     * Obtiene todos los formatos de un proyecto específico
     * @param proyectoGradoId ID del proyecto
     * @return Lista de formatos ordenados por número de intento
     */
    List<FormatoA> findByProyectoGradoId(Integer proyectoGradoId);

    /**
     * Obtiene un formato específico por proyecto e intento
     * @param proyectoGradoId ID del proyecto
     * @param numeroIntento Número de intento
     * @return Optional con el formato si existe
     */
    Optional<FormatoA> findByProyectoGradoIdAndNumeroIntento(Integer proyectoGradoId, Integer numeroIntento);

    /**
     * Obtiene el último formato (mayor número de intento) de un proyecto
     * @param proyectoGradoId ID del proyecto
     * @return Optional con el último formato
     */
    Optional<FormatoA> findLastFormatoByProyectoId(Integer proyectoGradoId);

    /**
     * Obtiene formatos por estado
     * @param estado Estado del formato
     * @return Lista de formatos
     */
    List<FormatoA> findByEstado(enumEstadoFormato estado);

    /**
     * Obtiene formatos evaluados por un usuario específico
     * @param evaluadorId ID del evaluador
     * @return Lista de formatos
     */
    List<FormatoA> findByEvaluadoPor(Integer evaluadorId);

    /**
     * Cuenta el número de intentos de un proyecto
     * @param proyectoGradoId ID del proyecto
     * @return Número de intentos realizados
     */
    Integer countByProyectoGradoId(Integer proyectoGradoId);

    /**
     * Obtiene todos los formatos
     * @return Lista completa de formatos
     */
    List<FormatoA> findAll();

    /**
     * Elimina un formato por ID
     * @param id ID del formato
     */
    void deleteById(Integer id);

    /**
     * Elimina todos los formatos de un proyecto
     * @param proyectoGradoId ID del proyecto
     */
    void deleteByProyectoGradoId(Integer proyectoGradoId);
}
