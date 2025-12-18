package app.data.repositories.repositoriesMap;

import app.data.models.Plan;
import app.data.models.Usuario;
import app.data.repositories.PlanRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PlanRepositoryMap extends GenericRepositoryMap<Plan> implements PlanRepository {
    private static final int INICIO_ARRAY = 0;

    @Override
    protected Integer getId(Plan entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Plan entity, Integer id) {
        entity.setId(id);
    }

    public Optional<Plan> findById(Integer id) {
        for (Plan plan : this.findAll()) {
            if (plan.getId() == id) {
                return Optional.of(plan);
            }
        }
        return Optional.empty();
    }


    public String estaEnElPlan(Plan plan, Usuario usuario) {
        boolean participa = false;
        for (int posicionUsuario = INICIO_ARRAY; !participa && posicionUsuario < plan.getUsuariosSubscritos().size(); posicionUsuario++) {
            if (plan.getUsuariosSubscritos().get(posicionUsuario).equals(usuario)) {
                participa = true;
                break;
            }
        }
        if (participa) {
            return "El usuario " + usuario.getNombre() + " sí participa en el plan";
        } else return "El usuario " + usuario.getNombre() + " no participa en el plan";
    }


    public List<Plan> planesSubscritosDelUsuario(Usuario usuario) {
        List<Plan> planesSubscritos = new ArrayList<>();
        for (Plan plan : findAll()) {
            if (plan.getUsuariosSubscritos().contains(usuario)) {
                planesSubscritos.add(plan);
            }
        }
        return planesSubscritos;
    }

    public List<Plan> getPlanesDisponibles(Usuario usuario) {
        List<Plan> planesDisponibles = new ArrayList<>();
        for (Plan plan : findAll()) {
            if (!plan.getUsuariosSubscritos().contains(usuario) && plan.getUsuariosSubscritos().size() < plan.getCapacidadMax()) {
                planesDisponibles.add(plan);
            }
        }
        return planesDisponibles;
    }
}
