package co.unicauca.gestiontrabajogrado.repository;

import co.unicauca.gestiontrabajogrado.service.AutenticacionService;
import co.unicauca.gestiontrabajogrado.service.IAutenticacionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import co.unicauca.gestiontrabajogrado.model.Usuario;
import co.unicauca.gestiontrabajogrado.model.Programa;
import co.unicauca.gestiontrabajogrado.model.Rol;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;



public class AuthSmokeTest {

    @Test
    public void registraYLogueaUsuario() {
        // Asegura tabla
        DatabaseInitializer.ensureCreated();

        IUsuarioRepository repo = new UsuarioRepository();
        PasswordHasher hasher = new PasswordHasher();
        IAutenticacionService auth = new AutenticacionService(repo, hasher);

        // REGISTRO
        Usuario nuevo = new Usuario();
        nuevo.setNombres("Sofia");
        nuevo.setApellidos("Moreno");
        nuevo.setCelular("3123456789");
        nuevo.setPrograma(Programa.INGENIERIA_DE_SISTEMAS);
        nuevo.setRol(Rol.ESTUDIANTE); // cámbialo a DOCENTE/ADMIN según el caso
        nuevo.setEmail("sofia@unicauca.edu.co");

        // Si ya existe el email, este registro lanzará IllegalArgumentException (único)
        Usuario creado = auth.register(nuevo, "ContraSegura#123");
        assertNotNull(creado.getId());

        // LOGIN OK
        Usuario logueado = auth.login("sofia@unicauca.edu.co", "ContraSegura#123");
        assertEquals(Rol.ESTUDIANTE, logueado.getRol());

        // LOGIN FAIL (contraseña mala)
        assertThrows(IllegalArgumentException.class,
                () -> auth.login("sofia@unicauca.edu.co", "mala#123"));
    }
}
