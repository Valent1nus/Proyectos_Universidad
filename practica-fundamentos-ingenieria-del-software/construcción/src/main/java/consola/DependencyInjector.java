package consola;

import controladores.*;
import persistentes.*;

public class DependencyInjector {
    private static DependencyInjector instance;
    private final CommandLineInterface commandLineInterface;
    private final VistaCLI vistaCLI;
    private final CCuidador cCuidador;
    private final CMascota cMascota;
    private final CUsuario cUsuario;
    private final CPropietario cPropietario;
    private final CCuidado cCuidado;
    private final IPersistentesGenerica pCuidador;
    private final IPersistentesGenerica pMascota;
    private final IPersistentesGenerica pMascotaExotica;
    private final IPersistentesGenerica pPropietario;
    private final IPersistentesGenerica pCuidado;


    private DependencyInjector() {
        this.pMascota = PMascota.getInstance();
        pMascota.pasarFicherosAListas();

        this.pMascotaExotica = PMascota_exotica.getInstance();
        pMascotaExotica.pasarFicherosAListas();

        this.pCuidador = PCuidador.getInstance();
        pCuidador.pasarFicherosAListas();

        this.pPropietario = PPropietario.getInstance();
        pPropietario.pasarFicherosAListas();

        this.pCuidado = PCuidado.getInstance();
        pCuidado.pasarFicherosAListas();
        this.vistaCLI = new VistaCLI();
        this.cCuidador = new CCuidador();
        this.cMascota = new CMascota();
        this.cUsuario = new CUsuario();
        this.cCuidado = new CCuidado();
        this.cPropietario = new CPropietario();
        this.commandLineInterface = new CommandLineInterface(this.cCuidador, this.cMascota, this.cUsuario, this.cPropietario,this.cCuidado ,this.vistaCLI);
    }

    public static synchronized DependencyInjector getInstance() {
        if (instance == null) {
            instance = new DependencyInjector();
        }
        return instance;
    }

    public void run() {
        this.commandLineInterface.runCommands();
    }

}

