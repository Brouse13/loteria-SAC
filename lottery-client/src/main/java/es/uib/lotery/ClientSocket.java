package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SellerRequestPacket;

public class ClientSocket {
        public static void main(String[] args) {
            if (args.length < 3) {
                System.out.println("Uso: java LotteryClient <dnsHost> <dnsPort> <clientName>");
                return;
            }
            String dnsHost = args[0];
            int dnsPort = Integer.parseInt(args[1]);
            String clientName = args[2];

            Client c = new Client(clientName);
            DNSRequestPacket req = new DNSRequestPacket();
            if (c.connectToServer(dnsHost, dnsPort)) {
                c.DNSConnectionTicket(req);
                c.disconnect();
            }

            SellerRequestPacket buy = new SellerRequestPacket();
            if(c.connectToServer()) {
                c.recieveTicket(buy);
            }
        }
    }
