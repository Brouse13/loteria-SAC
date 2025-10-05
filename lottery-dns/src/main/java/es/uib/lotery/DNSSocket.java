package es.uib.lotery;

import es.uib.lotery.connection.BaseServerConnection;
import es.uib.lotery.connection.ServerConnection;
import es.uib.lotery.packet.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static es.uib.lotery.utils.Constants.DNS_HOST;
import static es.uib.lotery.utils.Constants.DNS_PORT;

/**
 * The {@code DNSSocket} class represents a lightweight DNS-like service used
 * within the lottery system for service discovery and registration.
 * <p>
 * It listens for incoming packets from servers and clients, handling two main types:
 * </p>
 * <ul>
 *   <li>{@link DNSConnectPacket} - registers or unregisters seller servers.</li>
 *   <li>{@link DNSRequestPacket} - provides a random available seller address
 *       to a client or controller that requests one.</li>
 * </ul>
 *
 * <p>The DNS server maintains an internal registry of currently available sellers.
 * When a client requests a connection, the DNS server selects one of the registered
 * sellers at random and returns its address.</p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * DNSSocket dns = new DNSSocket();
 * dns.start();   // Start DNS service on configured host/port
 * }</pre>
 *
 * @author
 * @version 1.0
 */
public class DNSSocket {
    /** Random number generator used to select servers randomly. */
    private final Random random = new Random();

    /** The underlying server connection that handles incoming DNS packets. */
    private final ServerConnection serverConnection;

    /** A concurrent map storing the registered seller servers by name. */
    private final Map<String, InetSocketAddress> serversNames = new ConcurrentHashMap<>();

    /**
     * Creates and initializes a new {@code DNSSocket} instance.
     * <p>
     * The constructor registers handlers for both {@link DNSRequestPacket}
     * and {@link DNSConnectPacket} packet types.
     * </p>
     */
    public DNSSocket() {
        PacketHandleRegistry registry = new PacketHandleRegistry();
        this.serverConnection = new BaseServerConnection(registry);

        registry.registerHandler(DNSRequestPacket.class, this::handleDNS);
        registry.registerHandler(DNSConnectPacket.class, this::handleConnection);
    }

    /**
     * Starts the DNS server on the configured {DNS_HOST} and {DNS_PORT}.
     *
     * @throws IOException if the server fails to bind to the socket or start listening
     */
    public void start() throws IOException {
        serverConnection.start(new InetSocketAddress(DNS_HOST, DNS_PORT));
        serverConnection.listen();
    }

    /**
     * Stops the DNS server gracefully, closing all active connections.
     */
    public void stop() {
        serverConnection.stop();
    }

    /**
     * Handles incoming {@link DNSRequestPacket}s by returning a random registered
     * seller’s address (if any are available).
     *
     * @param packet the DNS request packet
     * @return a list containing a single {@link DNSResponsePacket} with the selected seller address,
     *         or an empty response if no sellers are currently registered
     */
    private List<BasePacket> handleDNS(DNSRequestPacket packet) {
        DNSResponsePacket.DNSResponsePacketBuilder builder = DNSResponsePacket.builder();

        InetSocketAddress address = getRandomServer();
        if (address == null) {
            return List.of(builder.build());
        }

        return List.of(builder.address(address).build());
    }

    /**
     * Handles incoming {@link DNSConnectPacket}s, which either register or
     * unregister seller servers based on the packet type.
     *
     * @param packet the DNS connect or disconnect packet
     * @return an empty list (no response required)
     */
    private List<BasePacket> handleConnection(DNSConnectPacket packet) {
        switch (packet.getType()) {
            case CONNECT -> serversNames.put(packet.getServerName(), packet.getAddress());
            case DISCONNECT -> serversNames.remove(packet.getServerName());
        }

        return List.of();
    }

    /**
     * Selects and returns a random registered seller’s {@link InetSocketAddress}.
     * <p>
     * If no sellers are currently registered, this method returns {@code null}.
     * </p>
     *
     * @return a random seller’s address, or {@code null} if none are available
     */
    private InetSocketAddress getRandomServer() {
        return serversNames.values().stream()
                .skip(random.nextInt(serversNames.size()))
                .findFirst()
                .orElse(null);
    }
}
