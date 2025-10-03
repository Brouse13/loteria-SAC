package es.uib.lotery;

import es.uib.lotery.entity.Ticket;
import es.uib.lotery.packet.DNSServersRequestPacket;
import es.uib.lotery.packet.SorteosRequestPacket;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Random;

public class ServerSocket {
    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java ServerSocket <dnsHost> <dnsPort>");
        }


        Server server = new Server();
        try{
            if(server.connectServer()){
                System.out.println("Server established");
                Thread serverThread = new Thread(() -> {
                    try {
                        server.listen();
                    } catch (Exception e) {
                        System.out.println("[SERVER] Error en el hilo: " + e.getMessage());
                        e.printStackTrace();
                    }
                });

                serverThread.setDaemon(false);
                serverThread.start();
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        server.setDNSAddress(new InetSocketAddress(args[0], Integer.parseInt(args[1])));
        DNSServersRequestPacket packet = new DNSServersRequestPacket();
        if(server.connectClient()){
            Random rand = new Random();
            while(true){
                if (1 == rand.nextInt(10)){
                    server.getSellers(packet);
                    Ticket ticket = server.sortear();

                    List<InetSocketAddress> sellers = server.getSellersAddress();

                    for(InetSocketAddress seller : sellers){
                        //broadcast
                        //FALTA HACER EL PAQUETE QUE HA DE MANDAR Y EL HANDLER PARA EL SELLER
                        //TAMBIÉN TENGO QUE HACER EL HANDLER DEL SERVER SOCKET PARA LOS PAQUETES DEL SELLER
                    }

                }
                if (2 == rand.nextInt(10)){
                    server.crearSorteo();
                }
            }
        }

    }
}

