/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sypev2server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Juaky
 */
public class SYPEv2Server {

    //Instanciamos la lista de InetAddress en la que guardaremos todas las InetAddress que nos envien mensaje
    public static List<InetAddress> inetAddressList = new ArrayList<InetAddress>();

    public static void main(String[] args) throws IOException {
        try {
            //Asocio el socket al puerto 5010
            DatagramSocket socket = new DatagramSocket(5010);
            //Declaramos un String en el que almacenaremos el mensaje que nos mande el cliente
            String mensaje;

            do {
                //Instanciamos un array de byte para recibir el datagrama
                byte[] bufer = new byte[1024];
                //Construyo el datagrama a recibir
                DatagramPacket recibo = new DatagramPacket(bufer, bufer.length);

                System.out.println("Esperando Datagrama .......... \n");

                //Recibo el datagrama por el socket
                socket.receive(recibo);
                //Obtengo el numero de bytes
                int bytesRec = recibo.getLength();
                //Obtengo el paquete en String
                String paquete = (new String(recibo.getData())).trim();
                //Obtengo el mensaje
                mensaje = paquete.split(": ")[1];
                //Obtengo la InetAddress
                InetAddress ip = recibo.getAddress();

                //Imprimimos información
                System.out.println("===================================");
                System.out.println("Número de Bytes recibidos: " + bytesRec);
                System.out.println("Contenido del Paquete: " + paquete);
                System.out.println("Puerto origen del mensaje: " + recibo.getPort());
                System.out.println("IP de origen: " + ip.getHostAddress());
                System.out.println("Puerto destino del mensaje: " + socket.getLocalPort());
                System.out.println("===================================");

                //Almaceno su InetAddress en la lista
                if (!inetAddressList.contains(ip)) {
                    inetAddressList.add(ip);
                }

                //Enviar el paquete a todas las direcciones de la lista al puerto 6011
                for (InetAddress ia : inetAddressList) {
                    System.out.println("Enviando a... " + ia + "\n");
                    //Construyo el datagrama a enviar
                    DatagramPacket envio = new DatagramPacket(paquete.getBytes(), paquete.getBytes().length, InetAddress.getByName(ia.getHostAddress()), 6011);
                    //Envio datagrama a destino y puerto
                    socket.send(envio);
                }
                //Se cerrara el servidor cuando le mandemos STOP
            } while (!mensaje.equals("STOP"));

            //Cerramos el socket
            socket.close();
            System.out.println("Servidor cerrado con éxito");
        } catch (SocketException ex) {
            Logger.getLogger(SYPEv2Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
