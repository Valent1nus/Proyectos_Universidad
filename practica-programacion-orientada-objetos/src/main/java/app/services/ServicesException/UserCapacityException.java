package app.services.ServicesException;

public class UserCapacityException extends RuntimeException {
    private static final String MENSAJE = "Hay un problema con la capacidad";

    public UserCapacityException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
