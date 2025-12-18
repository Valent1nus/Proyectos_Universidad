package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class CalcularCostePlan implements Command {
    private static final String NAME = "calcular-coste-para-usuario";
    private static final String HELP = ":<id-plan> En caso de estar logeado calcula cuanto le va a costar el plan al usuario logeado";
    private final ServicioPlan servicioPlan;
    private final View view;

    public CalcularCostePlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.view.show(String.valueOf(this.servicioPlan.calcularCostePlanParaUsuario(usuarioLogueado, Integer.valueOf(values[0]))));
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
