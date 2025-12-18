package consola;

public class VistaCLI {

    public VistaCLI() {
    }

    public void mostrarBienvenida() {
        System.out.println("\n\nBienvenido a la aplicación cuidando a Pancho\n");
    }

    public void mostrarDespedida() {
        System.out.println("¡Hasta Luego!");
    }

    public void mostrarMenuInicio() {
        System.out.println("1) Iniciar sesión (login");
        System.out.println("2) Registrarse como Propietario");
        System.out.println("3) Registrarse como Cuidador");
        System.out.println("4) Cerrar aplicación");
        System.out.println("5) Mostrar lista de mascotas como propietario o lista de cuidados como cuidador");
        System.out.println("6) Dar de alta mascota");
        System.out.println("7) Dar de alta mascota exotica");
        System.out.println("8) Crear reserva de cuidado de mascota");
        System.out.println("9) Logout");
        System.out.print("Seleccione una de las siguientes opciones escribiendo el número correspondiente (1/2/3/4/5/6/7/8/9):");
    }


    public void registrarDatosPropietario() {
        System.out.println("Introduce los siguientes datos:");
    }

    public void registrarDatosCuidador() {
        System.out.println("Introduce los siguientes datos:"+"tarifa(numero mayor que cero);documentacion");
    }

    public void darAltaMascota() {
        System.out.println("Por favor, inserta los datos de la siguiente manera:" +
                "RIAC;nombre;edad;raza;especie;numero_de_poliza;ubicacion;horario\n" +
                "Inserte los datos:");
    }
    public void darAltaMascotaExotica() {
        System.out.println("Por favor, inserta los datos de la siguiente manera:" +
                "RIAC;nombre;edad;raza;especie;numero_de_poliza;ubicacion;horario(En formato:yyyy-MM-dd HH:mm:ss);documentacion_extra\n" +
                "Inserte los datos:");
    }

    public void mostrarListaMascotas() {
    }
    public void crearCuidado() {
        System.out.println("Por favor, inserta los datos de la siguiente manera: fechaInicio;fechaFin;RIACmascota");
    }

    public void mostrarListaCuidados() {

    }
}
