package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import java.util.Optional;
import co.unicauca.gestiontrabajogrado.domain.model.User;

public interface IUserRepository {
    boolean emailExists(String email);
    User save(User u);
    Optional<User> findByEmail(String email);
    Optional<User> findById(Integer id);
    boolean existsById(Integer id);

}
