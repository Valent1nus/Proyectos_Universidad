package app.services.ServicioPlanException;

public class NotParticipantException extends RuntimeException {
    private static final String MENSAJE = "No ha participado en este plan";

    public NotParticipantException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}