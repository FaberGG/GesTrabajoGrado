package co.unicauca.gestiontrabajogrado.application;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;

public class LoginUser {
    private final IAutenticacionService service;

    public LoginUser(IAutenticacionService service) {
        this.service = service;
    }

    public User execute(String email, String password) {
        return service.login(email, password);
    }
}