package app.console.CommandLineInterfaceException;

public class CommandErrorException extends RuntimeException {
    private static final String MENSAJE = "Faltan valores";

    public CommandErrorException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
