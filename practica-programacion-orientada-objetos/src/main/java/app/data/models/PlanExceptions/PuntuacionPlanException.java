package app.data.models.PlanExceptions;

public class PuntuacionPlanException extends RuntimeException {
    private static final String MENSAJE = "No se puede puntuar";

    public PuntuacionPlanException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
