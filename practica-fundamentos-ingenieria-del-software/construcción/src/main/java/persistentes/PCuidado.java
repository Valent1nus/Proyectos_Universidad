package persistentes;

import modelos.CuidadoFactory;
import modelos.interfaces.ICuidado;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class PCuidado implements IPersistentesGenerica<ICuidado> {
    private static PCuidado instance;
    private final HashMap<Integer, ICuidado> cuidados;

    private PCuidado() {
        this.cuidados = new HashMap<>();
    }

    public static PCuidado getInstance() {
        if (instance == null) instance = new PCuidado();
        return instance;
    }

    private void leerCuidados(HashMap<Integer, ICuidado> lista) {
        CuidadoFactory cuidadoFactory = new CuidadoFactory();
        try (BufferedReader lector = new BufferedReader(new FileReader("docs\\persistencias\\cuidados.txt"))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] bloques = linea.split("_");
                if (bloques.length < 4) {
                    System.err.println("Línea mal formada: " + linea);
                    continue;
                }
                try {
                    Date fechaInicio = null;
                    Date fechaFin = null;
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    fechaInicio = format.parse(bloques[0]);
                    fechaFin = format.parse(bloques[1]);
                    String cuidadorSSRR = bloques[2];
                    String mascotaRIAC = bloques[3];
                    Integer id = Integer.valueOf(bloques[4]);
                    lista.put(id, cuidadoFactory.createCuidado(fechaInicio, fechaFin, cuidadorSSRR, mascotaRIAC));
                } catch (Exception e) {
                    System.err.println("Error procesando la línea: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    @Override
    public void insertar(String codigo, ICuidado dato) {
        try {
            CuidadoFactory cuidadoFactory = new CuidadoFactory();
            BufferedWriter escritor = new BufferedWriter(new FileWriter("docs\\persistencias\\cuidados.txt", true));
            escritor.write(dato.getFechaInic() + "_" + dato.getFechaFin() + "_" + dato.getCuidador().getId() + "_" + dato.getMascota().getRIAC() + "_" + dato.getID());
            escritor.newLine();
            escritor.close();
            this.cuidados.put(Integer.valueOf(codigo), cuidadoFactory.createCuidado(dato.getFechaInic(), dato.getFechaFin(), dato.getCuidador().getId(), dato.getMascota().getRIAC()));
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    @Override
    public ICuidado seleccionar(String codigo) {
        if (this.cuidados.containsKey(codigo)) {
            return this.cuidados.get(Integer.parseInt(codigo));
        }
        else return null;

    }

    @Override
    public void actualizar() {

    }

    @Override
    public void borrar(String codigo) {

    }

    @Override
    public void pasarFicherosAListas() {
        leerCuidados(this.cuidados);
    }
}
