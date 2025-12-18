package sockets.servidor;

import java.io.*;
import sockets.conexion.Conexion;

public class Servidor_img extends Conexion // Se hereda de conexión para hacer uso de los sockets y demás
{
    public Servidor_img() throws IOException {
        super("servidor"); // Se usa el constructor para servidor de Conexion
    }

    public void startServer() // Método para iniciar el servidor
    {
        try
        {
            System.out.println("Esperando..."); // Esperando conexión

            cs = ss.accept(); // Accept comienza el socket y espera una conexión desde un cliente

            System.out.println("Cliente en línea");

            // Se obtiene el flujo de entrada desde el cliente
            DataInputStream entradaCliente = new DataInputStream(cs.getInputStream());

            // Creamos un archivo donde vamos a guardar la imagen recibida
            File archivoDestino = new File("C:\\Users\\alumno\\Downloads\\socket\\servidor\\aaaa.png");
            try (FileOutputStream fos = new FileOutputStream(archivoDestino))
            {
                // Leemos el tamaño del archivo primero
                int tamañoArchivo = entradaCliente.readInt();

                // Creamos un buffer para almacenar los datos que vamos a recibir
                byte[] buffer = new byte[1024];
                int bytesLeidos;

                // Leemos los datos en bloques y los escribimos en el archivo
                while ((bytesLeidos = entradaCliente.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesLeidos);
                }

                System.out.println("Imagen recibida y guardada correctamente.");
            }
            catch (IOException e) {
                System.out.println("Error al guardar la imagen: " + e.getMessage());
            }

            System.out.println("Fin de la conexión");

            cs.close(); // Se finaliza la conexión con el cliente
            ss.close(); // Cerramos el socket del servidor
        }
        catch (Exception e)
        {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
