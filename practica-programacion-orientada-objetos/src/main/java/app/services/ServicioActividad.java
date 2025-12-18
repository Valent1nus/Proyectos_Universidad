package app.services;

import app.data.models.Actividad;
import app.data.repositories.ActividadRepository;
import app.services.ServicesException.NotFoundException;

import java.util.function.Function;

public class ServicioActividad {
    private final ActividadRepository actividadRepository;

    public ServicioActividad(ActividadRepository actividadRepository) {
        this.actividadRepository = actividadRepository;
    }

    public Actividad crearActividad(Actividad actividad) {
        return executeWithExceptionHandling(
                actividadRepository::create,
                actividad,
                "No se pudo crear la actividad"
        );
    }

    private <T, R> R executeWithExceptionHandling(
            Function<T, R> function,
            T input,
            String errorMessage
    ) {
        try {
            return function.apply(input);
        } catch (Exception e) {
            throw new NotFoundException(errorMessage);
        }
    }
}
