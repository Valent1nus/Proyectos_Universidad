package app.data.repositories.repositoriesMap;

import app.data.models.Usuario;
import app.data.repositories.UsuarioRepository;

import java.util.Optional;

public class UsuarioRepositoryMap extends GenericRepositoryMap<Usuario> implements UsuarioRepository {

    @Override
    protected Integer getId(Usuario entity) {
        return entity.getId();
    }

    @Override
    protected void setId(Usuario entity, Integer id) {
        entity.setId(id);
    }

    @Override
    public Optional<Usuario> findByMobile(Integer movil) {
        for (Usuario usuario : this.findAll()) {
            if (usuario.getMovil().equals(movil)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }

    public Optional<Usuario> findByName(String nombre) {
        for (Usuario usuario : this.findAll()) {
            if (usuario.getNombre().equals(nombre)) {
                return Optional.of(usuario);
            }
        }
        return Optional.empty();
    }


}
