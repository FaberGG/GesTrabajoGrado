package co.unicauca.gestiontrabajogrado.repository;

import java.util.Optional;
import co.unicauca.gestiontrabajogrado.model.Usuario;

public interface IUsuarioRepository {
    boolean emailExists(String email);
    Usuario save(Usuario u);
    Optional<Usuario> findByEmail(String email);
}
