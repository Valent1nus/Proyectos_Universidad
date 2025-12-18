package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioUsuario;

public class CrearUsuario implements Command {
    private static final String NAME = "crear-usuario";
    private static final String HELP = ":<nombre>;<edad>;<movil>;<contrasenia> Crea un usuario, al crearlo NO te logueas";
    private final View view;
    private final ServicioUsuario servicioUsuario;

    public CrearUsuario(View view, ServicioUsuario servicioUsuario) {
        this.view = view;
        this.servicioUsuario = servicioUsuario;
    }

    @Override
    public void execute(String[] values, Usuario usuario) {
        if (values.length != 4) {
            throw new CommandErrorException(this.name() + this.help());
        }
        Usuario usuarioCreado = new Usuario(values[0], Integer.valueOf(values[1]), Integer.valueOf(values[2]), values[3]);
        servicioUsuario.crearUsuario(usuarioCreado);
        this.view.show(usuarioCreado.toString());
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
