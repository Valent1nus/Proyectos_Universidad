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

/** Test de registro Cuidador **/
public class CajaNegra {
    private static CCuidador controladora;
    private static CUsuario cUsuario;
    private static String[] test0;
    private static String[] test1;
    private static String[] test2;
    private static String id;

    @BeforeAll
    public static void inicioClase() {
        controladora = new CCuidador();
        cUsuario = new CUsuario();
        test0 = new String[]{"20","POV: soy un documento"};
        test1 = new String[]{"20",""};
        test2 = new String[]{"-5","POV: soy un documento"};
        id = ExternalRRSS.LoginRRSS();
        cUsuario.setLogeado(id);
    }
    @Test
    public void CP0(){
        String test = controladora.crear(test0);
        Assertions.assertEquals("Cuidador \""+id+"\" creado con exito: ",test);
    }
    @Test
    public void CP1(){
        boolean intento = false;
        try {
            String test = controladora.crear(test1);
        }catch (IllegalArgumentException ex){
            intento = true;
        }Assertions.assertTrue(intento);
    }
    @Test
    public void CP2(){
        boolean intento = false;
        try {
            String test = controladora.crear(test2);
        }catch (IllegalArgumentException ex){
            intento = true;
        }Assertions.assertTrue(intento);
    }

}
