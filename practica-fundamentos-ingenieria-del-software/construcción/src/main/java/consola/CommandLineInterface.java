package consola;

import controladores.*;

import java.util.Scanner;

import static controladores.CUsuario.getLogeado;

public class CommandLineInterface {
    public static final String DELIMETER_COLON_OR_RETURN = "[:,\\r\\n]";
    private static final String LOGIN = "1";
    private static final String REGISTRARPROPIETARIO = "2";
    private static final String REGISTRARCUIDADOR = "3";
    private static final String EXIT = "4";
    private static final String LOGOUT = "logout";
    private static final String LISTAR_MASCOTAS = "listar-mascotas";
    private static final String ALTA_MASCOTA = "alta-mascota";
    private static final String ALTA_MASCOTA_EXOTICA = "alta-mascota-exotica";
    private static final String RESERVA_CUIDADO_MASCOTA = "reservar-cuidado ";
    private static boolean esCuidador;
    private final CCuidado cCuidado;
    private final CCuidador cCuidador;
    private final CMascota cMascota;
    private final CUsuario cUsuario;
    private final CPropietario cPropietario;
    private final VistaCLI vistaCLI;

    public CommandLineInterface(CCuidador cCuidador, CMascota cMascota, CUsuario cUsuario, CPropietario cPropietario, CCuidado cCuidado, VistaCLI vistaCLI) {
        this.cCuidador = cCuidador;
        this.cMascota = cMascota;
        this.cUsuario = cUsuario;
        this.cPropietario = cPropietario;
        this.vistaCLI = vistaCLI;
        this.cCuidado = cCuidado;
    }

    public boolean runCommands() {
        this.vistaCLI.mostrarBienvenida();
        Scanner scanner = new Scanner(System.in).useDelimiter(DELIMETER_COLON_OR_RETURN);
        boolean exit;
        do {
            exit = runMenuInicio(scanner);
        } while (!exit);
        this.vistaCLI.mostrarDespedida();
        return true;
    }

    private boolean runMenuInicio(Scanner scanner) {


        this.vistaCLI.mostrarMenuInicio();

        String commandName = scanner.next();

        boolean exit = false;
        switch (commandName){
            case LOGIN:{
                esCuidador = this.cUsuario.login();
                break;
            }
            case LOGOUT:{
                this.cUsuario.setLogeado("");
                esCuidador = false;
                break;
            }
            case REGISTRARCUIDADOR:{
                if(getLogeado()==null) {
                    vistaCLI.registrarDatosCuidador();
                    cUsuario.loginFake();
                    String[] datos = scanner.next().split(";");
                    cCuidador.crear(datos);
                    break;
                }else throw new RuntimeException("Ya estas logedo con un usuario perteneciente a nuestra BBDD");
            }
            case REGISTRARPROPIETARIO:{
                if(getLogeado()==null) {
                    vistaCLI.registrarDatosPropietario();
                    cUsuario.loginFake();
                    cPropietario.crear();
                    break;
                }else throw new RuntimeException("Ya estas logedo con un usuario perteneciente a nuestra BBDD");
            }
            case EXIT:{
                exit = true;
                break;
            }
            case LISTAR_MASCOTAS:{
                if(getLogeado()!=null) {
                    if (esCuidador) {
                        vistaCLI.mostrarListaCuidados();
                        cCuidador.mostrarCuidados();
                    } else {
                        vistaCLI.mostrarListaMascotas();
                        cPropietario.mostrarMascotas();
                    }
                }else throw new RuntimeException("Debes iniciar sesion para acceder a este tipo de funciones");
            }
            case ALTA_MASCOTA:{
                if (!esCuidador) {
                    vistaCLI.darAltaMascota();
                    String[] datos = scanner.next().split(";");
                    cMascota.crearMascota(datos);
                    break;
                }else throw new RuntimeException("Debes de ser un propietario para aceder a esta funcion");
            }
            case ALTA_MASCOTA_EXOTICA:{
                if (!esCuidador) {
                    vistaCLI.darAltaMascotaExotica();
                    String[] datos = scanner.next().split(";");
                    cMascota.crearMascota(datos);
                    break;
                }else throw new RuntimeException("Debes de ser un propietario para aceder a esta funcion");
            }
            case RESERVA_CUIDADO_MASCOTA:{
                if(!esCuidador){
                    vistaCLI.crearCuidado();
                    String[] datos = scanner.next().split(";");
                    cCuidado.crearCuidado(datos);
                }
            }
        }
    return exit;
    }
}

