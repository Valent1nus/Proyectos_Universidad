package app.console.DateBeforeNowException;

public class DateBeforeNowException extends RuntimeException {
    private static final String MENSAJE = "No puedes crear planes pasados";

    public DateBeforeNowException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
