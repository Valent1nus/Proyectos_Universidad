package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.DateBeforeNowException.DateBeforeNowException;
import app.console.View;
import app.data.models.Plan;
import app.data.models.Usuario;
import app.services.ServicioPlan;

import java.time.LocalDateTime;

public class CrearPlan implements Command {
    private static final String NAME = "crear-plan";
    private static final String HELP = ":<nombre>;<lugar>;<DD-MM-AAAA>;<hh-mm-ss>;<capacidad-máxima> Creas un plan y te declaras como propietario del plan en caso de estar logueado, no te unes automáticamente a ese plan. Si no indica un máximo, será ilimitado";
    private final ServicioPlan servicioPlan;
    private final View view;

    public CrearPlan(View view, ServicioPlan servicioPlan) {
        this.servicioPlan = servicioPlan;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 5 && values.length != 4) {
                throw new CommandErrorException(this.name() + this.help());
            }
            Integer capacidadMax;
            if (values.length == 4) {
                capacidadMax = null;
            } else {
                capacidadMax = Integer.valueOf(values[4]);
            }
            String[] fecha = values[2].split("-");
            String[] hora = values[3].split("-");
            LocalDateTime fechaYhora = LocalDateTime.of(Integer.valueOf(fecha[2]), Integer.valueOf(fecha[1]), Integer.valueOf(fecha[0]), Integer.valueOf(hora[0]), Integer.valueOf(hora[1]), Integer.valueOf(hora[2]), 0);
            if (fechaYhora.isBefore(LocalDateTime.now())) {
                throw new DateBeforeNowException("Introduzca una fecha posterior");
            }
            Plan planCreado = this.servicioPlan.crearPlan(new Plan(values[0], fechaYhora, values[1], capacidadMax), usuarioLogueado);
            this.view.show(planCreado.toString());
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
