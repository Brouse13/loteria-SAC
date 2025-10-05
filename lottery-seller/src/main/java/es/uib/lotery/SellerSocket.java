package es.uib.lotery;

import java.io.IOException;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

/**
 * The {@code SellerSocket} class is the main entry point for starting a lottery seller client.
 * <p>
 * It connects to a DNS service to register and unregister the seller, and then
 * starts the seller server to handle requests.
 * </p>
 *
 * <p><b>Usage:</b></p>
 * <pre>
 *     java -jar lottery-client.jar &lt;sellerHost&gt; &lt;sellerPort&gt; &lt;sellerName&gt;
 * </pre>
 *
 * Example:
 * <pre>
 *     java -jar lottery-client.jar localhost 8080 Seller1
 * </pre>
 */
public class SellerSocket {
    /** Logger for the SellerSocket class. */
    private static final Logger logger = Logger.getLogger(SellerSocket.class.getName());

    /**
     * The entry point of the seller client application.
     *
     * @param args command-line arguments:
     *             <ol>
     *                 <li>{@code sellerHost} - The host name or IP address of the seller server.</li>
     *                 <li>{@code sellerPort} - The TCP port number for the seller server.</li>
     *                 <li>{@code sellerName} - The name identifying the seller.</li>
     *             </ol>
     */
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("Usage: java -jar lottery-client.jar <sellerHost> <sellerPort> <sellerName>");
            return;
        }

        int sellerPort = 0;
        try {
            sellerPort = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            logger.warning("Error parsing port - %s ".formatted(args[1]));
            System.exit(1);
        }

        String sellerHost = args[0];
        String sellerName = args[2];

        new SellerSocket().start(sellerHost, sellerPort, sellerName);
    }

    /**
     * Starts the seller service by connecting to the DNS, registering the seller,
     * and launching the seller server.
     *
     * @param sellerHost the hostname or IP address of the seller
     * @param sellerPort the port number of the seller
     * @param sellerName the name identifying the seller
     */
    private void start(String sellerHost, int sellerPort, String sellerName) {
        Seller seller = new Seller(sellerName);

        // Connect to DNS and register
        if (seller.connectClient(DNS_HOST, DNS_PORT)) {
            seller.connectDNS(sellerHost, sellerPort);
            seller.disconnectClient();
        }

        // Start the seller server
        try {
            seller.start(sellerHost, sellerPort);
        } catch (IOException e) {
            seller.disconnectServer();
        }

        // Disconnect from DNS (unregister)
        if (seller.connectClient(DNS_HOST, DNS_PORT)) {
            seller.disconnectDNS(sellerHost, sellerPort);
            seller.disconnectClient();
        }
    }
}
