package app.data.models;

import app.data.repositories.PlanRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UsuarioTest {
    private UsuarioRepository usuarioRepository;
    private PlanRepository planRepository;
    private Usuario usuario;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.usuarioRepository = poblador.getUsuarioRepository();
        this.planRepository = poblador.getPlanRepository();
        this.usuario = usuarioRepository.read(1).get();

    }

    @Test
    public void testComprobarSolapamientoDosPlanes() {
        assertTrue(usuario.comprobarSolapamientoDosPlanes(this.planRepository.read(1).get(), this.planRepository.read(2).get()));
        assertFalse(usuario.comprobarSolapamientoDosPlanes(this.planRepository.read(5).get(), this.planRepository.read(4).get()));
    }

}
