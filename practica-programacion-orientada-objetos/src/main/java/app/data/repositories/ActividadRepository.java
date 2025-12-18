package app.data.repositories;

import app.data.models.Actividad;

import java.util.Optional;

public interface ActividadRepository extends GenericRepository<Actividad> {

    Optional<Actividad> findById(Integer id);
}
