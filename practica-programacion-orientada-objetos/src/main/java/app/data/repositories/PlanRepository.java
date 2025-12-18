package app.data.repositories;

import app.data.models.Plan;
import app.data.models.Usuario;

import java.util.List;
import java.util.Optional;

public interface PlanRepository extends GenericRepository<Plan> {
    Optional<Plan> findById(Integer id);


    String estaEnElPlan(Plan plan, Usuario usuario);

    List<Plan> planesSubscritosDelUsuario(Usuario usuario);

    List<Plan> getPlanesDisponibles(Usuario usuario);

}
