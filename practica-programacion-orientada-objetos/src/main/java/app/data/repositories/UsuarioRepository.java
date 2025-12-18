package app.data.repositories;

import app.data.models.Usuario;

import java.util.Optional;

public interface UsuarioRepository extends GenericRepository<Usuario> {
    Optional<Usuario> findByMobile(Integer movil);

    Optional<Usuario> findByName(String nombre);

}
