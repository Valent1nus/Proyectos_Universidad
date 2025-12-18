package app.data.repositories.repositoriesMap;

import app.data.models.Actividad;
import app.data.repositories.ActividadRepository;

import java.util.Optional;

public class ActividadRepositoryMap extends GenericRepositoryMap<Actividad> implements ActividadRepository {


    @Override
    protected Integer getId(Actividad entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Actividad entity, Integer id) {
        entity.setId(id);
    }


    public Optional<Actividad> findById(Integer id) {
        for (Actividad actividad : this.findAll()) {
            if (actividad.getId().equals(id)) {
                return Optional.of(actividad);
            }
        }
        return Optional.empty();
    }
}