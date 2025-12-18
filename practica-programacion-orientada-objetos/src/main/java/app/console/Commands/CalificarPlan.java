package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class CalificarPlan implements Command {
    private static final String NAME = "calificar-plan";
    private static final String HELP = ":<id-plan>;<calificación> Permite dar una calificación al plan si Ud. ha participado en este";
    private final ServicioPlan servicioPlan;
    private final View view;

    public CalificarPlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 2) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioPlan.calificarPlan(usuarioLogueado, Integer.valueOf(values[0]), Integer.valueOf(values[1]));
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
