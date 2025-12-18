package app.services;

import app.data.models.Usuario;
import app.data.repositories.PlanRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class ServicioUsuarioTest {
    private PlanRepository planRepository;
    private UsuarioRepository usuarioRepository;
    private ServicioUsuario servicioUsuario;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.planRepository = poblador.getPlanRepository();
        this.usuarioRepository = poblador.getUsuarioRepository();
        this.servicioUsuario = new ServicioUsuario(usuarioRepository, planRepository);
    }

    @Test
    public void testCrearUsuario() {
        Usuario usuario = new Usuario("Prueba usuario", 20, 687456321, "pruebas");
        servicioUsuario.crearUsuario(usuario);
        assertTrue(usuarioRepository.findAll().contains(usuario));
    }

    @Test
    public void testUnirseYAbandonarPlan() {
        servicioUsuario.unirsePlan(usuarioRepository.read(1).get(), 5);
        assertTrue(planRepository.findById(5).get().getUsuariosSubscritos().contains(usuarioRepository.read(1).get()));
        servicioUsuario.abandonarPlan(usuarioRepository.read(1).get(), 5);
        assertFalse(planRepository.findById(5).get().getUsuariosSubscritos().contains(usuarioRepository.read(1).get()));
    }
}
