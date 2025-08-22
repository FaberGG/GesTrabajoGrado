package co.unicauca.gestiontrabajogrado.repository;

import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.IUserRepository;
import co.unicauca.gestiontrabajogrado.infrastructure.repository.UserRepository;
import co.unicauca.gestiontrabajogrado.domain.service.AutenticacionService;
import co.unicauca.gestiontrabajogrado.domain.service.IAutenticacionService;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import co.unicauca.gestiontrabajogrado.domain.model.User;
import co.unicauca.gestiontrabajogrado.domain.model.enumProgram;
import co.unicauca.gestiontrabajogrado.domain.model.enumRol;
import co.unicauca.gestiontrabajogrado.util.PasswordHasher;



public class AuthSmokeTest {

//    @Test
//    public void registraYLogueaUsuario() {
//        // Asegura tabla
//        DatabaseInitializer.ensureCreated();
//
//        IUserRepository repo = new UserRepository();
//        PasswordHasher hasher = new PasswordHasher();
//        IAutenticacionService auth = new AutenticacionService(repo, hasher);
//
//        // REGISTRO
//        User nuevo = new User();
//        nuevo.setNombres("Sofia");
//        nuevo.setApellidos("Moreno");
//        nuevo.setCelular("3123456789");
//        nuevo.setPrograma(enumProgram.INGENIERIA_DE_SISTEMAS);
//        nuevo.setRol(enumRol.ESTUDIANTE); // cámbialo a DOCENTE/ADMIN según el caso
//        nuevo.setEmail("sofia3@unicauca.edu.co");
//
//        // Si ya existe el email, este registro lanzará IllegalArgumentException (único)
//        User creado = auth.register(nuevo, "ContraSegura#123");
//        assertNotNull(creado.getId());
//
//        // LOGIN OK
//        User logueado = auth.login("sofia3@unicauca.edu.co", "ContraSegura#123");
//        assertEquals(enumRol.ESTUDIANTE, logueado.getRol());
//
//        // LOGIN FAIL (contraseña mala)
//        assertThrows(IllegalArgumentException.class,
//                () -> auth.login("sofia3@unicauca.edu.co", "mala#123"));
//    }
}
