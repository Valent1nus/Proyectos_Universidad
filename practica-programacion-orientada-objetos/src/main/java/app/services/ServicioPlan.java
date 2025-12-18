package app.services;

import app.data.models.Actividad;
import app.data.models.Plan;
import app.data.models.Usuario;
import app.data.repositories.ActividadRepository;
import app.data.repositories.PlanRepository;
import app.services.ServicesException.NotFoundException;
import app.services.ServicesException.OutOfDateException;
import app.services.ServicesException.UserCapacityException;
import app.services.ServicioPlanException.AlreadyRatedException;
import app.services.ServicioPlanException.NotOwnerException;
import app.services.ServicioPlanException.NotParticipantException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class ServicioPlan {
    private static final int TIEMPO_ENTRE_ACTIVIDADES = 20;
    private final PlanRepository planRepository;
    private final ActividadRepository actividadRepository;

    public ServicioPlan(PlanRepository planRepository, ActividadRepository actividadRepository) {
        this.planRepository = planRepository;
        this.actividadRepository = actividadRepository;
    }

    public Plan crearPlan(Plan plan, Usuario usuarioCreador) {
        if (plan.getCapacidadMax() == null) {
            plan.setCapacidadMax(Integer.MAX_VALUE);
        }
        plan.setPropietario(usuarioCreador);
        return planRepository.create(plan);
    }

    public void borrarPlan(Integer idPlan, Usuario usuarioCreador) {
        Plan plan = planRepository.findById(idPlan)
                .filter(p -> p.getPropietario() != null && p.getPropietario().equals(usuarioCreador))
                .orElseThrow(() -> new NotOwnerException("Ud. no es el usuario creador del plan"));
        planRepository.deleteById(idPlan);
    }

    public void calificarPlan(Usuario usuarioLog, Integer idPlan, Integer calificacion) {
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new NotFoundException("El plan que desea calificar no existe"));
        if (!plan.getUsuariosSubscritos().contains(usuarioLog)) {
            throw new NotParticipantException("No puede puntuar un plan en el que no ha participado");
        }
        if (plan.getCalificacionUsuariosSubscritos().containsKey(usuarioLog)) {
            throw new AlreadyRatedException("No puede puntuar un plan dos veces");
        }
        if (LocalDateTime.now().isBefore(plan.getFechaYHora())) {
            throw new OutOfDateException("El plan que desea calificar aún no se ha realizado");
        }
        plan.aniadirCalificaion(usuarioLog, calificacion);
    }


    public void aniadirActividad(Integer idPlan, Integer idActividad, Integer idUsuarioCreador) {
        Plan plan = planRepository.findById(idPlan)
                .filter(p -> p.getPropietario() != null && p.getPropietario().getId() == idUsuarioCreador)
                .orElseThrow(() -> new NotOwnerException("No puede modificar el plan ya que no es creador"));
        Actividad actividad = actividadRepository.findById(idActividad)
                .orElseThrow(() -> new NotFoundException("No puede añadir una actividad que no existe"));
        if (plan.getUsuariosSubscritos().size() > actividad.getAforo()) {
            throw new UserCapacityException("Hay más usuarios subscritos de los que puede albergar la actividad");
        }

        if (actividad.getAforo() < plan.getCapacidadMax()) {
            plan.setCapacidadMax(actividad.getAforo());
        }
        plan.getActividadesDePlan().add(actividad);
        planRepository.update(plan);
    }

    public Integer calcularTiempoPlan(Integer idPlan) {
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new NotFoundException("No puede calcular el tiempo de un plan que no existe"));
        return plan.getActividadesDePlan().stream()
                .mapToInt(Actividad::getDuracion)
                .sum() + (plan.getActividadesDePlan().size() - 1) * TIEMPO_ENTRE_ACTIVIDADES;
    }

    public Float calcularCostePlanParaUsuario(Usuario usuario, Integer idPlan) {
        Plan plan = planRepository.findById(idPlan)
                .orElseThrow(() -> new NotFoundException("No puede calcular el coste de un plan que no existe"));
        return plan.getActividadesDePlan().stream()
                .map(actividad -> actividad.aplicarDescuentoActividadUsuario(usuario.getEdad()))
                .reduce(0.0F, Float::sum);
    }

    public void verPlanesDisponibles(Usuario usuario) {
        List<Plan> planesDisponibles = planRepository.getPlanesDisponibles(usuario).stream()
                .filter(plan -> LocalDateTime.now().isBefore(plan.getFechaYHora()))
                .collect(Collectors.toList());
        planesDisponibles.forEach(plan -> System.out.println(plan.toString()));
    }

    public void verPlanesMePuedoPermitir(Float costePermitir, Usuario usuario) {
        List<Plan> planesDisponibles = planRepository.findAll().stream()
                .filter(plan -> calcularCostePlanParaUsuario(usuario, plan.getId()) != null &&
                        calcularCostePlanParaUsuario(usuario, plan.getId()) <= costePermitir)
                .collect(Collectors.toList());
        planesDisponibles.forEach(plan -> System.out.println(plan.toString()));
    }

    public void verPlanesConActividad(Integer idActividad) {
        actividadRepository.findById(idActividad).ifPresent(actividad -> {
            List<Plan> planesDisponibles = planRepository.findAll().stream()
                    .filter(plan -> plan.getActividadesDePlan().contains(actividad))
                    .collect(Collectors.toList());
            planesDisponibles.forEach(plan -> System.out.println(plan.toString()));
        });
    }

    public void verPlanesOrdenadasPorPopulares() {
        List<Plan> listaTodo = planRepository.findAll();
        listaTodo.sort((p1, p2) -> Integer.compare(p2.getUsuariosSubscritos().size(), p1.getUsuariosSubscritos().size()));
        listaTodo.forEach(plan -> System.out.println(plan.toString()));
    }
}
