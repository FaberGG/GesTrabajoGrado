package co.unicauca.gestiontrabajogrado.domain.service;

import co.unicauca.gestiontrabajogrado.domain.model.User;

public interface IAutenticacionService {
    User register(User user, String plainPassword);
    User login(String email, String plainPassword);
}
