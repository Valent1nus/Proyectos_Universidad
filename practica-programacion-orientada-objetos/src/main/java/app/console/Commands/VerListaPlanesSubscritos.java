package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioUsuario;

public class VerListaPlanesSubscritos implements Command {
    private static final String NAME = "ver-lista-planes-subscritos";
    private static final String HELP = ": Muestra los planes a los que el usuario logueado está subscrito";
    private final ServicioUsuario servicioUsuario;
    private final View view;

    public VerListaPlanesSubscritos(View view, ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioUsuario.verListaPlanesSubscritos(usuarioLogueado);
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
