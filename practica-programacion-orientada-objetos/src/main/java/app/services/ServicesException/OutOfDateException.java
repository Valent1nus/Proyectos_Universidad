package app.services.ServicesException;

public class OutOfDateException extends RuntimeException {
    private static final String MENSAJE = "La fecha no es valida";

    public OutOfDateException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
