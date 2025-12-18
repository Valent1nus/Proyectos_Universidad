package sockets.cliente;

import java.io.*;
import sockets.conexion.Conexion;

public class Cliente_img extends Conexion
{
    public Cliente_img() throws IOException{
        super("cliente");
    }

    public void startClient() //Método para iniciar el cliente
    {
        try
        {
            File archivo = new File("C:\\Users\\alumno\\Downloads\\socket\\cliente\\imagen.png");
            byte[] datosImagen = new byte[(int) archivo.length()];

            try (FileInputStream fis = new FileInputStream(archivo)) {
                fis.read(datosImagen);
            } catch (IOException e) {
                System.out.println("ERROR al leer el archivo PNG: " + e.getMessage());
                return;
            }

            salidaServidor = new DataOutputStream(cs.getOutputStream());
            salidaServidor.writeInt(datosImagen.length);
            salidaServidor.write(datosImagen);

            cs.close(); // Fin de la conexión
        }
        catch (Exception e)
        {
            System.out.println(e.getMessage());
        }
    }
}
