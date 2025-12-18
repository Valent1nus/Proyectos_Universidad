package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;


public class BorrarPlan implements Command {
    private static final String NAME = "borrar-plan";
    private static final String HELP = ":<id-plan> Elimina un plan del que se es propietario, en caso de estar logueado";
    private final ServicioPlan servicioPlan;
    private final View view;

    public BorrarPlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.servicioPlan.borrarPlan(Integer.valueOf(values[0]), usuarioLogueado);
            this.view.show("Has borrado el plan");
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