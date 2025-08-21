package co.unicauca.gestiontrabajogrado.service;
import co.unicauca.gestiontrabajogrado.model.Usuario;
public interface IAutenticacionService {
    Usuario register(Usuario user, String plainPassword);
    Usuario login(String email, String plainPassword);
}
