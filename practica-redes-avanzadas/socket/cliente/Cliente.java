/**
 *  REDES AVANZADAS
 *  ETSI SISTEMAS INFORMÁTICOS
 */

package sockets.cliente;

import java.io.*;

import sockets.conexion.Conexion;

public class Cliente extends Conexion
{
    public Cliente() throws IOException{super("cliente");} //Se usa el constructor para cliente de Conexion

    public void startClient() //Método para iniciar el cliente
    {
        try
        {
           /*
           //Flujo de datos hacia el servidor
            salidaServidor = new DataOutputStream(cs.getOutputStream());

            //Se enviarán dos mensajes
            for (int i = 0; i < 2; i++)
            {
                //Se escribe en el servidor usando su flujo de datos
                salidaServidor.writeUTF("Este es el mensaje número " + (i+1) + "\n");
            }
            */

            BufferedWriter outputWriter = null;
            outputWriter = new BufferedWriter(new FileWriter("C:\\Users\\alumno\\Downloads\\socket\\cliente\\aaaa.txt"));
            outputWriter.write("hola que tal como estas");
            outputWriter.newLine();
            outputWriter.flush();
            outputWriter.close();
            String[] bloques = new String[0];
            salidaServidor = new DataOutputStream(cs.getOutputStream());

            try (BufferedReader lector = new BufferedReader(new FileReader("C:\\Users\\alumno\\Downloads\\socket\\cliente\\aaaa.txt"))){
                String linea;
                while ((linea = lector.readLine()) != null){
                    bloques = linea.split(" ");
                }

                for(String palabra: bloques){
                    salidaServidor.writeUTF(palabra + "\n");
                }
            }
            catch (IOException e){
                System.out.println("ERROR: " + e.getMessage());
            }
            cs.close();//Fin de la conexión

        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}