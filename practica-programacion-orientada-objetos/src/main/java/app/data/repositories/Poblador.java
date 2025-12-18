package app.data.repositories;

import app.data.models.Actividad;
import app.data.models.Plan;
import app.data.models.TipoActividad.TipoCine;
import app.data.models.TipoActividad.TipoGenerica;
import app.data.models.TipoActividad.TipoTeatro;
import app.data.models.Usuario;
import app.data.repositories.repositoriesMap.ActividadRepositoryMap;
import app.data.repositories.repositoriesMap.PlanRepositoryMap;
import app.data.repositories.repositoriesMap.UsuarioRepositoryMap;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Poblador {
    private final UsuarioRepository usuarioRepository;
    private final ActividadRepository actividadRepository;
    private final PlanRepository planRepository;

    public Poblador() {
        this.usuarioRepository = new UsuarioRepositoryMap();
        this.actividadRepository = new ActividadRepositoryMap();
        this.planRepository = new PlanRepositoryMap();
    }

    public Poblador(UsuarioRepository usuarioRepository, ActividadRepository actividadRepository, PlanRepository planRepository) {
        this.usuarioRepository = usuarioRepository;
        this.actividadRepository = actividadRepository;
        this.planRepository = planRepository;
    }

    public void seed() {
        Usuario[] ListaUsuarios = {
                new Usuario("Roberto", 23, 666777333, "contrasenia123"),
                new Usuario("Valentin", 20, 545454545, "@1234@"),
                new Usuario("Carlos", 14, 555555555, "123"),
                new Usuario("Belen", 69, 121212121, "123123"),
                new Usuario("Stefan", 45, 345284038, "[]344jgesdg)**16]"),
                new Usuario("Pedro", 27, 652959410, "contraseÑÑÑÑa")
        };
        for (Usuario usuario : ListaUsuarios) {
            this.usuarioRepository.create(usuario);
        }

        List<Actividad> listaActividades = new ArrayList<>();
        TipoGenerica generica1 = TipoGenerica.builder().nombre("Lolsito").descripcion("Jugar a la liga de las leyendas").duracion(180).coste(0.0F).aforo(3).build();
        listaActividades.add(generica1);
        TipoGenerica generica2 = TipoGenerica.builder().nombre("Parque").descripcion("Caminar por el parque, escuchando El Nano de Melendi").duracion(120).coste(5.0F).aforo(30).build();
        listaActividades.add(generica2);
        TipoCine cine1 = TipoCine.builder().nombre("Oppenheimer").descripcion("Ver la peli de el abre Jaime").duracion(180).coste(23.1F).aforo(2).build();
        listaActividades.add(cine1);
        TipoCine cine2 = TipoCine.builder().nombre("Tiburón 3").descripcion("Ver la peli de un tiburón tres veces").duracion(65).coste(15.55F).aforo(40).build();
        listaActividades.add(cine2);
        TipoTeatro teatro1 = TipoTeatro.builder().nombre("Teatrín").descripcion("Ver lo que den en la sala 5").duracion(120).coste(34.0F).aforo(2).build();
        listaActividades.add(teatro1);
        TipoTeatro teatro2 = TipoTeatro.builder().nombre("Matilda").descripcion("Matilda el musical lalalalla").duracion(90).coste(33.0F).aforo(20).build();
        listaActividades.add(teatro2);
        for (Actividad actividad : listaActividades) {
            this.actividadRepository.create(actividad);
        }

        Plan[] ListaPlanes = {
                new Plan("Lo que surja", LocalDateTime.of(2024, 12, 1, 16, 33, 13, 5), "Parque", null),
                new Plan("Vicio extremo", LocalDateTime.of(2024, 12, 1, 16, 33, 13, 5), "Casita", 5),
                new Plan("Examen de POO", LocalDateTime.of(2024, 12, 1, 20, 33, 13, 5), "Clase", 23),
                new Plan("Visita a lugares mágicos", LocalDateTime.of(2024, 1, 1, 1, 30, 33, 33), "Entrada del pueblo", 2),
                new Plan("Cinesito con amigos y compadres", LocalDateTime.of(2025, 12, 1, 16, 33, 33, 33), "Cine local de CEVESA", null)
        };
        for (Plan plan : ListaPlanes) {
            this.planRepository.create(plan);
        }

        ListaPlanes[0].getActividadesDePlan().add(listaActividades.get(0));
        ListaPlanes[0].getActividadesDePlan().add(listaActividades.get(1));
        ListaPlanes[1].getActividadesDePlan().add(listaActividades.get(2));
        ListaPlanes[2].getActividadesDePlan().add(listaActividades.get(3));
        ListaPlanes[3].getActividadesDePlan().add(listaActividades.get(4));
        ListaPlanes[4].getActividadesDePlan().add(listaActividades.get(5));
        ListaPlanes[4].getActividadesDePlan().add(listaActividades.get(0));

        ListaPlanes[0].aniadirParticipante(ListaUsuarios[0]);
        ListaPlanes[1].aniadirParticipante(ListaUsuarios[1]);
        ListaPlanes[2].aniadirParticipante(ListaUsuarios[2]);
        ListaPlanes[3].aniadirParticipante(ListaUsuarios[3]);
        ListaPlanes[4].aniadirParticipante(ListaUsuarios[4]);
        ListaPlanes[0].aniadirParticipante(ListaUsuarios[5]);
        ListaPlanes[1].aniadirParticipante(ListaUsuarios[0]);


    }

    public UsuarioRepository getUsuarioRepository() {
        return usuarioRepository;
    }

    public ActividadRepository getActividadRepository() {
        return actividadRepository;
    }

    public PlanRepository getPlanRepository() {
        return planRepository;
    }
}

