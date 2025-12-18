package vistas;

import modelos.interfaces.IPropietario;

public class VistaPropietario {
    public String listarMascotas(IPropietario propietario) {
        return cuerpoInicio(propietario) + "solicito un listado de sus mascotas:\n";
    }

    public String crearPropietario(IPropietario propietario) {
        return cuerpoInicio(propietario) + "creo un nuevo cuidado: ";
    }

    private String cuerpoInicio(IPropietario propietario) {
        System.out.println("El usuario \"" + propietario.getId() + "\" ha realizado la siguienta accion: ");
        return "El usuario \"" + propietario.getId() + "\" ha realizado la siguienta accion: ";
    }
}
