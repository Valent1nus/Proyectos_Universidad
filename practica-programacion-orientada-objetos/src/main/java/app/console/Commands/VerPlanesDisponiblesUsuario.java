package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class VerPlanesDisponiblesUsuario implements Command {
    private static final String NAME = "ver-lista-planes-disponibles";
    private static final String HELP = ": Mira planes a los que se puede subscribir el usuario logeado";
    private final ServicioPlan servicioPlan;
    private final View view;

    public VerPlanesDisponiblesUsuario(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioPlan.verPlanesDisponibles(usuarioLogueado);
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