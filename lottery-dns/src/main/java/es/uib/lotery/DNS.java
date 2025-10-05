package es.uib.lotery;

/**
 * The {@code DNS} class serves as the entry point for launching the
 * {@link DNSSocket} server in the lottery network.
 * <p>
 * This class provides a minimal bootstrap that starts the DNS-like service,
 * which is responsible for registering and discovering seller servers.
 * </p>
 *
 * <p>Typical usage (command line):</p>
 * <pre>{@code
 * java -jar lottery-dns.jar
 * }</pre>
 *
 * <p>Once started, the DNS server listens for incoming registration and lookup
 * requests from sellers and clients on the configured host and port.</p>
 *
 * @author
 * @version 1.0
 */
public class DNS {

    /**
     * Application entry point.
     * <p>
     * Creates and starts the {@link DNSSocket} service. If an error occurs during startup,
     * the DNS server is stopped gracefully.
     * </p>
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new DNS().start();
    }

    /**
     * Starts the {@link DNSSocket} server that handles DNS-related functionality
     * such as registration and lookup of seller nodes.
     * <p>
     * Any exceptions during startup trigger a safe shutdown.
     * </p>
     */
    private void start() {
        DNSSocket dnsSocket = new DNSSocket();

        try {
            dnsSocket.start();
        } catch (Exception e) {
            dnsSocket.stop();
        }
    }
}
