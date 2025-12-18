package modelos;

import persistentes.IPersistentesGenerica;
import persistentes.PCuidador;
import persistentes.PMascota;
import persistentes.PMascota_exotica;

import java.util.Date;

public class CuidadoFactory {
    private final IPersistentesGenerica pCuidador;
    private final IPersistentesGenerica pMascota;
    private final IPersistentesGenerica pMascota_exotica;

    public CuidadoFactory() {
        this.pCuidador = PCuidador.getInstance();
        this.pMascota = PMascota.getInstance();
        this.pMascota_exotica = PMascota_exotica.getInstance();
    }

    public Cuidado createCuidado(Date fechainicio, Date fechaFin, String cuidadorSSRR, String mascotaRIAC) {
        Cuidado cuidado = new Cuidado(fechainicio, fechaFin);
        try {
            Cuidador cuidador = (Cuidador) pCuidador.seleccionar(cuidadorSSRR);
            cuidado.setCuidador(cuidador);
            cuidado.setTarifa(cuidador.getTarifa());
        } catch (Exception e) {
            System.err.println("Error seleccionando cuidador: " + cuidadorSSRR);
            e.printStackTrace();
        }
        try {
            Mascota mascota = (Mascota) pMascota.seleccionar(mascotaRIAC);
            MascotaExotica mascotaExotica = (MascotaExotica) pMascota_exotica.seleccionar(mascotaRIAC);
            if (mascota != null) {
                cuidado.setMascota(mascota);
            } else if (mascotaExotica != null) {
                cuidado.setMascota(mascotaExotica);
            }
        } catch (Exception e) {
            System.err.println("Error seleccionando mascota: " + mascotaRIAC);
            e.printStackTrace();
        }
        return cuidado;
    }
}
