package co.unicauca.gestiontrabajogrado.application;

import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.model.User;

public class RegisterUser {
    private final IAutenticacionService service;

    public RegisterUser(IAutenticacionService service) {
        this.service = service;
    }

    public User execute(User user, String password) {
        return service.register(user, password);
    }
}