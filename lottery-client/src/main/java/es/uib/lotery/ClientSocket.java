package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SellerRequestPacket;

import java.util.Random;

public class ClientSocket {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java LotteryClient <dnsHost> <dnsPort> <clientName>");
            return;
        }

        String dnsHost = args[0];
        String clientName = args[2];
        int dnsPort;
        try {
            dnsPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            dnsPort = 8080;
            System.out.println("Using default dns port: " + dnsPort);
        }

        new ClientSocket().start(dnsHost, dnsPort, clientName);
    }

    private void start(String dnsHost, int dnsPort, String clientName) {
        Client client = new Client(clientName);

        if (client.connectToServer(dnsHost, dnsPort)) {
            DNSRequestPacket packet = DNSRequestPacket.builder().serverName("server1").build();
            client.DNSConnectionTicket(packet);
            client.disconnect();
        }
        
        while (true){
            Random random = new Random();
            if (1 == random.nextInt(10)) {
                if (client.connectToServer()) {
                    SellerRequestPacket packet = SellerRequestPacket.builder().build();
                    client.receiveTicket(packet);
                    client.disconnect();
                }
                client.listTickets();
            }
        }
    }
}
