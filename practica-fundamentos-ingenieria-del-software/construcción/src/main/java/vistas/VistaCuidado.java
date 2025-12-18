package vistas;

import modelos.interfaces.ICuidado;

import java.util.List;

public class VistaCuidado {
    private static final String SEPARADOR = "-------------------------------------------------------";

    public VistaCuidado() {
    }

    public void altaCuidado(ICuidado cuidado) {
        System.out.println("El cuidado \"" + cuidado.getID() + "\" " + "se ha creado con exito");
    }

    public void mostrarListadoCuidados(List<ICuidado> cuidados) {
        StringBuilder str = new StringBuilder("Mascotas a listar:" + "\n");
        for (ICuidado cuidado : cuidados) {
            str.append(SEPARADOR).append("\n");
            str.append("\t").append("Nombre: ");
            str.append(cuidado.getCuidador().getId()).append("\n");
        }
        str.append(SEPARADOR).append("\n");
        System.out.println(str);
    }
}
