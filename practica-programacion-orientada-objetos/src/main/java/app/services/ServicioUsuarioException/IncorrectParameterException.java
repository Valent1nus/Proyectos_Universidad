package app.services.ServicioUsuarioException;

public class IncorrectParameterException extends RuntimeException {
    private static final String MENSAJE = "El valor es incorrecto";

    public IncorrectParameterException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
