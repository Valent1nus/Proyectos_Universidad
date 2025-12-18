package app.data.models.ModelsExceptions;

public class ForbiddenArgumentException extends RuntimeException {
    private static final String MENSAJE = "Valor fuera de rango";

    public ForbiddenArgumentException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
