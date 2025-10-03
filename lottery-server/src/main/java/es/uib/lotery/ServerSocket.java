package es.uib.lotery;

import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.DNSServersRequestPacket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class ServerSocket {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ServerSocket <dnsHost> <dnsPort>");
            return;
        }

        String dnsHost = args[0];
        int dnsPort;
        try {
            dnsPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){
            dnsPort = 8080;
            System.out.println("Using default port 8080");
        }

        Server server = new Server();

        Thread serverThread = new Thread(() -> {
            try {
                server.startServer("localhost", 1234);
            } catch (Exception e) {
                server.disconnectServer();
                System.out.println("[SERVER] Error en el hilo: " + e.getMessage());
                e.printStackTrace();
            }
        });

        serverThread.setDaemon(false);
        serverThread.start();

        DNSServersRequestPacket packet = new DNSServersRequestPacket();
        if (server.connectClient(dnsHost, dnsPort)) {
            Random rand = new Random();

            while (true) {
                if (1 == rand.nextInt(10)) {
                    server.getSellers(packet);
                    Ticket ticket = server.sortear();

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

