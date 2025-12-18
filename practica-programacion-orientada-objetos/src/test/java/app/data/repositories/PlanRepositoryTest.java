package app.data.repositories;

import app.data.models.Plan;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class PlanRepositoryTest {
    private PlanRepository planRepository;
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.planRepository = poblador.getPlanRepository();
        this.usuarioRepository = poblador.getUsuarioRepository();
    }

    @Test
    public void testFindById() {
        Optional<Plan> plan = this.planRepository.findById(1);
        assertTrue(plan.isPresent());
        assertEquals("Lo que surja", plan.get().getNombre());
        assertEquals(Integer.MAX_VALUE, plan.get().getCapacidadMax());
    }

    @Test
    public void testEstaEnElPlan() {
        assertEquals("El usuario " + usuarioRepository.read(1).get().getNombre() + " sí participa en el plan", planRepository.estaEnElPlan(planRepository.read(1).get(), usuarioRepository.read(1).get()));
        assertEquals("El usuario " + usuarioRepository.read(2).get().getNombre() + " no participa en el plan", planRepository.estaEnElPlan(planRepository.read(1).get(), usuarioRepository.read(2).get()));
    }
}
