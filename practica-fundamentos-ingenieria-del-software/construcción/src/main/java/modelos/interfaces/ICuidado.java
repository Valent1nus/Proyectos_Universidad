package modelos.interfaces;

import modelos.Cuidador;
import modelos.Mascota;

import java.util.Date;

public interface ICuidado {
    int getID();

    Cuidador getCuidador();
    Date getFechaInic();
    Date getFechaFin();
    Mascota getMascota();


}
