package persistentes;

import modelos.Cuidador;
import modelos.interfaces.ICuidador;

import java.io.*;
import java.util.HashMap;

public class PCuidador implements IPersistentesGenerica<ICuidador> {
    private static PCuidador instance;
    private final HashMap<String, ICuidador> cuidadores;

    private PCuidador() {
        this.cuidadores = new HashMap<>();
    }

    public static PCuidador getInstance() {
        if (instance == null) instance = new PCuidador();
        return instance;
    }

    public void pasarFicherosAListas() {
        leerCuidadores(this.cuidadores);
    }

    private void leerCuidadores(HashMap<String, ICuidador> lista) {
        try (BufferedReader lector = new BufferedReader(new FileReader("docs\\persistencias\\cuidadores.txt.txt"))) {
            String linea;
            while ((linea = lector.readLine()) != null) {
                String[] bloques = linea.split("_");
                if (bloques.length < 3) {
                    System.err.println("Línea mal formada: " + linea);
                    continue;
                }
                try {
                    String ide = bloques[0];
                    Double tarifa = Double.parseDouble(bloques[1]);
                    String documentacion = bloques[2];
                    lista.put(ide, new Cuidador(ide, tarifa, documentacion));
                } catch (Exception e) {
                    System.err.println("Error procesando la línea: " + linea);
                }
            }
        } catch (IOException e) {
            System.out.println("Error al leer el archivo: " + e.getMessage());
        }
    }

    @Override
    public void insertar(String codigo, ICuidador dato) {
            try {
                BufferedWriter escritor = new BufferedWriter(new FileWriter("docs\\persistencias\\cuidadores.txt.txt", true));
                escritor.newLine();
                this.cuidadores.put(codigo, new Cuidador(codigo, dato.getTarifa(), dato.getDocumentacion()));
                escritor.write(codigo + "_" + dato.getTarifa() + "_" + dato.getDocumentacion());
                escritor.close();
                this.cuidadores.put(codigo, new Cuidador(codigo, dato.getTarifa(), dato.getDocumentacion()));
            } catch (IOException e) {
                System.out.println("Error al leer el archivo: " + e.getMessage());
            }
    }

    @Override
    public void actualizar() {
        // Implementar la lógica de actualización aquí
    }

    @Override
    public ICuidador seleccionar(String SSRR) {
        if (this.cuidadores.containsKey(SSRR)) {
            return this.cuidadores.get(SSRR);
        }
        else return null;
    }

    @Override
    public void borrar(String codigo) {
        // Implementar la lógica de borrado aquí
    }
}
