package es.uib.lotery;

import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.DNSServersRequestPacket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

import static es.uib.lotery.utils.Constants.*;

public class ServerSocket {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ServerSocket");
            return;
        }

        Server server = new Server();

        Thread serverThread = new Thread(() -> {
            try {
                server.startServer(SERVER_HOST, SERVER_PORT);
            } catch (Exception e) {
                server.disconnectServer();
                System.out.println("[SERVER] Error en el hilo: " + e.getMessage());
            }
        });

        serverThread.setDaemon(false);
        serverThread.start();

        DNSServersRequestPacket packet = new DNSServersRequestPacket();
        if (server.connectClient(DNS_HOST, DNS_PORT)) {
            Random rand = new Random();

            while (true) {
                if (1 == rand.nextInt(10)) {
                    server.getSellers(packet);
                    Ticket ticket = server.sortear(1);

                    List<InetSocketAddress> sellers = server.getSellersAddress();

                    for (InetSocketAddress seller : sellers) {
                        //broadcast
                        //FALTA HACER EL PAQUETE QUE HA DE MANDAR Y EL HANDLER PARA EL SELLER
                        //TAMBIÉN TENGO QUE HACER EL HANDLER DEL SERVER SOCKET PARA LOS PAQUETES DEL SELLER
                    }

                }
                if (2 == rand.nextInt(10)) {
                    server.crearSorteo();
                }
            }
        }
    }
}

