package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;

public class Logout implements Command {

    private static final String NAME = "logout";
    private static final String HELP = ": Cierra sesión";
    private final View view;
    private Usuario usuarioLogueado;


    public Logout(View view) {
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuario) {
        if (values.length != 1) {
            throw new CommandErrorException(this.name() + this.help());
        }
        this.usuarioLogueado = null;
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
