package app.services.ServicesException;

public class NotFoundException extends RuntimeException {
    private static final String MENSAJE = "Lo que busca no existe";

    public NotFoundException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
