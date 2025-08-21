package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.EmailPolicy;
import co.unicauca.gestiontrabajogrado.util.PasswordPolicy;

public class AutenticacionService implements IAutenticacionService {
    private final IUserRepository repo;
    private final PasswordHasher hasher;

    public AutenticacionService(IUserRepository repo, PasswordHasher hasher) {
        this.repo = repo;
        this.hasher = hasher;
    }

    @Override
    public User register(User user, String plainPassword) {
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
    public User login(String email, String plainPassword) {
        var opt = repo.findByEmail(email);
        if (opt.isEmpty())
            throw new IllegalArgumentException("Credenciales inválidas");
        var u = opt.get();
        if (!hasher.verify(plainPassword, u.getPasswordHash()))
            throw new IllegalArgumentException("Credenciales inválidas");
        return u; // ya trae rol; tu UI carga menú según u.getRol()
    }
}
