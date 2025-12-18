package app.data.models.PlanExceptions;

public class CapacityException extends RuntimeException {
    private static final String MENSAJE = "Capacidad fuera de rango";

    public CapacityException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
