package app.services.ServicioPlanException;

public class AlreadyRatedException extends RuntimeException {
    private static final String MENSAJE = "Este plan ya ha sido puntuado";

    public AlreadyRatedException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}