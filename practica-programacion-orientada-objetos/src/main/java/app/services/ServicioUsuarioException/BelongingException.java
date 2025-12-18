package app.services.ServicioUsuarioException;

public class BelongingException extends RuntimeException {
    private static final String MENSAJE = "Problema de pertenencia";

    public BelongingException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
