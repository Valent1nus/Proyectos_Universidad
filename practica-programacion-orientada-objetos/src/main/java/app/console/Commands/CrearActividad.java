package app.console.Commands;

import app.console.Command;
import app.console.CommandLineInterfaceException.CommandErrorException;
import app.console.View;
import app.data.models.Actividad;
import app.data.models.TipoActividad.TipoCine;
import app.data.models.TipoActividad.TipoGenerica;
import app.data.models.TipoActividad.TipoTeatro;
import app.data.models.Usuario;
import app.services.ServicioActividad;


public class CrearActividad implements Command {
    private static final String NAME = "crear-actividad";
    private static final String HELP = ":<nombre>;<descripcion>;<duracion>;<coste>;<tipo>;<aforo> Creas una actividad si estas logueado, el aforo es opcional";
    private final ServicioActividad servicioActividad;
    private final View view;

    public CrearActividad(View view, ServicioActividad servicioActividad) {
        this.servicioActividad = servicioActividad;
        this.view = view;
    }

    @Override
    public void execute(String[] values, Usuario usuarioLogueado) {
        if (usuarioLogueado != null) {
            if (values.length != 6 && values.length != 5) {
                throw new CommandErrorException(this.name() + this.help());
            }
            Integer aforo;
            if (values.length == 5) {
                aforo = null;
            } else {
                aforo = Integer.valueOf(values[5]);
            }
            if (values[4].equals("Cine")) {
                Actividad actividadCreada = this.servicioActividad.crearActividad(TipoCine.builder().nombre(values[0]).descripcion(values[1]).duracion(Integer.valueOf(values[2])).coste(Float.valueOf(values[3])).aforo(aforo).build());
                this.view.show(actividadCreada.toString());
            } else if (values[4].equals("Teatro")) {
                Actividad actividadCreada = this.servicioActividad.crearActividad(TipoTeatro.builder().nombre(values[0]).descripcion(values[1]).duracion(Integer.valueOf(values[2])).coste(Float.valueOf(values[3])).aforo(aforo).build());
                this.view.show(actividadCreada.toString());
            } else if (values[4].equals("Generica")) {
                Actividad actividadCreada = this.servicioActividad.crearActividad(TipoGenerica.builder().nombre(values[0]).descripcion(values[1]).duracion(Integer.valueOf(values[2])).coste(Float.valueOf(values[3])).aforo(aforo).build());
                this.view.show(actividadCreada.toString());
            } else {
                this.view.showError("El tipo solo puede ser: Generica/Teatro/Cine");
            }
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
