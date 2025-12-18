package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class AniadirActividadAPlan implements Command {
    private static final String NAME = "aniadir-actividad-a-plan";
    private static final String HELP = ":<id-plan>;<id-actividad> En caso de ser el propietario de un plan y estar logueado añade una actividad a un plan";
    private final ServicioPlan servicioPlan;
    private final View view;

    public AniadirActividadAPlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 2) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioPlan.aniadirActividad(Integer.valueOf(values[0]), Integer.valueOf(values[1]), usuarioLogueado.getId());
            this.view.show("Has añadido la actividad al plan");
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
