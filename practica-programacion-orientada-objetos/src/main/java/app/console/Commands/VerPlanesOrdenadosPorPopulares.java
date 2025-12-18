package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class VerPlanesOrdenadosPorPopulares implements Command {
    private static final String NAME = "ver-planes-ordenados-por-populares";
    private static final String HELP = ":   Ordena todas los planes creados según su popularidad (la popularidad aumenta cuando un usuario es añadido a un plan y disminuye cuando ese usuario es eliminado)";
    private final ServicioPlan servicioPlan;
    private final View view;

    public VerPlanesOrdenadosPorPopulares(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioPlan.verPlanesOrdenadasPorPopulares();
        } else {
            this.view.showError("Tiene que logearse primero");
        }
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String help() {
        return NAME + HELP;
    }

    @Override
    public Usuario getUsuarioLogueado() {
        return null;
    }
}