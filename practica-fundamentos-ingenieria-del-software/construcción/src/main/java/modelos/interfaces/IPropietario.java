package modelos.interfaces;

import java.util.List;

public interface IPropietario extends IUsuario {

    List<IMascota> listarMascotas();
}
