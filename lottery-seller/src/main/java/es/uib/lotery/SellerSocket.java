package es.uib.lotery;

import java.io.IOException;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

public class SellerSocket {
    private static final Logger logger = Logger.getLogger(SellerSocket.class.getName());
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar lottery-client.jar <sellerHost> <sellerPort> <sellerName>");
            return;
        }

        int sellerPort = 0;
        try {
            sellerPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e) {
            logger.warning("Error parsing port - %s ".formatted(args[1]));
            System.exit(1);
        }

        String sellerHost = args[0];
        String sellerName = args[2];

        new SellerSocket().start(sellerHost, sellerPort, sellerName);
    }

    private void start(String sellerHost, int sellerPort, String sellerName) {
        Seller seller = new Seller(sellerName);

        // Connect to DNS
        if (seller.connectClient(DNS_HOST, DNS_PORT)) {
            seller.connectDNS(sellerHost, sellerPort);
            seller.disconnectClient();
        }

        try {
            seller.start(sellerHost, sellerPort);
        }catch (IOException e) {
            seller.disconnectServer();
        }

        // Disconnect from DNS
        if (seller.connectClient(DNS_HOST, DNS_PORT)) {
            seller.disconnectDNS(sellerHost, sellerPort);
            seller.disconnectClient();
        }
    }
}
