package app.data.repositories;

import app.data.models.Actividad;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class ActividadRepositoryTest {
    private ActividadRepository actividadRepository;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.actividadRepository = poblador.getActividadRepository();
    }

    @Test
    public void testFindById() {
        Optional<Actividad> actividad = this.actividadRepository.findById(1);
        assertTrue(actividad.isPresent());
        assertEquals("Lolsito", actividad.get().getNombre());
        assertEquals(3, actividad.get().getAforo());
    }

}
