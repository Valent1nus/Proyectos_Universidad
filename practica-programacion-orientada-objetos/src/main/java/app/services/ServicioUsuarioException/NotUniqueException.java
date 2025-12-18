package app.services.ServicioUsuarioException;

public class NotUniqueException extends RuntimeException {
    private static final String MENSAJE = "No es unico";

    public NotUniqueException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
