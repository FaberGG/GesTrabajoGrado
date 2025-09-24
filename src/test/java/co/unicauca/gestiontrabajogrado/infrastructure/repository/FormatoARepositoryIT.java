package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class FormatoARepositoryIT {

    FormatoARepository repoFA;
    ProyectoGradoRepository repoP;

    @BeforeAll
    static void boot() {
        // Usa un archivo real para este test
        System.setProperty("db.url", "jdbc:sqlite:target/itest-proyectos.db");
        co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer.ensureCreated();
        seedUsers();
    }



    @BeforeEach void setUp(){ repoFA = new FormatoARepository(); repoP = new ProyectoGradoRepository(); }

    static void seedUsers(){
        exec("DELETE FROM usuarios");
        exec("INSERT INTO usuarios(nombres,apellidos,email,enumProgram,enumRol,password) VALUES " +
                "('Doc','Uno','doc@u.edu','INGENIERIA_DE_SISTEMAS','DOCENTE','x')," +
                "('Est','A','ea@u.edu','INGENIERIA_DE_SISTEMAS','ESTUDIANTE','x')");
    }
    static void exec(String sql){
        try(Connection c=DriverManager.getConnection(System.getProperty("db.url"));
            Statement st=c.createStatement()){ st.executeUpdate(sql); }
        catch(Exception e){ throw new RuntimeException(e); }
    }

    private ProyectoGrado nuevoProyecto() {
        var p=new ProyectoGrado();
        p.setTitulo("PX");
        p.setModalidad(enumModalidad.PRACTICA_PROFESIONAL);
        p.setFechaCreacion(LocalDateTime.now());
        p.setDirectorId(1);
        p.setObjetivoGeneral("G");
        p.setObjetivosEspecificos("E");
        p.setEstudiante1Id(2);
        p.setEstado(enumEstadoProyecto.EN_PROCESO);
        p.setNumeroIntentos(1);
        return repoP.save(p);
    }

    @Test
    void unique_proyectoIntento_impideDuplicados() {
        var p = nuevoProyecto();

        var f1 = new FormatoA();
        f1.setProyectoGradoId(p.getId());
        f1.setNumeroIntento(1);
        f1.setRutaArchivo("/F1.pdf");
        f1.setNombreArchivo("F1.pdf");
        repoFA.save(f1);

        var f2 = new FormatoA();
        f2.setProyectoGradoId(p.getId());
        f2.setNumeroIntento(1);
        f2.setRutaArchivo("/F1b.pdf");
        f2.setNombreArchivo("F1b.pdf");

        assertThrows(IllegalArgumentException.class, () -> repoFA.save(f2));
    }

    @Test
    void findLastFormatoByProyectoId_devuelveMaxIntento() {
        var p = nuevoProyecto();

        for (int i=1; i<=3; i++) {
            var f = new FormatoA();
            f.setProyectoGradoId(p.getId());
            f.setNumeroIntento(i);
            f.setRutaArchivo("/F"+i+".pdf");
            f.setNombreArchivo("F"+i+".pdf");
            repoFA.save(f);
        }
        var last = repoFA.findLastFormatoByProyectoId(p.getId()).orElseThrow();
        assertEquals(3, last.getNumeroIntento());
    }
}
