package app.console.Commands;

import app.console.AlreadyLoguedException.AlreadyLoguedException;
import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioUsuario;

public class Login implements Command {
    private static final String NAME = "login";
    private static final String HELP = ":<nombre-usuario>;<contrasenia> Inicia sesión un usuario que tenga una cuenta";
    private final ServicioUsuario servicioUsuario;
    private final View view;
    private Usuario usuarioLogueado;


    public Login(View view, ServicioUsuario servicioUsuario) {
        this.servicioUsuario = servicioUsuario;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuario) {
        if (values.length != 2) {
            throw new CommandErrorException(this.name() + this.help());
        }
        if (usuario != null) {
            throw new AlreadyLoguedException("Haga logout si quiere loguearse");
        }
        this.usuarioLogueado = this.servicioUsuario.loginUsuario(values[0], values[1]);
        this.view.show(usuarioLogueado.toString());
    }

    public Usuario getUsuarioLogueado() {
        return this.usuarioLogueado;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public String help() {
        return NAME + HELP;
    }
}
