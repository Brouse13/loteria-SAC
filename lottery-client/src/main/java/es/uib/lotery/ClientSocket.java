package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SellerRequestPacket;

import java.util.Random;

import static es.uib.lotery.utils.Constants.DNS_HOST;
import static es.uib.lotery.utils.Constants.DNS_PORT;

public class ClientSocket {
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Uso: java LotteryClient <clientName>");
            return;
        }

        new ClientSocket().start(args[0]);
    }

    private void start(String clientName) {
        Client client = new Client(clientName);

        if (client.connectToServer(DNS_HOST, DNS_PORT)) {
            DNSRequestPacket packet = DNSRequestPacket.builder().serverName("server").build();
            client.DNSConnectionTicket(packet);
            client.disconnect();
        }

        while (true) {
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
