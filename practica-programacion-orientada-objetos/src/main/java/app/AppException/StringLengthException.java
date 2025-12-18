package app.AppException;

public class StringLengthException extends RuntimeException {
    private static final String MENSAJE = "Cantidad de caracteres invalida";

    public StringLengthException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
