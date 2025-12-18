package controladores;

import modelos.Cuidado;
import modelos.interfaces.ICuidado;
import persistentes.IPersistentesGenerica;
import persistentes.PCuidado;
import vistas.VistaCuidado;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CCuidado {
    private final VistaCuidado vistaCuidado;

    public CCuidado() {
        this.vistaCuidado = new VistaCuidado();
    }

    public void crearCuidado(String[] datos){
        if(datos.length != 3){
            throw new RuntimeException("Numero de datos invÃ¡lidos");
        }else{
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date fechaInicio = null;
            Date fechaFin = null;
            try {
                fechaInicio = format.parse(datos[0]);
                fechaFin = format.parse(datos[1]);
                String cuidadorSSRR = null;
                String mascotaRIAC = datos[2];
                ICuidado iCuidado = new Cuidado(fechaInicio,fechaFin,cuidadorSSRR,mascotaRIAC);
                IPersistentesGenerica<ICuidado> persistencia = PCuidado.getInstance();
                persistencia.insertar("",iCuidado);
                this.vistaCuidado.altaCuidado(iCuidado);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
