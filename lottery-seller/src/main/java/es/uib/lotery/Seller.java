package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.packet.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.*;
import java.util.logging.Logger;

import static es.uib.lotery.utils.Constants.SERVER_HOST;
import static es.uib.lotery.utils.Constants.SERVER_PORT;

/**
 * Represents a lottery seller node responsible for:
 * <ul>
 *   <li>Registering and unregistering itself with a DNS service.</li>
 *   <li>Handling client requests for lottery draws (sorteos).</li>
 *   <li>Communicating with the main lottery server to process requests.</li>
 * </ul>
 *
 * <p>This class encapsulates both a server connection (to receive requests)
 * and a client connection (to communicate with other services such as DNS and the lottery server).</p>
 *
 * <p>Typical usage example:</p>
 * <pre>
 *     Seller seller = new Seller("Seller1");
 *     seller.start("localhost", 9090);
 * </pre>
 *
 * @author
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
public class Seller {
    /** Logger instance for the Seller class. */
    private static final Logger logger = Logger.getLogger(Seller.class.getName());

    /** The name identifying this seller. */
    private final String sellerName;

    /** Server-side connection used to listen for incoming requests. */
    private final BaseServerConnection connectionServer;

    /** Client-side connection used to communicate with DNS or the main lottery server. */
    private final BaseClientConnection connectionClient = new BaseClientConnection();

    /**
     * Creates a new {@code Seller} instance with a given seller name and
     * registers the request handler for lottery draw requests.
     *
     * @param sellerName the name identifying this seller
     */
    public Seller(String sellerName) {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.sellerName = sellerName;
        this.connectionServer = new BaseServerConnection(registry);

        registry.registerHandler(SorteoRequestPacket.class, this::requestSorteo);
    }

    /**
     * Starts the seller's server to accept incoming connections and requests.
     *
     * @param host the hostname or IP address to bind the server
     * @param port the port number to listen on
     * @throws IOException if the server fails to start
     */
    public void start(String host, int port) throws IOException {
        connectionServer.start(new InetSocketAddress(host, port));
        connectionServer.listen();
    }

    /**
     * Stops the seller server gracefully, closing all active connections.
     */
    public void disconnectServer() {
        this.connectionServer.stop();
    }

    /**
     * Establishes a client connection to the specified host and port.
     *
     * @param host the hostname or IP address to connect to
     * @param port the TCP port to connect to
     * @return {@code true} if the connection was established successfully, {@code false} otherwise
     */
    public boolean connectClient(String host, int port) {
        return this.connectionClient.connect(new InetSocketAddress(host, port));
    }

    /**
     * Disconnects the active client connection.
     */
    public void disconnectClient() {
        connectionClient.disconnect();
    }

    /**
     * Sends a request to the DNS service to register this seller as an available node.
     *
     * @param host the host where the seller server is running
     * @param port the port where the seller server is listening
     */
    public void connectDNS(String host, int port) {
        DNSConnectPacket request = DNSConnectPacket.builder()
                .address(new InetSocketAddress(host, port))
                .serverName(sellerName)
                .type(DNSConnectPacket.Type.CONNECT)
                .build();

        connectionClient.send(request, null);
    }

    /**
     * Sends a request to the DNS service to unregister this seller.
     *
     * @param host the host where the seller server is running
     * @param port the port where the seller server is listening
     */
    public void disconnectDNS(String host, int port) {
        DNSConnectPacket request = DNSConnectPacket.builder()
                .serverName(sellerName)
                .type(DNSConnectPacket.Type.DISCONNECT)
                .address(new InetSocketAddress(host, port))
                .build();

        connectionClient.send(request, null);
    }

    /**
     * Handles incoming lottery draw requests from clients.
     * <p>
     * This method:
     * <ul>
     *   <li>Connects to the main lottery server.</li>
     *   <li>Sends the {@link SorteoRequestPacket} request.</li>
     *   <li>Waits asynchronously for a {@link SorteoResponsePacket} response.</li>
     *   <li>Returns the response back to the requester.</li>
     * </ul>
     *
     * @param requestPacket the incoming lottery request
     * @return a list containing a single {@link SorteoResponsePacket} with the result of the draw
     */
    private List<BasePacket> requestSorteo(SorteoRequestPacket requestPacket) {
        CompletableFuture<Boolean> hasWin = new CompletableFuture<>();

        if (!connectionClient.connect(new InetSocketAddress(SERVER_HOST, SERVER_PORT))) return List.of();

        connectionClient.send(requestPacket, response -> {
            if (!(response instanceof SorteoResponsePacket packet)) return;

            boolean win = packet.isWin();
            hasWin.complete(win);

            logger.config("HasWin - %b with number - %d".formatted(packet.isWin(), requestPacket.getRequestNumber()));
        });

        Boolean result = false;
        try {
            result = hasWin.get(5, TimeUnit.SECONDS);
        } catch (TimeoutException | CancellationException ignore) {
            // The server is busy or unresponsive, respond as failed.
        } catch (Exception e) {
            logger.warning(e.getMessage());
        }

        connectionClient.disconnect();

        return List.of(SorteoResponsePacket.builder().win(result).build());
    }
}
