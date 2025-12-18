package persistentes;

public interface IPersistentesGenerica<T> {
    void insertar(String codigo, T dato);

    T seleccionar(String codigo);

    void actualizar();

    void borrar(String codigo);

    void pasarFicherosAListas();
}
