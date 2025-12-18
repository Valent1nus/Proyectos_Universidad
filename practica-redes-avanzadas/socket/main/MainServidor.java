/**
 *  REDES AVANZADAS
 *  ETSI SISTEMAS INFORMÁTICOS
 */

package sockets.main;

import java.io.IOException;
import sockets.servidor.Servidor;
import sockets.servidor.Servidor_img;


//Clase principal que hará uso del servidor
public class MainServidor
{
    public static void main(String[] args) throws IOException
    {
        Servidor_img serv = new Servidor_img(); //Se crea el servidor

        System.out.println("Iniciando servidor\n");
        serv.startServer(); //Se inicia el servidor
    }
}
