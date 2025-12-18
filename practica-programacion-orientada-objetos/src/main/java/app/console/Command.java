package app.console;

import app.data.models.Usuario;

public interface Command {
    void execute(String[] values, Usuario usuarioLogueado);

    String name();

    String help();

    Usuario getUsuarioLogueado();
}
