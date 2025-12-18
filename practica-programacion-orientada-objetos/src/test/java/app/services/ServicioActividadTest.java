package app.services;

import app.data.models.TipoActividad.TipoCine;
import app.data.models.TipoActividad.TipoGenerica;
import app.data.models.TipoActividad.TipoTeatro;
import app.data.repositories.ActividadRepository;
import app.data.repositories.Poblador;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServicioActividadTest {
    private ActividadRepository actividadRepository;
    private ServicioActividad servicioActividad;

    @BeforeEach
    public void setUp() {
        Poblador poblador = new Poblador();
        poblador.seed();
        this.actividadRepository = poblador.getActividadRepository();
        this.servicioActividad = new ServicioActividad(actividadRepository);
    }

    @Test
    public void testCrearActividades() {
        TipoGenerica generica = TipoGenerica.builder().nombre("Prueba genErica").descripcion("esto es una prueba").coste(100.0F).aforo(30).duracion(20).build();
        servicioActividad.crearActividad(generica);
        assertTrue(actividadRepository.findAll().contains(generica));
        TipoCine cine = TipoCine.builder().nombre("Prueba cine").descripcion("esto es una prueba").duracion(20).coste(100.0F).aforo(30).build();
        servicioActividad.crearActividad(cine);
        assertTrue(actividadRepository.findAll().contains(cine));
        TipoTeatro teatro = TipoTeatro.builder().nombre("Prueba teatro").descripcion("esto es una prueba").coste(100.0F).aforo(20).duracion(30).build();
        servicioActividad.crearActividad(teatro);
        assertTrue(actividadRepository.findAll().contains(teatro));
    }

}
