package es.uib.lotery;

import es.uib.lotery.packet.DNSRequestPacket;
import es.uib.lotery.packet.SorteoRequestPacket;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.*;

/**
 * The {@code ClientSocket} class represents the entry point for running a
 * lottery client that repeatedly participates in lottery draws.
 * <p>
 * This class interacts with the DNS server to retrieve an available seller's
 * address and then periodically sends lottery requests ({@link SorteoRequestPacket})
 * to that seller using a {@link Client} instance.
 * </p>
 *
 * <p>Key features:</p>
 * <ul>
 *     <li>Connects to DNS to discover seller servers.</li>
 *     <li>Uses a scheduled executor to repeatedly attempt lottery requests.</li>
 *     <li>Logs successful lottery wins.</li>
 * </ul>
 *
 * <p>Typical usage (command line):</p>
 * <pre>{@code
 * java -jar lottery-client-socket.jar
 * }</pre>
 *
 * @see Client
 * @see DNSRequestPacket
 * @see SorteoRequestPacket
 */
public class ClientSocket {
    /** Logger used for tracking client events and wins. */
    private static final Logger logger = Logger.getLogger(ClientSocket.class.getName());

    /** Executor for scheduling periodic lottery requests. */
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    /**
     * Application entry point.
     * <p>
     * Starts a new {@code ClientSocket} instance and begins periodic lottery requests.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new ClientSocket().start();
    }

    /**
     * Starts the client workflow:
     * <ol>
     *     <li>Connects to the DNS server to retrieve a seller's address.</li>
     *     <li>Disconnects from the DNS server.</li>
     *     <li>Schedules repeated lottery requests to the seller using a random interval.</li>
     *     <li>Logs successful wins to the console.</li>
     * </ol>
     */
    private void start() {
        AtomicReference<InetSocketAddress> serverAddress = new AtomicReference<>();
        Client client = new Client();

        // Retrieve seller address from DNS
        if (client.connectToServer(DNS_HOST, DNS_PORT)) {
            DNSRequestPacket packet = new DNSRequestPacket();
            client.requestAddress(packet, serverAddress::set);
            client.disconnect();
        }

        final Random random = new Random();
        final InetSocketAddress address = serverAddress.get();

        // Schedule repeated lottery requests
        scheduler.scheduleAtFixedRate(() -> {
            if (client.connectToServer(address.getHostName(), address.getPort())) {
                int number = RANDOM_NUMBER.get();
                client.pedirSorteo(SorteoRequestPacket.builder()
                        .requestNumber(number)
                        .build());

                if (client.hasWin()) {
                    logger.config("Has ganado con el numero: %d".formatted(number));
                }

                client.disconnect();
            }
        }, 0, random.nextInt(CLIENT_RETRY_SECONDS), TimeUnit.SECONDS);
    }
}
