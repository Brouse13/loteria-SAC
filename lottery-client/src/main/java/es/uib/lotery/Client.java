package es.uib.lotery;

import es.uib.lotery.connection.BaseClientConnection;
import es.uib.lotery.packet.*;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.function.Consumer;

/**
 * The {@code Client} class represents an object-oriented lottery client
 * that interacts with the DNS and Seller servers to participate in lottery draws.
 * <p>
 * It uses {@link BaseClientConnection} to manage low-level socket communication,
 * sending and receiving packets to retrieve available sellers and request lottery results.
 * </p>
 *
 * <p>Typical usage:</p>
 * <pre>{@code
 * Client client = new Client();
 * if (client.connectToServer("localhost", 8080)) {
 *     client.pedirSorteo(new SorteoRequestPacket(123));
 *     if (client.hasWin()) {
 *         System.out.println("You won!");
 *     }
 *     client.disconnect();
 * }
 * }</pre>
 *
 * <p><b>Note:</b> This class handles packets asynchronously using callbacks
 * registered via lambda expressions.</p>
 *
 * @see BaseClientConnection
 * @see DNSRequestPacket
 * @see SorteoRequestPacket
 * @see SorteoResponsePacket
 */
@Getter
@Setter
public class Client {

    /** Underlying client connection that handles socket-level communication. */
    private final BaseClientConnection connection = new BaseClientConnection();

    /** Flag indicating whether the client has won the current draw. */
    private boolean win = false;

    /**
     * Connects the client to a given server host and port.
     *
     * @param host the hostname or IP address of the server
     * @param port the TCP port number to connect to
     * @return {@code true} if the connection was successfully established,
     *         {@code false} otherwise
     */
    public boolean connectToServer(String host, int port) {
        return connection.connect(new InetSocketAddress(host, port));
    }

    /**
     * Disconnects the client from the server.
     */
    public void disconnect() {
        connection.disconnect();
    }

    /**
     * Sends a {@link DNSRequestPacket} to the DNS server to request
     * an available seller's address.
     * <p>
     * The DNS server responds with a {@link DNSResponsePacket},
     * and the provided {@link Consumer} callback is executed with
     * the received {@link InetSocketAddress}.
     * </p>
     *
     * @param dnsRequest the DNS request packet
     * @param address a consumer callback that receives the seller's address
     */
    public void requestAddress(DNSRequestPacket dnsRequest, Consumer<InetSocketAddress> address) {
        connection.send(dnsRequest, (response) -> {
            if (!(response instanceof DNSResponsePacket)) return;
            address.accept(((DNSResponsePacket) response).getAddress());
        });
    }

    /**
     * Sends a {@link SorteoRequestPacket} to a seller server to participate
     * in a lottery draw.
     * <p>
     * When the {@link SorteoResponsePacket} is received, the client updates
     * its internal {@code win} flag accordingly.
     * </p>
     *
     * @param requestPacket the lottery request packet
     */
    public void pedirSorteo(SorteoRequestPacket requestPacket) {
        win = false;

        connection.send(requestPacket, (packet) -> {
            if (!(packet instanceof SorteoResponsePacket)) return;

            if (((SorteoResponsePacket) packet).isWin()) {
                win = true;
            }
        });
    }

    /**
     * Returns whether the client has won the latest draw.
     *
     * @return {@code true} if the client won, {@code false} otherwise
     */
    public boolean hasWin() {
        return win;
    }
}
