package co.unicauca.gestiontrabajogrado.controller;

import co.unicauca.gestiontrabajogrado.domain.model.User;

public interface IDashBoardController {
    void openDocente(User user);
    void openEstudiante(User user);
    void openAdmin(User user);
    void openLogin();
}
