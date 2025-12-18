package app.data.models;

import app.data.repositories.ActividadRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ActividadTest {
    private UsuarioRepository usuarioRepository;

    private ActividadRepository actividadRepository;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.usuarioRepository = poblador.getUsuarioRepository();
        this.actividadRepository = poblador.getActividadRepository();
    }

    @Test
    public void testAplicarDescuentoActividadUsuario() {
        assertEquals(17.0F, actividadRepository.read(5).get().aplicarDescuentoActividadUsuario(usuarioRepository.read(2).get().getEdad()));
        assertEquals(11.55F, actividadRepository.read(3).get().aplicarDescuentoActividadUsuario(usuarioRepository.read(2).get().getEdad()));
        assertEquals(10.20F, actividadRepository.read(5).get().aplicarDescuentoActividadUsuario(usuarioRepository.read(4).get().getEdad()), 0.0001F);
    }
}
