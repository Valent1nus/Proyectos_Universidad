/**
 *  REDES AVANZADAS
 *  ETSI SISTEMAS INFORMÁTICOS
 */

package sockets.main;

import java.io.IOException;
import sockets.cliente.Cliente;
import sockets.cliente.Cliente_img;


//Clase principal que hará uso del cliente
public class MainCliente
{
    public static void main(String[] args) throws IOException
    {
        Cliente_img cli = new Cliente_img(); //Se crea el cliente

        System.out.println("Iniciando cliente\n");
        cli.startClient(); //Se inicia el cliente
    }
}