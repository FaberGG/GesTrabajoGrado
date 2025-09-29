package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;
import co.unicauca.gestiontrabajogrado.util.IEmailPolicy;
import co.unicauca.gestiontrabajogrado.util.IPasswordPolicy;

public class AutenticacionService implements IAutenticacionService {
    private final IUserRepository repo;
    private final PasswordHasher hasher;
    private final IEmailPolicy emailPolicy;
    private final IPasswordPolicy passwordPolicy;

    public AutenticacionService(
        IUserRepository repo,
        PasswordHasher hasher,
        IEmailPolicy emailPolicy,
        IPasswordPolicy passwordPolicy
    ) {
        this.repo = repo;
        this.hasher = hasher;
        this.emailPolicy = emailPolicy;
        this.passwordPolicy = passwordPolicy;
    }

    @Override
    public User register(User user, String plainPassword) {
        if (!emailPolicy.isInstitutional(user.getEmail()))
            throw new IllegalArgumentException("El email debe ser institucional (@unicauca.edu.co)");
        if (!passwordPolicy.isValid(plainPassword))
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
    //    if (!hasher.verify(plainPassword, u.getPasswordHash()))
      //      throw new IllegalArgumentException("Credenciales inválidas");
        return u; // ya trae rol; tu UI carga menú según u.getRol()
    }
}
