import controladores.CCuidador;
import controladores.CUsuario;
import modelos.Cuidador;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import persistentes.IPersistentesGenerica;
import servidor.ExternalRRSS;
import vistas.VistaCuidado;
import vistas.VistaCuidador;

public class CajaBlanca {
    private static CUsuario cUsuario;
    private static CCuidador controladora;
    private static IPersistentesGenerica<Cuidador> persistencia;
    private static VistaCuidador vista1;
    private static VistaCuidado vista2;
    private static String[] test0;
    private static String[] test1;
    private static String id;

    @BeforeAll
    public static void inicioClase() {
        controladora = new CCuidador();
        cUsuario = new CUsuario();
        test0 = new String[]{"20","POV: soy un documento"};
        test1 = new String[]{"20",""};
    }
    @Test
    public void CP0(){
        id = ExternalRRSS.LoginRRSS();
        cUsuario.setLogeado(id);
        String test = controladora.crear(test0);
        Assertions.assertEquals("Cuidador \""+id+"\" creado con exito: ",test);
    }
    @Test
    public void CP1(){
        boolean intento = false;
        id = ExternalRRSS.LoginRRSS();
        cUsuario.setLogeado(id);
        try {
            String test = controladora.crear(test1);
        }catch (IllegalArgumentException ex){
            intento = true;
        }
        Assertions.assertTrue(intento);
    }
    @Test
    public void CP3(){
        id = ExternalRRSS.LoginRRSS();
        cUsuario.setLogeado(id);
        String test = controladora.crear(test0);
        id = ExternalRRSS.LoginRRSS();
        cUsuario.setLogeado(id);
        boolean intento = false;
        try {
            String test2 = controladora.crear(test0);
        }catch (RuntimeException ex){
            intento = true;
        }Assertions.assertTrue(intento);
    }

}
