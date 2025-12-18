package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Usuario;
import app.services.ServicioPlan;

public class CalcularTiempoPlan implements Command {
    private static final String NAME = "calcular-tiempo-plan";
    private static final String HELP = ":<id-plan> En caso de estar logueado calcula el tiempo en minutos de un plan";
    private final ServicioPlan servicioPlan;
    private final View view;

    public CalcularTiempoPlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 1) {
                throw new CommandErrorException(this.name() + this.help());
            }
            this.view.show(String.valueOf(this.servicioPlan.calcularTiempoPlan(Integer.valueOf(values[0]))));
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
