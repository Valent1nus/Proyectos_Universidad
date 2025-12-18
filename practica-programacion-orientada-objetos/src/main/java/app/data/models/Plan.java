package app.data.models;

import app.data.models.ModelsExceptions.ForbiddenArgumentException;
import app.data.models.PlanExceptions.CapacityException;
import app.data.models.PlanExceptions.PuntuacionPlanException;

import java.time.LocalDateTime;
import java.util.*;

public class Plan {
    private static final int TIEMPO_ENTRE_ACTIVIDADES = 20;
    private static final int INICIO_ARRAY = 0;
    private static final int TAMANIO_MIN_ARRAY = 1;
    private static final int CALIFICACION_MENOR_QUE_LA_MINIMA = -1;
    private static final int CALIFICACION_MAYOR_QUE_LA_MAXIMA = 11;
    private final String nombre;
    private final LocalDateTime fechaYHora;
    private final String lugar;
    private Integer capacidadMax;
    private Usuario propietario;
    private Integer id;
    private final List<Usuario> usuariosSubscritos;
    private final Map<String, Integer> calificacionUsuariosSubscritos;
    private final List<Actividad> actividadesDePlan;

    public Plan(String nombre, LocalDateTime fechaYHora, String lugar, Integer capacidadMax) {
        this.nombre = nombre;
        this.fechaYHora = fechaYHora;
        this.lugar = lugar;
        setCapacidadMax(capacidadMax);
        this.usuariosSubscritos = new LinkedList<>();
        this.calificacionUsuariosSubscritos = new HashMap<>();
        this.actividadesDePlan = new ArrayList<>();
    }

    public Integer calcularTiempoPlan() {
        if (!this.getActividadesDePlan().isEmpty()) {
            int tiempoTotal = this.getActividadesDePlan().get(INICIO_ARRAY).getDuracion();
            if (this.getActividadesDePlan().size() > TAMANIO_MIN_ARRAY) {
                for (int posicionActividad = TAMANIO_MIN_ARRAY; posicionActividad < this.getActividadesDePlan().size(); posicionActividad++) {
                    tiempoTotal += this.getActividadesDePlan().get(posicionActividad).getDuracion() + TIEMPO_ENTRE_ACTIVIDADES;
                }
            }
            return tiempoTotal;
        }
        return 0;
    }

    public void aniadirParticipante(Usuario usuario) {
        if (this.usuariosSubscritos.size() < capacidadMax) {
            this.usuariosSubscritos.add(usuario);
        } else {
            throw new CapacityException("El plan ha alcanzado su máxima capacidad y no se le puede añadir");
        }
    }

    public void aniadirCalificaion(Usuario usuario, Integer calificaion) {
        if (this.usuariosSubscritos.contains(usuario)) {
            int indice = this.usuariosSubscritos.indexOf(usuario);
            if (indice == -1) {
                throw new PuntuacionPlanException("El usuario no ha participado en el plan");
            }
            if (this.calificacionUsuariosSubscritos.get(usuario.getNombre()) == null) {
                if (calificaion > CALIFICACION_MAYOR_QUE_LA_MAXIMA || calificaion < CALIFICACION_MENOR_QUE_LA_MINIMA) {
                    throw new ForbiddenArgumentException("La calificaión tiene que ser un número entre el 0 y el 10");
                }
                this.calificacionUsuariosSubscritos.put(usuario.getNombre(), calificaion);
            } else {
                throw new PuntuacionPlanException("El usuario ya puntuó el plan con un: " + this.calificacionUsuariosSubscritos.get(usuario.getNombre()));
            }
        }
    }

    public LocalDateTime getFechaYHora() {
        return fechaYHora;
    }

    public Integer getCapacidadMax() {
        return capacidadMax;
    }

    public void setCapacidadMax(Integer capacidadMax) {
        if (capacidadMax == null) {
            this.capacidadMax = Integer.MAX_VALUE;
        } else if (capacidadMax <= 0) {
            throw new CapacityException("La capacidad de un plan no puede ser negativa o cero");
        } else {
            this.capacidadMax = capacidadMax;
        }
    }

    public Usuario getPropietario() {
        return propietario;
    }

    public void setPropietario(Usuario propietario) {
        this.propietario = propietario;
    }

    public String getNombre() {
        return nombre;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<String, Integer> getCalificacionUsuariosSubscritos() {
        return calificacionUsuariosSubscritos;
    }

    public List<Usuario> getUsuariosSubscritos() {
        return usuariosSubscritos;
    }

    public List<Actividad> getActividadesDePlan() {
        return actividadesDePlan;
    }

    public List<String> getNombreActividades() {
        List<String> nombreActividades = new ArrayList<>();
        for (Actividad actividad : this.getActividadesDePlan()) {
            nombreActividades.add(actividad.getNombre());
        }
        return nombreActividades;
    }

    @Override
    public String toString() {
        return "Plan{" +
                "nombre='" + nombre + '\'' +
                ", fechaYHora=" + fechaYHora +
                ", lugar='" + lugar + '\'' +
                ", capacidadMax=" + capacidadMax +
                ", propietario=" + propietario +
                ", id=" + id + '\n' +
                ", actividades=" + getNombreActividades() + '\'' +
                " }";

    }
}

