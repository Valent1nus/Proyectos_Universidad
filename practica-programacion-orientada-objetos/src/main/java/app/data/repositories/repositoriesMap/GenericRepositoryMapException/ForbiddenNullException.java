package app.data.repositories.repositoriesMap.GenericRepositoryMapException;

public class ForbiddenNullException extends RuntimeException {
    private static final String MENSAJE = "El valor es null";

    public ForbiddenNullException(String mensaje) {
        super(MENSAJE + "; " + mensaje);
    }
}
