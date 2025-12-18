package app.services.ServicioPlanException;

public class NotOwnerException extends RuntimeException {
    private static final String MENSAJE = "No eres el creador";

    public NotOwnerException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
