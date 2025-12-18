package app.services;

import app.data.models.Plan;
import app.data.repositories.ActividadRepository;
import app.data.repositories.PlanRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class ServicioPlanTest {
    private PlanRepository planRepository;
    private UsuarioRepository usuarioRepository;
    private ActividadRepository actividadRepository;
    private ServicioPlan servicioPlan;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.planRepository = poblador.getPlanRepository();
        this.usuarioRepository = poblador.getUsuarioRepository();
        this.actividadRepository = poblador.getActividadRepository();
        this.servicioPlan = new ServicioPlan(planRepository, actividadRepository);
    }

    @Test
    public void testCrearYBorrarPlan() {
        Plan plan = new Plan("Prueba plan", LocalDateTime.now(), "En mi ordenador", 20);
        servicioPlan.crearPlan(plan, usuarioRepository.read(1).get());
        assertTrue(planRepository.findAll().contains(plan));
        servicioPlan.borrarPlan(6, usuarioRepository.read(1).get());
        assertFalse(planRepository.findAll().contains(plan));

    }

    @Test
    public void testAniadirActividad() {
        planRepository.read(1).get().setPropietario(usuarioRepository.read(1).get());
        servicioPlan.aniadirActividad(1, 3, 1);
        assertTrue(planRepository.read(1).get().getActividadesDePlan().contains(actividadRepository.read(3).get()));
    }

    @Test
    public void testCalcularTiempoPlan() {
        assertEquals(320, servicioPlan.calcularTiempoPlan(1));
    }

    @Test
    public void testCalcularCostePlanParaUsuario() {
        assertEquals(16.5f, servicioPlan.calcularCostePlanParaUsuario(usuarioRepository.read(2).get(), 5));
    }
}