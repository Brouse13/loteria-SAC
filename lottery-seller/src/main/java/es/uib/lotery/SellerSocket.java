package es.uib.lotery;

import java.io.IOException;

import static es.uib.lotery.utils.Constants.*;

public class SellerSocket {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Uso: java LotteryClient <sellerHost> <sellerPort> <sellerName>");
            return;
        }
        int sellerPort = 0;
        try {
            sellerPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
            System.exit(1);
        }

        String sellerHost = args[0];
        String sellerName = args[2];

        new SellerSocket().start(sellerHost, sellerPort, sellerName);
    }

    private void start(String sellerHost, int sellerPort, String sellerName) {
        Seller seller = new Seller(sellerName);

        // Connect DNS packet
        if (seller.connectClient(DNS_HOST, DNS_PORT)) {
            seller.connectDNS(sellerHost, sellerPort);
            seller.disconnectClient();
        }

        // Connect to server
        if (seller.connectClient(SERVER_HOST, SERVER_PORT)) {
            seller.requestSorteos();
            seller.disconnectClient();
        }

        try {
            seller.start(sellerHost, sellerPort);
        }catch (IOException e) {
            seller.disconnectServer();
        } finally {
            if (seller.connectClient(DNS_HOST, DNS_PORT)) {
                seller.disconnectDNS(sellerHost, sellerPort);
                seller.disconnectClient();
            }
        }
    }
}
