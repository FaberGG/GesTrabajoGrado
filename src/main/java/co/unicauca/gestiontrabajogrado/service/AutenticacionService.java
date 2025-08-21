package co.unicauca.gestiontrabajogrado.service;

import co.unicauca.gestiontrabajogrado.repository.IUsuarioRepository;
import co.unicauca.gestiontrabajogrado.model.Usuario;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;

public class AutenticacionService implements IAutenticacionService {
    private final IUsuarioRepository repo;
    private final PasswordHasher hasher;

    public AutenticacionService(IUsuarioRepository repo, PasswordHasher hasher) {
        this.repo = repo;
        this.hasher = hasher;
    }

    @Override
    public Usuario register(Usuario user, String plainPassword) {
        if (!EmailPolicy.isInstitutional(user.getEmail()))
            throw new IllegalArgumentException("El email debe ser institucional (@unicauca.edu.co)");
        if (!PasswordPolicy.isValid(plainPassword))
            throw new IllegalArgumentException("La contraseña no cumple la política (min 6, 1 dígito, 1 mayúscula, 1 especial)");
        if (repo.emailExists(user.getEmail()))
            throw new IllegalArgumentException("El email ya está registrado");

        user.setPasswordHash(hasher.hash(plainPassword));
        return repo.save(user);
    }

    @Override
    public Usuario login(String email, String plainPassword) {
        var opt = repo.findByEmail(email);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Credenciales inválidas");
        var u = opt.get();
        if (!hasher.verify(plainPassword, u.getPasswordHash()))
            throw new IllegalArgumentException("Credenciales inválidas");
        return u; // ya trae rol; tu UI carga menú según u.getRol()
    }
}
