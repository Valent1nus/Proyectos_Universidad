package app.data.models;

import app.data.repositories.PlanRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class PlanTest {
    private UsuarioRepository usuarioRepository;
    private PlanRepository planRepository;
    private Usuario usuario;
    private Plan plan;


    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.usuarioRepository = poblador.getUsuarioRepository();
        this.planRepository = poblador.getPlanRepository();
        this.usuario = usuarioRepository.read(2).get();
        this.plan = planRepository.read(1).get();
    }

    @Test
    public void testCalcularTiempoPlan() {
        Integer tiempoTotal = planRepository.read(1).get().calcularTiempoPlan();
        assertNotNull(tiempoTotal);
        assertEquals(320, tiempoTotal);
    }

    @Test
    public void testAniadirParticipante() {
        assertEquals(2, planRepository.read(1).get().getUsuariosSubscritos().size());
        planRepository.read(1).get().aniadirParticipante(usuario);
        assertEquals(3, planRepository.read(1).get().getUsuariosSubscritos().size());
        assertTrue(planRepository.read(1).get().getUsuariosSubscritos().contains(usuario));
    }

    @Test
    public void testAniadirCalificacion() {
        plan.aniadirCalificaion(usuario, 10);
        Map<String, Integer> aux = planRepository.read(1).get().getCalificacionUsuariosSubscritos();
        assertEquals(aux, planRepository.read(1).get().getCalificacionUsuariosSubscritos());
    }
}
