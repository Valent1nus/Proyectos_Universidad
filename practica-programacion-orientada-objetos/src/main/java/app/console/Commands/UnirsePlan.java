package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioUsuario;

public class UnirsePlan implements Command {
    private static final String NAME = "unirse-plan";
    private static final String HELP = ":<id-plan> Te permite unirte a un plan si estas logeado y hay aforo suficiente";
    private final ServicioUsuario servicioUsuario;
    private final View view;

    public UnirsePlan(View view, ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioUsuario.unirsePlan(usuarioLogueado, Integer.valueOf(values[0]));
            this.view.show("Te has unido al plan");
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
