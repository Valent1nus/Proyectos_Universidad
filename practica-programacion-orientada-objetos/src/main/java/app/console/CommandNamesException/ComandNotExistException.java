package app.console.CommandNamesException;

public class ComandNotExistException extends RuntimeException {
    private static final String MENSAJE = "Comando invalido";

    public ComandNotExistException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
