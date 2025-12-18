package app.services;

import app.data.models.Plan;
import app.data.models.Usuario;
import app.data.repositories.PlanRepository;
import app.data.repositories.UsuarioRepository;
import app.services.ServicesException.NotFoundException;
import app.services.ServicesException.OutOfDateException;
import app.services.ServicioUsuarioException.BelongingException;
import app.services.ServicioUsuarioException.IncorrectParameterException;
import app.services.ServicioUsuarioException.NotUniqueException;

import java.time.LocalDateTime;
import java.util.Optional;

public class ServicioUsuario {
    private final UsuarioRepository usuarioRepository;
    private final PlanRepository planRepository;

    public ServicioUsuario(UsuarioRepository usuarioRepository, PlanRepository planRepository) {
        this.usuarioRepository = usuarioRepository;
        this.planRepository = planRepository;
    }

    public Usuario crearUsuario(Usuario usuario) {
        validarMovilNoExistente(usuario.getMovil(), "El móvil ya existe, y debiera ser único: " + usuario.getMovil());
        validarNombreNoExistente(usuario.getNombre(), "El nombre ya existe, y debiera ser único: " + usuario.getNombre());
        return usuarioRepository.create(usuario);
    }

    public Usuario loginUsuario(String nombreUsuario, String contraseniaUsuario) {
        return usuarioRepository.findByName(nombreUsuario)
                .filter(usuario -> usuario.getContrasenia().equals(contraseniaUsuario))
                .orElseThrow(() -> new IncorrectParameterException("Introduzca correctamente el nombre del usuario y la contraseña"));
    }

    public Optional<Usuario> unirsePlan(Usuario usuario, Integer idPlan) {
        Plan plan = planRepository.findById(idPlan).orElseThrow(() -> new NotFoundException("El plan no existe"));
        if (plan.getFechaYHora().isBefore(LocalDateTime.now())) {
            throw new OutOfDateException("El plan ya se realizó y ya no puede inscribirse");
        }
        if (plan.getUsuariosSubscritos().contains(usuario)) {
            throw new BelongingException("El usuario ya pertenece a este plan");
        }
        boolean solapamiento = planRepository.planesSubscritosDelUsuario(usuario).stream()
                .anyMatch(p -> usuario.comprobarSolapamientoDosPlanes(p, plan));
        if (!solapamiento) {
            plan.aniadirParticipante(usuario);
            usuarioRepository.update(usuario);
            planRepository.update(plan);
            return Optional.of(usuario);
        }
        throw new OutOfDateException("El usuario tiene un plan a la misma hora");
    }

    public Optional<Usuario> abandonarPlan(Usuario usuario, Integer idPlan) {
        Plan plan = planRepository.findById(idPlan).orElseThrow(() -> new NotFoundException("El plan no existe"));

        if (!plan.getUsuariosSubscritos().contains(usuario)) {
            throw new BelongingException("El usuario no pertenecía a este plan");
        }
        if (plan.getFechaYHora().isBefore(LocalDateTime.now())) {
            throw new OutOfDateException("El plan ya se realizó y no puede abandonarlo");
        }
        plan.getUsuariosSubscritos().remove(usuario);
        usuarioRepository.update(usuario);
        planRepository.update(plan);
        return Optional.of(usuario);
    }

    public void verListaPlanesSubscritos(Usuario usuario) {
        planRepository.planesSubscritosDelUsuario(usuario).forEach(plan -> System.out.println(plan.toString()));
    }

    private void validarNombreNoExistente(String nombre, String mensajeError) {
        if (usuarioRepository.findByName(nombre).isPresent()) {
            throw new NotUniqueException(mensajeError);
        }
    }

    private void validarMovilNoExistente(Integer movil, String mensajeError) {
        if (usuarioRepository.findByMobile(movil).isPresent()) {
            throw new NotUniqueException(mensajeError);
        }
    }
}
