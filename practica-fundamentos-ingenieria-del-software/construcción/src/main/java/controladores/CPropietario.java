package controladores;


import modelos.Propietario;
import modelos.interfaces.ICuidador;
import modelos.interfaces.IPropietario;
import persistentes.IPersistentesGenerica;
import persistentes.PCuidador;
import persistentes.PPropietario;
import servidor.ExternalRRSS;
import vistas.VistaMascota;
import vistas.VistaPropietario;

public class CPropietario extends CUsuario {
    private final VistaPropietario vistaPropietario;
    private final VistaMascota vistaMascota;

    public CPropietario() {
        super();
        this.vistaPropietario = new VistaPropietario();
        this.vistaMascota = new VistaMascota();
    }

    public String registrarPropietario(IPropietario newPropietario) {
        if (getLogeado() != null) {
            IPersistentesGenerica<IPropietario> persistencia = PPropietario.getInstance();
            if (persistencia.seleccionar(getLogeado()) == null) {
                persistencia.insertar(getLogeado(), newPropietario);
                return vistaPropietario.crearPropietario(newPropietario);
            } else {
                throw new RuntimeException();
            }
        } else throw new IllegalArgumentException("Usuario no logeado");
    }

    public String crear() {
            IPropietario newPropietario = new Propietario(getLogeado());
            return registrarPropietario(newPropietario);
    }

    public void mostrarMascotas() {
        IPersistentesGenerica<IPropietario> persistencia = PPropietario.getInstance();
        IPropietario propietario = persistencia.seleccionar(getLogeado());
        this.vistaMascota.mostrarListado(propietario.listarMascotas());
    }
    public boolean esPropietario() {
        IPersistentesGenerica<IPropietario> persistencia = PPropietario.getInstance();
        return persistencia.seleccionar(getLogeado())!=null;
    }
}
