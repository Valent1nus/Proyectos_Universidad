package app.console;

import app.console.Commands.*;
import app.data.repositories.ActividadRepository;
import app.data.repositories.PlanRepository;
import app.data.repositories.Poblador;
import app.data.repositories.UsuarioRepository;
import app.data.repositories.repositoriesMap.ActividadRepositoryMap;
import app.data.repositories.repositoriesMap.PlanRepositoryMap;
import app.data.repositories.repositoriesMap.UsuarioRepositoryMap;
import app.services.ServicioActividad;
import app.services.ServicioPlan;
import app.services.ServicioUsuario;

public class DependencyInjector {
    private static DependencyInjector instance;
    private final ErrorHandler errorHandler;
    private final View view;
    private final CommandLineInterface commandLineInterface;
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;
    private final ActividadRepository actividadRepository;
    private final ServicioUsuario servicioUsuario;
    private final ServicioActividad servicioActividad;
    private final ServicioPlan servicioPlan;
    private final Poblador poblador;

    private DependencyInjector() {
        this.view = new View();
        this.usuarioRepository = new UsuarioRepositoryMap();
        this.planRepository = new PlanRepositoryMap();
        this.actividadRepository = new ActividadRepositoryMap();
        this.servicioUsuario = new ServicioUsuario(usuarioRepository, planRepository);
        this.servicioActividad = new ServicioActividad(actividadRepository);
        this.servicioPlan = new ServicioPlan(planRepository, actividadRepository);
        this.commandLineInterface = new CommandLineInterface(view);
        commandLineInterface.add(new AbandonarPlan(view, servicioUsuario));
        commandLineInterface.add(new AniadirActividadAPlan(view, servicioPlan));
        commandLineInterface.add(new BorrarPlan(view, servicioPlan));
        commandLineInterface.add(new CalcularCostePlan(view, servicioPlan));
        commandLineInterface.add(new CalcularTiempoPlan(view, servicioPlan));
        commandLineInterface.add(new CalificarPlan(view, servicioPlan));
        commandLineInterface.add(new CrearActividad(view, servicioActividad));
        commandLineInterface.add(new CrearPlan(view, servicioPlan));
        commandLineInterface.add(new CrearUsuario(view, servicioUsuario));
        commandLineInterface.add(new Login(view, servicioUsuario));
        commandLineInterface.add(new Logout(view));
        commandLineInterface.add(new UnirsePlan(view, servicioUsuario));
        commandLineInterface.add(new VerListaPlanesSubscritos(view, servicioUsuario));
        commandLineInterface.add(new VerPlanesConActividad(view, servicioPlan));
        commandLineInterface.add(new VerPlanesDisponiblesUsuario(view, servicioPlan));
        commandLineInterface.add(new VerPlanesMePuedoPermitir(view, servicioPlan));
        commandLineInterface.add(new VerPlanesOrdenadosPorPopulares(view, servicioPlan));
        this.errorHandler = new ErrorHandler(commandLineInterface, view);
        this.poblador = new Poblador(usuarioRepository, actividadRepository, planRepository);
        this.poblador.seed();
    }

    public static synchronized DependencyInjector getInstance() {
        if (instance == null) {
            instance = new DependencyInjector();
        }
        return instance;
    }

    public void run() {
        this.errorHandler.handlesErrors();
    }


    public View getView() {
        return this.view;
    }


    public ServicioPlan getServicioPlan() {
        return this.servicioPlan;
    }

    public Poblador getPoblador() {
        return poblador;
    }
}

