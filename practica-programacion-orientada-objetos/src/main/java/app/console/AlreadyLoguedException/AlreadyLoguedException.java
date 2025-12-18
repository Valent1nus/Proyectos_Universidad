package app.console.AlreadyLoguedException;

public class AlreadyLoguedException extends RuntimeException {
    private static final String MENSAJE = "Ya hay un usuario logueado";

    public AlreadyLoguedException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}


