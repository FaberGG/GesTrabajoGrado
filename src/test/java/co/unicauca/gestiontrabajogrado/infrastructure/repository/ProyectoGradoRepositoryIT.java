package co.unicauca.gestiontrabajogrado.infrastructure.repository;

import co.unicauca.gestiontrabajogrado.domain.model.*;
import co.unicauca.gestiontrabajogrado.infrastructure.database.DatabaseInitializer;
import org.junit.jupiter.api.*;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ProyectoGradoRepositoryIT {

    ProyectoGradoRepository repo;

    @BeforeAll
    static void boot() {
        System.setProperty("db.url", "jdbc:sqlite:target/itest-proyectos.db"); // o itest-formatos.db
        DatabaseInitializer.ensureCreated();
        seedUsers();
    }



    @BeforeEach void setUp(){ repo = new ProyectoGradoRepository(); }

    static void seedUsers(){
        exec("DELETE FROM usuarios");
        exec("INSERT INTO usuarios(nombres,apellidos,email,enumProgram,enumRol,password) VALUES " +
                "('Doc','Uno','doc@u.edu','INGENIERIA_DE_SISTEMAS','DOCENTE','x')," +
                "('Est','A','ea@u.edu','INGENIERIA_DE_SISTEMAS','ESTUDIANTE','x')," +
                "('Est','B','eb@u.edu','INGENIERIA_DE_SISTEMAS','ESTUDIANTE','x')");
    }
    static void exec(String sql){
        try(Connection c=DriverManager.getConnection(System.getProperty("db.url"));
            Statement st=c.createStatement()){ st.executeUpdate(sql); }
        catch(Exception e){ throw new RuntimeException(e); }
    }

    @Test
    void insert_y_findByEstudianteId_mapeaAmbosEstudiantes() {
        var p = new ProyectoGrado();
        p.setTitulo("T1");
        p.setModalidad(enumModalidad.INVESTIGACION);
        p.setFechaCreacion(LocalDateTime.now());
        p.setDirectorId(1);
        p.setObjetivoGeneral("G");
        p.setObjetivosEspecificos("E");
        p.setEstudiante1Id(2);
        p.setEstudiante2Id(3);
        p.setEstado(enumEstadoProyecto.EN_PROCESO);
        p.setNumeroIntentos(1);

        p = repo.save(p);
        assertNotNull(p.getId());

        List<ProyectoGrado> porE1 = repo.findByEstudianteId(2);
        List<ProyectoGrado> porE2 = repo.findByEstudianteId(3);

        assertFalse(porE1.isEmpty());
        assertFalse(porE2.isEmpty());
        assertEquals(p.getId(), porE1.get(0).getId());
        assertEquals(p.getId(), porE2.get(0).getId());
    }

    @Test
    void update_actualizaSegundoEstudiante() {
        var p = new ProyectoGrado();
        p.setTitulo("T2");
        p.setModalidad(enumModalidad.INVESTIGACION);
        p.setFechaCreacion(LocalDateTime.now());
        p.setDirectorId(1);
        p.setObjetivoGeneral("G");
        p.setObjetivosEspecificos("E");
        p.setEstudiante1Id(2);
        p.setEstado(enumEstadoProyecto.EN_PROCESO);
        p.setNumeroIntentos(1);
        p = repo.save(p);

        p.setEstudiante2Id(3);
        repo.update(p);

        var rec = repo.findById(p.getId()).orElseThrow();
        assertEquals(3, rec.getEstudiante2Id());
    }
}
