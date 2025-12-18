package app.data.repositories;

import app.data.models.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class UsuarioRepositoryTest {
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.usuarioRepository = poblador.getUsuarioRepository();
    }

    @Test
    public void testFindByMobile() {
        Optional<Usuario> usuario = this.usuarioRepository.findByMobile(666777333);
        assertEquals("Roberto", usuario.get().getNombre());
        assertEquals(23, usuario.get().getEdad());
    }

    @Test
    public void testFindByName() {
        Optional<Usuario> usuario = this.usuarioRepository.findByName("Roberto");
        assertEquals("Roberto", usuario.get().getNombre());
        assertEquals(23, usuario.get().getEdad());
    }
}
