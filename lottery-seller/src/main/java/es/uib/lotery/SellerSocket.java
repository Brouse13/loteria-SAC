package es.uib.lotery;

import java.io.IOException;

public class SellerSocket {
    public static void main(String[] args) {
        if (args.length < 5) {
            System.out.println("Uso: java LotteryClient <dnsHost> <dnsPort> <serverHost> <serverPort> <sellerName>");
            return;
        }
        int serverPort = 0, dnsPort = 0;
        try {
            serverPort = Integer.parseInt(args[3]);
            dnsPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            System.out.println("Error parsing number: " + e.getMessage());
            System.exit(1);
        }

        String dnsHost = args[0];
        String serverHost = args[2];
        String sellerName = args[4];

        new SellerSocket().start(dnsHost, dnsPort, serverHost, serverPort, sellerName);
    }

    private void start(String dnsHost, int dnsPort, String serverHost, int serverPort, String sellerName) {
        Seller seller = new Seller(sellerName);

        // Connect DNS packet
        if (seller.connectClient(dnsHost, dnsPort)) {
            seller.connectDNS(serverHost, serverPort);
            seller.disconnectClient();
        }

        // Connect to server
        if (seller.connectClient(serverHost, serverPort)) {
            seller.requestSorteos();
            seller.disconnectClient();
        }

        // Start server (TODO ESTO NO TIENE QUE SER ESTE HOST Y PUERTO)
        try {
            seller.start("localhost", 1234);
        }catch (IOException e) {
            seller.disconnectServer();
        }

        // Disconnect DNS packet
        if (seller.connectClient(dnsHost, dnsPort)) {
            seller.disconnectDNS(serverHost, serverPort);
            seller.disconnectClient();
        }
    }
}
